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
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class Main extends Activity {
	private Button pCallButton;
	private Button pTrainingButton;
	private Button pAddPatientButton;
	private Button pEditPatientButton;
	private Button pHelpButton;
	
	private final static int MENU_SETTINGS = 1;
	private final static int MENU_TEST = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		Intent tService = new Intent(this, ServerService.class);
		startService(tService);
	
		pCallButton =        (Button) findViewById(R.id.ButtonMainMenuCall);
		pTrainingButton =    (Button) findViewById(R.id.ButtonMainMenuTraining);
		pAddPatientButton =  (Button) findViewById(R.id.ButtonMainMenuAddPatient);
		pEditPatientButton = (Button) findViewById(R.id.ButtonMainMenuUpdPatient);
		pHelpButton =        (Button) findViewById(R.id.ButtonMainMenuHelp);

		pCallButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), CallMenu.class);
                startActivity(i); 
			}
		});
		pTrainingButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), TrainingMenu.class);
                startActivity(i);
			}
		});
		pAddPatientButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
                Intent i = new Intent(Constants.ODK_INTENT_FILTER_SHOW_FORM);
                i.putExtra(Constants.ODK_FILEPATH_KEY, Constants.PATH_ODK_FORMS + "mHealth.xml");
                startActivity(i);                
			}
		});
		pEditPatientButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			}
		});		
		pHelpButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), Help.class);
                startActivity(i);
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		// if basic settings have not been set, go to preferences screen.
		if (!Preferences.hasBasicSettings()) {
			Intent i = new Intent(getApplicationContext(), Settings.class);
			startActivity(i);    	   			
		}
	}



	public boolean onCreateOptionsMenu(Menu tMenu) {
		tMenu.
			add(0, MENU_SETTINGS, 0, "Settings").
			setIcon(R.drawable.ic_menu_preferences).
			setIntent(new Intent(this, Settings.class));

		tMenu.
			add(0, MENU_TEST, 0, "Test").
			setIcon(R.drawable.ic_menu_clear_playlist).
			setIntent(new Intent(this, Test.class));
		
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//Intent tService = new Intent(this, ServerService.class);
		//stopService(tService);
	}
	
	
}
