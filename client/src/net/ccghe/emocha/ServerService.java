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

import net.ccghe.emocha.async.DownloadOneFile;
import net.ccghe.emocha.model.DBAdapter;
import net.ccghe.emocha.model.Preferences;
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
	private static Downloader downloader;
	private Timer serverTimer = new Timer();
	private Handler handler = new Handler();
		    
	@Override
	public void onCreate() {
		super.onCreate();

		DBAdapter.init(this);
		Server.init(this);
		
		downloader = new Downloader();
		
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
		downloader.stop(handler, downloadLoop);		
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
            if (DBAdapter.pendingDownloadNum() == 0) {
    			Context c = ServerService.this.getApplicationContext();

    			if (downloader.isRunning()) {
    				downloader.stop(handler, downloadLoop);
    			}
    			
            	// Call the server, maybe get back a list of files to download 
            	String localFileListTimestamp = Preferences.getLastServerUpdateTS(c);
    	        JSONObject response = Server.GetSdcardFileList(localFileListTimestamp);

    			try {
    				String timestamp = response.getString("last_server_upd");
    				if (localFileListTimestamp.equals(timestamp)) {
    					Log.i("EMOCHA", "Server TS unchanged. Nothing to do.");
    				} else {
    					DBAdapter.updateFromJSON(response.getJSONArray("files"));
        				Preferences.setLastServerUpdateTS(timestamp, c);

    					Log.i("EMOCHA", "Server TS changed. Requests written in database.");
    				}
    			} catch (JSONException e) {
    				Log.e("EMOCHA", "json exception while parsing server response");			
    			}            	
            } else if (!downloader.isRunning()) {
				Log.i("EMOCHA", "Downloads found in database. Start downloader.");
				downloader.start(handler, downloadLoop);
            }
		}
	};	

	public class Downloader {
		private Boolean isActive 		= false;
		private Boolean isDownloading 	= false;
		private String mFilePath;
		private String mServerURL;
		
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
		public void downloadPrepare(String path, String serverURL) {
			isDownloading = true;
			mFilePath = path;
			mServerURL = serverURL;
			Log.i(Constants.LOG_TAG, "DOWNLOAD PREPARE: " + mFilePath);			
		}
		public void downloadBegin() {
			Log.i(Constants.LOG_TAG, "DOWNLOAD begin: " + mFilePath);			
			new DownloadOneFile(mFilePath, mServerURL, this);			
		}
		public void downloadComplete() {
			isDownloading = false;
			Log.i(Constants.LOG_TAG, "DOWNLOAD complete: " + mFilePath);			
		}
	}

	private Runnable downloadLoop = new Runnable() {
		public void run() {
        	if (downloader.isDownloading) {
            	Log.w(Constants.LOG_TAG, "Downloading...");	            		
			} else {
				Context c = ServerService.this.getApplicationContext();
				if (Preferences.getNetworkActive(c)) {
					String filePath = DBAdapter.getFirstDownloadID();
					if (filePath != null) {
						String serverURL = Preferences.getServerURL(c);

						downloader.downloadPrepare(filePath, serverURL);
						new Thread(null, downloadThread, "DownloadThread").start();	    		
					}
				}
        	}
			handler.postDelayed(this, INTERVAL_DOWNLOAD);        	
		}
	};
	private Runnable downloadThread = new Runnable() {
		public void run() {
			downloader.downloadBegin();
		}
	};
}
