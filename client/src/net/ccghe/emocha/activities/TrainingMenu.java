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


import net.ccghe.emocha.Constants;
import net.ccghe.emocha.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TrainingMenu extends ListActivity {
	
    final String[] labels = {"Lectures" , "Courses" , "Library"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	initViews();
    }

    private void initViews() {
	this.setContentView(R.layout.standard_main_menu);
	setListAdapter(new ArrayAdapter<String>(this, R.layout.standard_list_button, R.id.firstLine, labels));

	TextView mH1 = (TextView) findViewById(R.id.header_title);
	mH1.setText("Choose a category");

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

	switch (position) {
	case 0:
	    launchActivity(R.id.ButtonTrainLectures);
	    break;
	case 1:
	    launchActivity(R.id.ButtonTrainCourses);
	    break;
	case 2:
	    launchActivity(R.id.ButtonTrainLibrary);
	    break;
	}

	super.onListItemClick(l, v, position, id);
    }

    private void launchActivity(int id) {
	Intent tIntent = new Intent(getApplicationContext(), TrainingDocList.class);
	tIntent.putExtra(Constants.TRAINING_TYPE, id);
	startActivity(tIntent);
    }
}
