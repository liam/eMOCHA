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

import net.ccghe.utils.MD5;

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
