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
package net.ccghe.emocha.model;

import java.io.UnsupportedEncodingException;

import net.ccghe.emocha.Constants;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.twmacinta.util.MD5;

public class Preferences {
    public static final String PREF_SERVER_URL = "pref_server_url";
    public static final String PREF_PASSWORD = "pref_api_password";
    public static final String PREF_NET_ACTIVE = "pref_net_active";
    public static final String LAST_DOWNLOAD_TS = "pref_sys_last_server_upd";
    public static final String LAST_UPLOAD_TS = "pref_sys_last_upload_ts";
    public static final String PREF_LAST_GPS_POS = "pref_sys_last_gps_pos";

    private static String imei;
    private static String user;

    private static SharedPreferences sPrefs = null;

    public static void init(Context context) {
	if (sPrefs == null) {
	    sPrefs = PreferenceManager.getDefaultSharedPreferences(context);

	    // Get the hashed IMEI code
	    TelephonyManager phoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    imei = phoneManager.getDeviceId();

	    MD5 md5 = new MD5();
	    try {
		md5.Update(imei, null);
		user = md5.asHex();
	    } catch (UnsupportedEncodingException e) {
		user = "";
	    }
	}
    }

    public static void destroy() {
	sPrefs = null;
    }
	
    public static String getServerURL(Context context) {
	init(context);
	String tURL = sPrefs.getString(PREF_SERVER_URL, "");
	return tURL.length() < Constants.SERVER_URL_MIN_LENGTH ? null : tURL;
    }

    public static String getPassword(Context context) {
	init(context);
	return sPrefs.getString(PREF_PASSWORD, "");
    }

    public static String getImei(Context context) {
	init(context);
	return imei;
    }

    public static String getUser(Context context) {
	init(context);
	return user;
    }

    public static Boolean getNetworkActive(Context context) {
	init(context);
	return sPrefs.getBoolean(PREF_NET_ACTIVE, false);
    }

    public static String getLastDownloadTimestamp(Context context) {
	init(context);
	return sPrefs.getString(LAST_DOWNLOAD_TS, "0");
    }

    public static void setLastDownloadTimestamp(String ts, Context context) {
	init(context);
	SharedPreferences.Editor editor = sPrefs.edit();
	editor.putString(LAST_DOWNLOAD_TS, ts);
	editor.commit();
    }

    public static Long getLastUploadTimestamp(Context context) {
	init(context);
	return sPrefs.getLong(LAST_UPLOAD_TS, 0);
    }

    public static void setLastUploadTimestamp(Long ts, Context context) {
	init(context);
	SharedPreferences.Editor editor = sPrefs.edit();
	editor.putLong(LAST_UPLOAD_TS, ts);
	editor.commit();
    }

    public static String getGpsPos(Context context) {
	init(context);
	return sPrefs.getString(PREF_LAST_GPS_POS, ".");
    }

    public static void setGpsPos(String pos, Context context) {
	init(context);
	SharedPreferences.Editor editor = sPrefs.edit();
	editor.putString(PREF_LAST_GPS_POS, pos);
	editor.commit();
    }

    public static boolean hasBasicSettings(Context context) {
	String tPwd = getPassword(context);
	return getServerURL(context) != null && tPwd.length() >= 4;
    }
}
