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

import java.util.ArrayList;

import net.ccghe.emocha.Constants;
import net.ccghe.emocha.HTMLView;
import net.ccghe.emocha.R;
import net.ccghe.emocha.model.DBAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TrainingDocList extends Activity {
    private ListView pList;
    private ArrayAdapter<String> pAdapter;
    private ArrayList<String> pUnits;
    private int pType;

    private ArrayList<String> getNames(ArrayList<String> paths) {
	ArrayList<String> result = new ArrayList<String>();
	int count = paths.size();
	for (int i = 0; i < count; i++) {
	    String path = paths.get(i);
	    result.add(path.substring(1 + path.lastIndexOf("/"), path.lastIndexOf(".")));
	}
	return result;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.training_list);

	Bundle extras = getIntent().getExtras();
	pType = extras.getInt(Constants.TRAINING_TYPE);

	pList = (ListView) findViewById(R.id.TrainingList);
	TextView mH1 = (TextView) findViewById(R.id.header_title);
	pUnits = new ArrayList<String>();

	switch (pType) {
	case R.id.ButtonTrainCourses:
	    mH1.setText(R.string.label_training_courses);
	    pUnits = getNames(DBAdapter.getFilesFiltered(DBAdapter.FILTER_TRAINING_COURSES));
	    break;
	case R.id.ButtonTrainLectures:
	    mH1.setText(R.string.label_training_lectures);
	    pUnits = getNames(DBAdapter.getFilesFiltered(DBAdapter.FILTER_TRAINING_LECTURES));
	    break;
	case R.id.ButtonTrainLibrary:
	    mH1.setText(R.string.label_training_library);
	    pUnits = getNames(DBAdapter.getFilesFiltered(DBAdapter.FILTER_TRAINING_LIBRARY));
	    break;
	}
	pAdapter = new ArrayAdapter<String>(this, R.layout.training_row, R.id.ListText, pUnits);
	pList.setAdapter(pAdapter);

	pList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    public void onItemClick(AdapterView<?> parent, View v, int tPosition, long id) {
		// Toast.makeText(getApplicationContext(), pUnits[position],
		// Toast.LENGTH_SHORT).show();
		Intent tIntent;
		switch (pType) {
		case R.id.ButtonTrainCourses:
		    tIntent = new Intent(getApplicationContext(), TrainingThumb.class);
		    tIntent.putExtra(Constants.DOC_ID, Constants.PATH_TRAINING_COURSES + pUnits.get(tPosition) + ".mp4");
		    startActivity(tIntent);
		    break;
		case R.id.ButtonTrainLectures:
		    tIntent = new Intent(getApplicationContext(), TrainingThumb.class);
		    tIntent.putExtra(Constants.DOC_ID, Constants.PATH_TRAINING_LECTURES + pUnits.get(tPosition) + ".mp4");
		    startActivity(tIntent);
		    break;
		case R.id.ButtonTrainLibrary:
		    tIntent = new Intent(getApplicationContext(), HTMLView.class);
		    tIntent.putExtra(Constants.DOC_ID, pUnits.get(tPosition));
		    startActivity(tIntent);
		    break;
		}

	    }
	});

    }

}
