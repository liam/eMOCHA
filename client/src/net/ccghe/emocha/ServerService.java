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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.ccghe.emocha.model.DBAdapter;
import net.ccghe.emocha.model.Preferences;
import net.ccghe.utils.PostData;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServerService extends Service {
	private static final int ONE_SECOND = 1000;
	private static final long TIMER_INTERVAL = 30 * ONE_SECOND;
	private Timer timer = new Timer();

	@Override
	public void onCreate() {
		super.onCreate();

		Preferences.init(this);
		DBAdapter.init(this);
		
		startService();
		Log.i("EMOCHA", "Start SERVICE");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		stopService();
		
		Preferences.destroy();
		DBAdapter.destroy();
		
		Log.i("EMOCHA", "Stop SERVICE");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private TimerTask doRefresh = new TimerTask() {
		public void run() {
			Thread tThread = new Thread(null, doSlowStuff, "Background");
			tThread.start();
		}
	};
	private Runnable doSlowStuff = new Runnable() {
		public void run() {
			String tURL = Preferences.getServerURL();
			
			if (tURL == null) {
				return;
			}
			
	        List<NameValuePair> tPostData = new ArrayList<NameValuePair>(2);   	
	        tPostData.add(new BasicNameValuePair("usr", Preferences.getIMEI_hash()));
	        tPostData.add(new BasicNameValuePair("pwd", Preferences.getPassword()));
	        
	        tPostData.add(new BasicNameValuePair("cmd", "doSomething"));
	        tPostData.add(new BasicNameValuePair("param1", "now"));	        
	        //tPostData.add(new BasicNameValuePair("xml", new String(tData, "UTF-8")));
	        
			String tResponse = PostData.Send(tPostData, tURL);

			Log.i("EMOCHA", "Service called server. Response is: " + tResponse);			
		}
	};
		
	private void startService() {
		timer.scheduleAtFixedRate(doRefresh, 0, TIMER_INTERVAL);
	}

	private void stopService() {
		if (timer != null){
			timer.cancel();
		}
	}
	
}
