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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.ccghe.emocha.model.DBAdapter;
import net.ccghe.emocha.model.DBAdapter.FileDetails;
import net.ccghe.utils.FileInfo;
import net.ccghe.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Test extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
										
		// Sync sdcard > database.sdcard
		//syncSD2DB();
				
		// JSON decode
		String tMSG = jsonDecode();
        
		TextView tTV = new TextView(this);
		setContentView(tTV);
        tTV.setText(tMSG + "DONE.");
	}

	private String jsonDecode() {
		String response = "";
        String json_code = getString(R.string.json_sample_code);
        String path;
        
        Map<String, FileInfo> serverFiles = new HashMap<String, FileInfo>();        
        
        try {
			JSONObject jsonResponseObject = new JSONObject(json_code);
			JSONArray jsonFilesArray = jsonResponseObject.getJSONArray("files");
			JSONObject jsonFile;
			int numOfFiles = jsonFilesArray.length(); 
			
			for (int i = 0; i < numOfFiles; i++) {  
				jsonFile = jsonFilesArray.getJSONObject(i);
				path = jsonFile.getString("path");
				serverFiles.put(path, new FileInfo(
						path, 
						jsonFile.getLong("ts"),
						jsonFile.getLong("size"),
						jsonFile.getString("md5")
				));
			}
			Log.i("EMOCHA", serverFiles.get("sdcard/emocha/training/lectures/lecture01.jpg").path());
			Log.i("EMOCHA", serverFiles.get("sdcard/emocha/training/lectures/lecture01.jpg").md5());
			Log.i("EMOCHA", "" + serverFiles.get("sdcard/emocha/training/lectures/lecture01.jpg").length());
			Log.i("EMOCHA", "" + serverFiles.get("sdcard/emocha/training/lectures/lecture01.jpg").lastModified());
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return response;
	}
	
	private void syncSD2DB() {
		FileDetails dbFile;
		String 		md5;
		String		path;

		ArrayList<FileInfo> sdcardFiles = FileUtils.getFilesAsArrayListRecursive("/sdcard/emocha/");	
		// Mark all files for database removal 
		// Later un-mark files found in the sdcard
		DBAdapter.markForDeletion(true);

		// go through all files in sdcard
		for(FileInfo sdcardFile : sdcardFiles) {
			path = sdcardFile.path();
			dbFile = DBAdapter.getFile(path);
			if (dbFile.valid()) {
				if (sdcardFile.length() == dbFile.length() && sdcardFile.lastModified() == dbFile.lastModified()) {
					DBAdapter.markForDeletion(path, false);
				} else {
					md5 = FileUtils.getMd5Hash(path);
					DBAdapter.updateFile(path, sdcardFile.lastModified(), sdcardFile.length(), md5, false, false);										
				}
			} else {				
				md5 = FileUtils.getMd5Hash(path);
				DBAdapter.insertFile(path, sdcardFile.lastModified(), sdcardFile.length(), md5, false, false);
			}
			dbFile.close();
		}
        DBAdapter.deleteMarked();		
	}
	
	
}
