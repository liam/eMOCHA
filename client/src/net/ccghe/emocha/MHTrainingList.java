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

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MHTrainingList extends Activity {
	private TextView			 pTitle;
	private ListView             pList;
	private ArrayAdapter<String> pAdapter;
	private ArrayList<String>	 pUnits;
	private int 				 pType;
	
	private Pattern 			 pVideoFilePattern;
	private Matcher				 pVideoFileMatcher;
		
	/**
	 * @param tFolderPath
	 */
	private void addFilesFromFolder(String tFolderPath) {
		  File tFolder = new File(tFolderPath);
		  File[] tListOfFiles = tFolder.listFiles();	  
		  for (int i = 0; i < tListOfFiles.length; i++) {
			  if (tListOfFiles[i].isFile()) {
				  String tName = tListOfFiles[i].getName();
				  pVideoFileMatcher = pVideoFilePattern.matcher(tName);
				  if(pVideoFileMatcher.matches()) {
					  pUnits.add(tName);
				  }
		    } 
		  }	  		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	  setContentView(R.layout.mh_training_list);

	  Bundle extras = getIntent().getExtras();
	  pType = extras.getInt(MHConstants.TRAINING_TYPE);
	  
	  pList  = (ListView) findViewById(R.id.TrainingList);
	  pTitle = (TextView) findViewById(R.id.TrainingListTitle);
	  pUnits = new ArrayList<String> ();
	  
	  pVideoFilePattern = Pattern.compile(MHConstants.VIDEO_FILE_PATTERN);
	  	  
	  switch (pType) {
		  case R.id.ButtonTrainCourses:
			  pTitle.setText(R.string.label_training_courses);
			  addFilesFromFolder(MHConstants.VIDEO_COURSES_PATH);			  
			  break;
		  case R.id.ButtonTrainLectures:
			  pTitle.setText(R.string.label_training_lectures);
			  addFilesFromFolder(MHConstants.VIDEO_LECTURES_PATH);			  			  
			  break;
		  case R.id.ButtonTrainLibrary:
			  pTitle.setText(R.string.label_training_library);
			  pUnits.add("abacavir");
			  pUnits.add("atazanavir"); 
			  pUnits.add("darunavir");
			  pUnits.add("delavirdine");
			  pUnits.add("didanosine");
			  pUnits.add("efavirenz");
			  pUnits.add("emtricitabine"); 
			  pUnits.add("enfuvirtide");
			  pUnits.add("etravirine");
			  pUnits.add("fosamprenavir"); 
			  pUnits.add("indinavir"); 
			  pUnits.add("lamivudine");
			  pUnits.add("lopinavir_ritonavir"); 
			  pUnits.add("maraviroc");
			  pUnits.add("nelfinavir");
			  break;
	  }
	  pAdapter = new ArrayAdapter<String>(this, R.layout.mh_list_row, R.id.ListText, pUnits);
	  pList.setAdapter(pAdapter);
	  
	  pList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int tPosition, long id) {
			//Toast.makeText(getApplicationContext(), pUnits[position], Toast.LENGTH_SHORT).show();	
			Intent tIntent;
			switch (pType) {
				  case R.id.ButtonTrainCourses:
					  tIntent = new Intent(getApplicationContext(), MHTrainingItem.class);				
					  tIntent.putExtra(MHConstants.DOC_ID, MHConstants.VIDEO_COURSES_PATH + pUnits.get(tPosition));					  
					  startActivity(tIntent);
					  break;
				  case R.id.ButtonTrainLectures:
					  tIntent = new Intent(getApplicationContext(), MHVideo.class);				
					  tIntent.putExtra(MHConstants.DOC_ID, MHConstants.VIDEO_LECTURES_PATH + pUnits.get(tPosition));					  
					  startActivity(tIntent);
					  break;
				  case R.id.ButtonTrainLibrary:
					  tIntent = new Intent(getApplicationContext(), MHWebView.class);
				      tIntent.putExtra(MHConstants.DOC_ID, pUnits.get(tPosition));
				      startActivity(tIntent);
					  break;	
			}
						
        }
	  });
	  
	}

}
