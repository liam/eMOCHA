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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends Activity {

	private SharedPreferences 	pSettings;
	private Button 				pButtonSavePrefs;
	private EditText			pServerURLInput;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mh_settings);

		pSettings = getSharedPreferences(Constants.MAIN_PREFS_FILE_NAME, 0);

		pServerURLInput = (EditText) findViewById(R.id.ServerURLInput);
		
		pButtonSavePrefs = (Button) findViewById(R.id.ButtonSavePrefs);
		pButtonSavePrefs.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SharedPreferences.Editor tEditSettings = pSettings.edit();
				tEditSettings.putString(Constants.PREFS_VAR_SERVER_URL, pServerURLInput.getText().toString());
				tEditSettings.commit();
				
				Intent i = new Intent(getApplicationContext(), Main.class);
                startActivity(i); 				
			}
		});		
				
		String tServerURL = pSettings.getString(Constants.PREFS_VAR_SERVER_URL, ".");
		if (tServerURL == ".") {
			Toast.makeText(getApplicationContext(), "Please enter server URL", Toast.LENGTH_SHORT).show();	
		} else {
			pServerURLInput.setText(tServerURL);
		}
	
	}
	
}
