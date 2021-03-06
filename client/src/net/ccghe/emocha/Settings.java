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

import net.ccghe.emocha.model.Preferences;
import net.ccghe.utils.Server;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	PreferenceScreen screen;
	SharedPreferences pref;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);

		screen = getPreferenceScreen();
		pref = screen.getSharedPreferences();
		
		if (Preferences.getServerURL(this) == null) {
			Toast.makeText(getApplicationContext(), "Please enter valid server URL", Toast.LENGTH_SHORT).show();	
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		pref.unregisterOnSharedPreferenceChangeListener(this);    
	}

	@Override
	protected void onResume() {
		super.onResume();
		update();
	    pref.registerOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		update();
		// TODO: Settings should not know about PostData, but PostData can not listen to prefchange event...
		Server.init(this);
	}
	private void update() {
		screen.findPreference(Preferences.PREF_SERVER_URL).
			setSummary(pref.getString(Preferences.PREF_SERVER_URL, "(not set)"));			
		screen.findPreference(Preferences.PREF_PASSWORD).
			setSummary(pref.getString(Preferences.PREF_PASSWORD, "(not set)"));			
		
	}

	
}
