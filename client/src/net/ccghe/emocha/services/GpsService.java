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
/**
 * 
 * @author jiri
 * This service is not a service class in the strict sense that it runs by itself in the background.
 * This class registers itself to an intent filter so that we can call it to update the gps position.
 * When this service services the intent it checks if the timestamp
 * of the last location to the current system time. If the difference is greater then GPS_UPDATE_MS it attaches
 * a listener to the gps. 
 * In case of poor reception of the GPS and therefor no fix can be made the service will try MAX_FIX_ATTEMPS to get a fix. If it does
 * not succeed it sets the gps location to null. This forces the services to do another check, despite the time difference being smaller
 * then GPS_UPDATE_MS, when it receives an intent to update the gps.
 */
public class GpsService extends Service {

    public static final String      INTENT_FILTER    = "net.ccghe.emocha.GPS_LOCATION_UPDATE";
    
    private static final int        MAX_FIX_ATTEMPS  = 3;
    private static final String     GPS_PROVIDER_ID  = LocationManager.GPS_PROVIDER;
    private static final long       GPS_UPDATE_MS    = 60 * Constants.ONE_SECOND;
    private static final boolean    LOG_TO_TEXT      = true;

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
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        
        IntentFilter intentFilter = new IntentFilter(GpsService.INTENT_FILTER);
        this.registerReceiver(mReceiver, intentFilter);
        
        mLocationManager = (LocationManager) this.getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new DeviceLocationListener();
        mLocation = mLocationManager.getLastKnownLocation(GPS_PROVIDER_ID);
    }

    /**
     * @param   loc    Can be set to null, so that we force a gps fixing attempt on
     *                 the next call of updateGps, discarding the time passed.
     */
    private void setGps(Location loc) {
        mLocationManager.removeUpdates(mLocationListener);
        mLocation = loc;
        mPendingUpdate = false;
    }

    private void updateGps() {
        if (!mPendingUpdate) {
            Long timestamp = (mLocation == null) ? -1 : System.currentTimeMillis()- mLocation.getTime();

            if (timestamp == -1 || timestamp > GPS_UPDATE_MS) {
                Log.i( getClass().getSimpleName() , "Start a gps update listener");
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
            if(!LOG_TO_TEXT) return;
            
            try {
                FileLogUtil.appendToLog("gpsdata.txt", msg);
            } catch (IOException ignore) {}
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
            Log.i(getClass().getSimpleName(), "Gps result " + ((location == null) ? "null" : "location is set"));
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
                Log.w(getClass().getSimpleName(), "attempt to fix gps " + mFixAttempts);
                if (mFixAttempts++ >= MAX_FIX_ATTEMPS) {
                    onGpsResult(null);
                    appendLog(" Provider: " + provider  + " could not get a fix because status: " + stat);

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
