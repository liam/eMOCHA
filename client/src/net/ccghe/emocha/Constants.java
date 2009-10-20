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

public class Constants {
	public static final String TRAINING_TYPE  			= "trainingType";
	public static final String DOC_ID					= "docID";

	public static final String PATH_ODK_FORMS        	= "/sdcard/odk/forms/";
	public static final String PATH_ODK_DATA			= "/sdcard/odk/instances/";
	
	public static final String PATH_BASE 				= "/sdcard/emocha/";
	public static final String PATH_TRAINING_COURSES	= PATH_BASE + "training/courses/";
	public static final String PATH_TRAINING_LECTURES	= PATH_BASE + "training/lectures/";
	public static final String PATH_TRAINING_LIBRARY	= PATH_BASE + "training/library/";
	
	public static final String FILE_PATTERN_VIDEO   	= "^\\w.*\\.mp4$";	
	public static final String FILE_PATTERN_HTML    	= "^\\w.*\\.html$";	

	public static final int ONE_SECOND = 1000; // milliseconds

	public static final int SERVER_URL_MIN_LENGTH   	= 18; 

	/*
	 	To be able to jump into ODK without showing us the main menu
	 	and loading a form directly, we modify the "ODK/AndroidManifest.xml"
	 	and add an intent-filter to the FormEntry activity:

		<intent-filter>
			<action android:name="org.odk.collect.android.SHOW_FORM" />
	        <category android:name="android.intent.category.DEFAULT"/>
		</intent-filter>    
	 	
	 */
	public static final String ODK_INTENT_FILTER_SHOW_FORM 	= "org.odk.collect.android.SHOW_FORM";
	
	// Next value must equal the value of: 
	// org.google.android.odk.SharedConstants.FILEPATH_KEY
    public static final String ODK_FILEPATH_KEY 			= "formpath";
	public static final String LOG_TAG 						= "EMOCHA";
}
