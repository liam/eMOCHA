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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TrainingMenu extends Activity {
	private Button pTrainingLectures;
	private Button pTrainingCourses;
	private Button pTrainingLibrary;
	
	private View.OnClickListener pOnClick;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training_menu);
		
		pTrainingCourses  = (Button) findViewById(R.id.ButtonTrainCourses);
		pTrainingLectures = (Button) findViewById(R.id.ButtonTrainLectures);
		pTrainingLibrary  = (Button) findViewById(R.id.ButtonTrainLibrary);
		
		pOnClick = new View.OnClickListener() {
			public void onClick(View v) {
				int tID = v.getId();
		        switch(tID) {
		        	case R.id.ButtonTrainCourses:
						Toast.makeText(getApplicationContext(), "COURSES", Toast.LENGTH_SHORT).show();						        		
		        		break;
		        	case R.id.ButtonTrainLectures:
						Toast.makeText(getApplicationContext(), "LECTURES", Toast.LENGTH_SHORT).show();						        		
		        		break;
		        	case R.id.ButtonTrainLibrary:
						Toast.makeText(getApplicationContext(), "LIBRARY", Toast.LENGTH_SHORT).show();						        		
		        		break;
		        }
		        Intent tIntent = new Intent(getApplicationContext(), TrainingDocList.class);
		        tIntent.putExtra(Constants.TRAINING_TYPE, tID);
		        startActivity(tIntent);
            }			
		};
		
		pTrainingCourses.setOnClickListener(pOnClick);
		pTrainingLectures.setOnClickListener(pOnClick);
		pTrainingLibrary.setOnClickListener(pOnClick);
	}

}
