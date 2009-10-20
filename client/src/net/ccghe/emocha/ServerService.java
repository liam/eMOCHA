/*******************************************************************************
 * eMOCHA - electronic Mobile Open-Source Comprehensive Health Application
 * Copyright (c) 2009 Abe Pazos - abe@ccghe.net
 * 
 * This file is part of eMOCHA.
 * 
 * eMOCHA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * eMOCHA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.ccghe.emocha;

import java.util.Timer;
import java.util.TimerTask;

import net.ccghe.emocha.model.DBAdapter;
import net.ccghe.emocha.model.Preferences;
import net.ccghe.utils.FileUtils;
import net.ccghe.utils.Server;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class ServerService extends Service {
	private static final long INTERVAL_CALL_SERVER 		= 30 * Constants.ONE_SECOND;
	private static final long INTERVAL_DOWNLOAD 		= 5 * Constants.ONE_SECOND;
	private static FileTransmitter fileTransmitter;
	private Timer serverTimer = new Timer();
	private Handler handler = new Handler();
		    
	@Override
	public void onCreate() {
		super.onCreate();

		DBAdapter.init(this);
		Server.init(this);
		
		fileTransmitter = new FileTransmitter();
		
	    serverTimer.schedule(onServerTimer, 0, INTERVAL_CALL_SERVER);
	    
	    Log.i("EMOCHA", "Start Service. Network " + 
	    		(Preferences.getNetworkActive(ServerService.this) ? "ON" : "OFF"));
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (serverTimer != null) {
			serverTimer.cancel();
		}
		fileTransmitter.stop(handler, transmitLoop);		
		Preferences.destroy();
		DBAdapter.destroy();
		
		Log.i("EMOCHA", "Stop SERVICE");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	private TimerTask onServerTimer = new TimerTask() {
	    public void run() {
	    	Log.i(Constants.LOG_TAG, "tick");
	    	if(Preferences.getNetworkActive(ServerService.this.getApplicationContext())) {
				new Thread(null, serverThread, "ServerThread").start();	    		
	    	}
		}
	};
	
	private Runnable serverThread = new Runnable() {
		public void run() {
            if (DBAdapter.pendingFileTransfersNum() == 0) {
    			Context c = ServerService.this.getApplicationContext();

    			if (fileTransmitter.isRunning()) {
    				fileTransmitter.stop(handler, transmitLoop);
    			}

    			// Build a list of files that should be sent to the server
            	Long newestTimestamp = DBAdapter.insertFilesNewerThan(Preferences.getLastUploadTimestamp(c), 
            			FileUtils.getFilesAsArrayListRecursive(Constants.PATH_ODK_DATA));
            	Preferences.setLastUploadTimestamp(newestTimestamp, c);
            	
            	// Call the server, maybe get back a list of files to download 
            	String lastDownloadTimestamp = Preferences.getLastDownloadTimestamp(c);
    	        JSONObject response = Server.GetSdcardFileList(lastDownloadTimestamp);

    			try {
    				//DBAdapter.updateFilesToUpload(lastLocalTimestamp);

    				String lastServerUpd = response.getString("last_server_upd");    				
    				if (lastDownloadTimestamp.equals(lastServerUpd)) {
    					Log.i("EMOCHA", "Server TS unchanged. Nothing to download.");
    				} else {
    					DBAdapter.updateFromJSON(response.getJSONArray("files"));
        				Preferences.setLastDownloadTimestamp(lastServerUpd, c);

    					Log.i("EMOCHA", "Server TS changed. Requests written in database.");
    				}
    			} catch (JSONException e) {
    				Log.e("EMOCHA", "json exception while parsing server response");			
    			}            	
            } else if (!fileTransmitter.isRunning()) {
				Log.i("EMOCHA", "Transfers found in database. Start Transmitter.");
				fileTransmitter.start(handler, transmitLoop);
            }
		}
	};	

	public class FileTransmitter {
		public final int UPLOAD = 1;
		public final int DOWNLOAD = 2;
		private Boolean isActive 		= false;
		private Boolean isTransmitting 	= false;
		private String mFilePath;
		private int mDirection;
		
		public void start(Handler h, Runnable r) {
			isActive = true;
			h.removeCallbacks(r);
			h.postDelayed(r, INTERVAL_DOWNLOAD);
		}
		public void stop(Handler h, Runnable r) {
			isActive = false;
			h.removeCallbacks(r);
		}
		public Boolean isRunning() {
			return isActive;
		}
		public void transmitPrepare(String path, int direction) {
			isTransmitting = true;
			mFilePath = path;
			mDirection = direction;
			Log.i(Constants.LOG_TAG, "TRANSMIT prepare: " + mFilePath);			
		}
		public void transmitBegin() {
			Log.i(Constants.LOG_TAG, "TRANSMIT begin: " + mFilePath);
			switch (mDirection) {
			case DOWNLOAD:
				Server.DownloadFile(mFilePath, this);
				break;
			case UPLOAD:
				Server.UploadFile(mFilePath, this);
				break;
			}
		}
		public void transmitComplete() {
			isTransmitting = false;
			Log.i(Constants.LOG_TAG, "TRANSMIT complete: " + mFilePath);			
		}
	}

	private Runnable transmitLoop = new Runnable() {
		public void run() {
        	if (fileTransmitter.isTransmitting) {
            	Log.w(Constants.LOG_TAG, "Sending / receiving files...");	            		
			} else {
				Context c = ServerService.this.getApplicationContext();
				if (Preferences.getNetworkActive(c)) {
					String downloadPath = DBAdapter.getFirstDownloadID();
					if (downloadPath != null) {
						fileTransmitter.transmitPrepare(downloadPath, fileTransmitter.DOWNLOAD);
						new Thread(null, fileTransmitThread, "FileTransmitThread").start();
					} else {
						String uploadPath = DBAdapter.getFirstUploadID();
						if (uploadPath != null) {
							fileTransmitter.transmitPrepare(uploadPath, fileTransmitter.UPLOAD);
							new Thread(null, fileTransmitThread, "FileTransmitThread").start();
						}
					}
				}
        	}
			handler.postDelayed(this, INTERVAL_DOWNLOAD);        	
		}
	};
	private Runnable fileTransmitThread = new Runnable() {
		public void run() {
			fileTransmitter.transmitBegin();
		}
	};
}
