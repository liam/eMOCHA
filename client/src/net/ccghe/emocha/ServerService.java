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
import net.ccghe.utils.PostData;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServerService extends Service {
	private static final long TIMER_INTERVAL = 60 * Constants.ONE_SECOND;
	private Timer timer = new Timer();
	
    //private final ExecutorService threadExecutor = Executors.newFixedThreadPool(1);
    
	@Override
	public void onCreate() {
		super.onCreate();

		DBAdapter.init(this);
		PostData.init(this);
		
	    timer.scheduleAtFixedRate(onTimer, 0, TIMER_INTERVAL);

	    Log.i("EMOCHA", "Start Service. Is network allowed? " + 
	    		Boolean.toString( Preferences.getNetworkActive(ServerService.this) ) );
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (timer != null){
			timer.cancel();
		}
		
		Preferences.destroy();
		DBAdapter.destroy();
		
		Log.i("EMOCHA", "Stop SERVICE");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
		
	private TimerTask onTimer = new TimerTask() {
	    public void run() {
			Thread thread = new Thread(null, slowThread, "ServerService");
			thread.start();
		}
	};
	private Runnable slowThread = new Runnable() {
		public void run() {
			Context c = ServerService.this.getApplicationContext();
            if(Preferences.getNetworkActive(c) ) {
            	String lastServerUpdL = Preferences.getLastServerUpdateTS(c);
    	        JSONObject response = PostData.GetSdcardFileList(lastServerUpdL);

    			try {
    				String lastServerUpdS = response.getString("last_server_upd");
    				if (lastServerUpdL.equals(lastServerUpdS)) {
    					Log.i("EMOCHA", "SDCARD FILES UP TO DATE");
    				} else {
    					Log.i("EMOCHA", "SDCARD FILES NEED UPDATING");  
    					Preferences.setLastServerUpdateTS(lastServerUpdS, c);
    				}
    			} catch (JSONException e) {
    				Log.e("EMOCHA", "json exception while parsing server response");			
    			}
    	        
            	// 1. get file list from server (sending usr, pwd, and lastTS)
            	// 2. if we get something, insert into database a copy of the list
            	// 3. compare list to file system. delete unwanted files. 
            	//    mark changed and new files for download.
            	// 4. when each file is downloaded, compare md5 and mark it as downloaded.
    			
			    //threadExecutor.execute(new DownloadFileGroup("yes"));                    
			}			
		}
	};	
}
