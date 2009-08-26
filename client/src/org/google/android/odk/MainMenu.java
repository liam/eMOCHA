/*
 * Copyright (C) 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.google.android.odk;

import net.ccghe.emocha.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Responsible for displaying buttons to launch the major activities. Also
 * launches some activities based on returns of others.
 * 
 * @author Carl Hartung (carlhartung@gmail.com)
 * @author Yaw Anokwa (yanokwa@gmail.com)
 */
public class MainMenu extends Activity {

    private static final String t = "MainMenu";

    // The request code for returning chosen form to main menu.
    private static final int FORM_CHOOSER = 0;
    private static final int INSTANCE_CHOOSER = 1;

    public static final int MENU_PREFERENCES = Menu.FIRST;


    /**
     * Create View with buttons to launch activities.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(SharedConstants.APPLICATION_THEME);

        super.onCreate(savedInstanceState);
        Log.i(t, "called onCreate");

        setContentView(R.layout.mainmenu);
        setTitle(getString(R.string.app_name) + " > " + getString(R.string.main_menu));

        Button chooseform = (Button) findViewById(R.id.chooseform);
        chooseform.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), FormChooser.class);
                startActivityForResult(i, FORM_CHOOSER);
            }
        });

        Button manageforms = (Button) findViewById(R.id.manageform);
        manageforms.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), FormManager.class);
                startActivity(i);
            }
        });

        Button senddata = (Button) findViewById(R.id.senddata);
        senddata.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                

            }
            
        });

        Button editdata = (Button) findViewById(R.id.editdata);
        editdata.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), InstanceChooser.class);
                startActivityForResult(i, INSTANCE_CHOOSER);
            }
        });
    }


    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_PREFERENCES, 0, getString(R.string.preferences)).setIcon(
                android.R.drawable.ic_menu_preferences);

        return true;
    }


    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_PREFERENCES:
                // createPreferencesMenu();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Upon return, check intent for data needed to launch other activities.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_CANCELED) {
            // The request was canceled, so do nothing.
            return;
        }

        switch (requestCode) {
            // if form chooser returns with a form name, start entry
            case FORM_CHOOSER:
                String s = intent.getStringExtra(SharedConstants.FILEPATH_KEY);
                Intent i = new Intent(this, FormEntry.class);
                Log.i("ABE", s); // /sdcard/odk/forms/mhealth.xml
                i.putExtra(SharedConstants.FILEPATH_KEY, s);
                startActivity(i);
                break;
        }
    }
}
