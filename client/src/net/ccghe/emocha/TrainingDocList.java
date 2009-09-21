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

public class TrainingDocList extends Activity {
	private TextView			 pTitle;
	private ListView             pList;
	private ArrayAdapter<String> pAdapter;
	private ArrayList<String>	 pUnits;
	private int 				 pType;
	
	private Pattern 			 pFilePatternVideo;
	private Pattern   			 pFilePatternHTML;
	private Matcher				 pPatternMatcher;
	
	/**
	 * @param tFolderPath
	 */
	private void addFilesFromFolder(String tFolderPath, Pattern tPattern) {
		  File tFolder = new File(tFolderPath);
		  File[] tListOfFiles = tFolder.listFiles();	  
		  for (int i = 0; i < tListOfFiles.length; i++) {
			  if (tListOfFiles[i].isFile()) {
				  String tName = tListOfFiles[i].getName();
				  pPatternMatcher = tPattern.matcher(tName);
				  if(pPatternMatcher.matches()) {
					  pUnits.add(tName);
				  }
		    } 
		  }	  		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	  setContentView(R.layout.training_list);

	  Bundle extras = getIntent().getExtras();
	  pType = extras.getInt(Constants.TRAINING_TYPE);
	  
	  pList  = (ListView) findViewById(R.id.TrainingList);
	  pTitle = (TextView) findViewById(R.id.TrainingListTitle);
	  pUnits = new ArrayList<String> ();
	  
	  pFilePatternVideo = Pattern.compile(Constants.FILE_PATTERN_VIDEO);
	  pFilePatternHTML  = Pattern.compile(Constants.FILE_PATTERN_HTML);
	  	  
	  switch (pType) {
		  case R.id.ButtonTrainCourses:
			  pTitle.setText(R.string.label_training_courses);
			  addFilesFromFolder(Constants.PATH_TRAINING_COURSES, pFilePatternVideo);			  
			  break;
		  case R.id.ButtonTrainLectures:
			  pTitle.setText(R.string.label_training_lectures);
			  addFilesFromFolder(Constants.PATH_TRAINING_LECTURES, pFilePatternVideo);			  			  
			  break;
		  case R.id.ButtonTrainLibrary:
			  pTitle.setText(R.string.label_training_library);
			  addFilesFromFolder(Constants.PATH_TRAINING_LIBRARY, pFilePatternHTML);
			  break;
	  }
	  pAdapter = new ArrayAdapter<String>(this, R.layout.training_row, R.id.ListText, pUnits);
	  pList.setAdapter(pAdapter);
	  
	  pList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int tPosition, long id) {
			//Toast.makeText(getApplicationContext(), pUnits[position], Toast.LENGTH_SHORT).show();	
			Intent tIntent;
			switch (pType) {
				  case R.id.ButtonTrainCourses:
					  tIntent = new Intent(getApplicationContext(), TrainingThumb.class);				
					  tIntent.putExtra(Constants.DOC_ID, Constants.PATH_TRAINING_COURSES + pUnits.get(tPosition));					  
					  startActivity(tIntent);
					  break;
				  case R.id.ButtonTrainLectures:
					  tIntent = new Intent(getApplicationContext(), TrainingThumb.class);				
					  tIntent.putExtra(Constants.DOC_ID, Constants.PATH_TRAINING_LECTURES + pUnits.get(tPosition));					  
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
