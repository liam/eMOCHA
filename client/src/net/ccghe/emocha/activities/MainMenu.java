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
package net.ccghe.emocha.activities;

import net.ccghe.emocha.R;
import net.ccghe.emocha.Settings;
import net.ccghe.emocha.Test;
import net.ccghe.emocha.model.Preferences;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainMenu extends ListActivity {
	
    private final static int MENU_SETTINGS = 1;
    private final static int MENU_TEST = 2;

    final String[] labels = { "Communication", "Patients", "Training", "Help", "Settings" };
      
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	initViews();

	this.sendBroadcast(new Intent().setAction("net.ccghe.emocha.START"));
    }

    private void initViews() {
	this.setContentView(R.layout.standard_main_menu);
	setListAdapter(new ArrayAdapter<String>(this, R.layout.standard_list_button, R.id.firstLine, labels));
	TextView mH1 = (TextView) findViewById(R.id.header_title);
	mH1.setText("Welcome. What would you like to do today?");
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        switch( position ){
            case 0:
                launchActivity( CommunicationsMenu.class );
                break;
            case 1:
                launchActivity( PatientListActivity.class );
                break;
            case 2:
                launchActivity( TrainingMenu.class );
                break;
            case 3:
                launchActivity( HelpActivity.class );
                break;
            case 4:
                launchActivity( Settings.class );
                break;
			}
        super.onListItemClick(l, v, position, id);
			}
    
    private void launchActivity(Class<?> clazz) {
	Intent i = new Intent(getApplicationContext(), clazz);
	startActivity(i);
    }
	
    @Override
    protected void onStart() {
	super.onStart();

	// if basic settings have not been set, go to preferences screen.
	if (!Preferences.hasBasicSettings(this)) {
	    Intent i = new Intent(getApplicationContext(), Settings.class);
	    startActivity(i);
	}
    }

    public boolean onCreateOptionsMenu(Menu tMenu) {
	tMenu.add(0, MENU_SETTINGS, 0, "Settings").setIcon(R.drawable.ic_menu_preferences).setIntent(new Intent(this, Settings.class));
	tMenu.add(0, MENU_TEST, 0, "Test").setIcon(R.drawable.ic_menu_clear_playlist).setIntent(new Intent(this, Test.class));

	return true;
    }

}
