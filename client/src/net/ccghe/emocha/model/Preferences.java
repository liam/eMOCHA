package net.ccghe.emocha.model;

import org.javarosa.core.util.MD5;

import net.ccghe.emocha.Constants;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class Preferences {
	public static final String PREF_SERVER_URL = "pref_server_url";
	public static final String PREF_PASSWORD   = "pref_api_password";

	private static String pImeiHash;

	private static SharedPreferences sPrefs = null;
	
	public static void init(Context tContext) {
		if (sPrefs == null) {
			sPrefs = PreferenceManager.getDefaultSharedPreferences(tContext);
		}
		
		// Get the hashed IMEI code
        TelephonyManager tPhoneManager = (TelephonyManager) tContext.getSystemService(Context.TELEPHONY_SERVICE);
        pImeiHash = new String(MD5.hash(tPhoneManager.getDeviceId().getBytes())); 
	}
	public static void destroy() {
		sPrefs = null;
	}
	
	public static String getServerURL() {
		String tURL = sPrefs.getString(PREF_SERVER_URL, ""); 
		return tURL.length() < Constants.SERVER_URL_MIN_LENGTH ? null : tURL;
	}
	public static String getPassword() {
		return sPrefs.getString(PREF_PASSWORD, "");
	}
	public static String getIMEI_hash() {
		return pImeiHash;
	}

	public static boolean hasBasicSettings() {
		String tPwd = getPassword();
		return getServerURL() != null && tPwd.length() >= 4;
	}
}
