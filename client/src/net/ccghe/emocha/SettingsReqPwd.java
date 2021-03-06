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

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SettingsReqPwd extends Activity {
	private TextView responseText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.settings_add_phone);
		
		responseText = (TextView) findViewById(R.id.SettingsAddPhoneResponse);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		String url = Preferences.getServerURL(this); 
		
		if (url.length() < Constants.SERVER_URL_MIN_LENGTH) {
			responseText.setText(R.string.settings_serverURL_undefined);			
		} else {		
	        JSONObject response = Server.activatePhone(Preferences.getImei(this));
			
			try {
				String msg = response.getString("msg");
				responseText.setText(msg);			
			} catch (JSONException e) {
				responseText.setText("error");			
			}

	        
	        
		}
	}
	
	

	
	
}
