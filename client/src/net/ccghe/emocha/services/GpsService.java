package net.ccghe.emocha.services;

import java.io.IOException;

import net.ccghe.emocha.Constants;
import net.ccghe.utils.FileLogUtil;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GpsService extends Service {

    public static final String      INTENT_FILTER    = "net.ccghe.emocha.GPS_LOCATION_UPDATE";

//    private static final String     LONGITUDE        = "lng";
//    private static final String     LATTITUDE        = "lat";
//    private static final String     TIME             = "time";
    private static final int        MAX_FIX_ATTEMPS  = 6;
    private static final String     GPS_PROVIDER_ID  = LocationManager.GPS_PROVIDER;
    private static final long       GPS_UPDATE_MS    = 30 * Constants.ONE_SECOND;

    private LocationManager         mLocationManager = null;
    private Location                mLocation;
    private LocationListener        mLocationListener;
    private Boolean                 mPendingUpdate   = false;
    
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	@Override
	public void onReceive(Context context, Intent intent) {
	    updateGps();
	}
    };

    @Override
    public void onDestroy() {
        this.unregisterReceiver(mReceiver);
        setGps(null);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(GpsService.INTENT_FILTER);
        this.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mLocationManager = (LocationManager) this.getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new DeviceLocationListener();
        mLocation = mLocationManager.getLastKnownLocation(GPS_PROVIDER_ID);
    }
    /**
     * @param loc   Can be set to null, so that we force a gps fixing attempt on the next call of
     *              updateGps, discarding the time passed.
     */
    private void setGps(Location loc) {
        mLocationManager.removeUpdates(mLocationListener);
        mLocation = loc;
        mPendingUpdate = false;
    }

    private void updateGps() {
	if (!mPendingUpdate) {
	    Long timestamp = (mLocation == null) ? -1 : System.currentTimeMillis() - mLocation.getTime();

	    if (timestamp == -1 || timestamp > GPS_UPDATE_MS) {
		Log.i("EMOCHA", "Start a gps update listener");
		mLocationManager.requestLocationUpdates(GPS_PROVIDER_ID, 0, 0, mLocationListener);
		mPendingUpdate = true;
	    }
	}

    }

    public class DeviceLocationListener implements LocationListener {
	private int mFixAttempts = 0;

	public DeviceLocationListener() {
	    appendLog(" DeviceLocationListener inistantiated ");
	}

	public void onLocationChanged(Location location) {
	    double lat = location.getLatitude();
	    double lng = location.getLongitude();

	    appendLog(" loc : [ " + lat + ":" + lng + " ]\n");
	    onGpsResult(location);
	}

	private void appendLog(String msg) {
	    try {
		FileLogUtil.appendToLog("gpsdata.txt", msg);
	    } catch (IOException e) {
	    }
	}

	public void onProviderDisabled(String provider) {
	    onGpsResult(null);
	    appendLog(" onProviderDisabled");
	}

	public void onProviderEnabled(String provider) {
	    appendLog(" onProviderEnabled");
	    updateGps();
	}

	private void onGpsResult(Location location) {
	    mFixAttempts = 0;
	    setGps(location);
	    Log.i("EMOCHA", "Gps result " + ((location == null) ? "null" : "location is set"));
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	    String stat = "undefined";

	    switch (status) {
	    case LocationProvider.AVAILABLE:
		stat = "AVAILABLE";
		break;
	    case LocationProvider.OUT_OF_SERVICE:
		stat = "OUT_OF_SERVICE";
		onGpsResult(null);
		break;
	    case LocationProvider.TEMPORARILY_UNAVAILABLE:
		stat = "TEMPORARILY_UNAVAILABLE";
		Log.w("EMOCHA", "attempt to fix gps " + mFixAttempts);
		if (mFixAttempts++ >= MAX_FIX_ATTEMPS) {
		    onGpsResult(null);
		    appendLog(" Provider: " + provider + " could not get a fix because status: " + stat);

		}
		break;
	    }
	}

    }

    @Override
    public IBinder onBind(Intent intent) {
	return null;
    }
}
