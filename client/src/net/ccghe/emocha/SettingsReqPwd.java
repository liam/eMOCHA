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

import net.ccghe.emocha.model.Preferences;
import net.ccghe.utils.PostData;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SettingsReqPwd extends Activity {
	private TextView pResponseText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.settings_add_phone);
		
		pResponseText = (TextView) findViewById(R.id.SettingsAddPhoneResponse);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		String url = Preferences.getServerURL(this); 
		
		if (url.length() < Constants.SERVER_URL_MIN_LENGTH) {
			pResponseText.setText(R.string.settings_serverURL_undefined);			
		} else {		
	        List<NameValuePair> postData = new ArrayList<NameValuePair>(2);   	
	        postData.add(new BasicNameValuePair("imei", Preferences.getImei(this)));	        
	        postData.add(new BasicNameValuePair("cmd", "activatePhone"));	        

	        String json_code = PostData.Send(postData, url);	        
			try {
				JSONObject jsonResponseObject = new JSONObject(json_code);
				String response = jsonResponseObject.getString("msg");
				pResponseText.setText(response);			
			} catch (JSONException e) {
				pResponseText.setText("error");			
			}

	        
	        
		}
	}
	
	

	
	
}
