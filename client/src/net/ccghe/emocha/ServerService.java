package net.ccghe.emocha;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
		
		startService();
		Log.i("ABE", "Start SERVICE");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		stopService();
		Log.i("ABE", "Stop SERVICE");
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

			Log.i("ABE", "Service called server. Response is: " + tResponse);								
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
