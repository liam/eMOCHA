/*******************************************************************************
 * eMocha - Easy Mobile Open-Source Comprehensive Health Application
 * Copyright (c) 2009 Abe Pazos - abe@ccghe.net
 * 
 * This file is part of eMocha.
 * 
 * eMocha is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * eMocha is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.ccghe.emocha;

import org.google.android.odk.FormEntry;
import org.google.android.odk.MainMenu;
import org.google.android.odk.SharedConstants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MHMainMenu extends Activity {
	private Button pCallButton;
	private Button pTrainingButton;
	private Button pAddPatientButton;
	private Button pEditPatientButton;
	private Button pHelpButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mh_main_menu);

		pCallButton =        (Button) findViewById(R.id.ButtonMainMenuCall);
		pTrainingButton =    (Button) findViewById(R.id.ButtonMainMenuTraining);
		pAddPatientButton =  (Button) findViewById(R.id.ButtonMainMenuAddPatient);
		pEditPatientButton = (Button) findViewById(R.id.ButtonMainMenuUpdPatient);
		pHelpButton =        (Button) findViewById(R.id.ButtonMainMenuHelp);
			
		pCallButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), MHCallMenu.class);
                startActivity(i); 
			}
		});
		pTrainingButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), MHTrainingMenu.class);
                startActivity(i);
			}
		});
		pAddPatientButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//Intent i = new Intent(getApplicationContext(), MainMenu.class);
                //startActivity(i);
                
                Intent i = new Intent(getApplicationContext(), FormEntry.class);                
                i.putExtra(SharedConstants.FILEPATH_KEY, "/sdcard/odk/forms/mHealth.xml");
                startActivity(i);                
			}
		});
		pEditPatientButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), MainMenu.class);
                startActivity(i);
			}
		});		
		pHelpButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), MHHelp.class);
                startActivity(i);
			}
		});
		
		
	}
}
