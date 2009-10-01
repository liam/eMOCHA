package net.ccghe.emocha;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.ccghe.emocha.model.DBAdapter;
import net.ccghe.emocha.model.DBAdapter.FileDetails;
import net.ccghe.utils.FileInfo;
import net.ccghe.utils.FileUtils;
import android.app.Activity;
import android.database.Cursor;
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
        Log.i("EMOCHA", json_code);
        try {
			JSONObject jsonResponseObject = new JSONObject(json_code);
			JSONArray jsonFilesArray = jsonResponseObject.getJSONArray("files");
			JSONObject jsonFile;
			int numOfFiles = jsonFilesArray.length(); 
			
			for (int i = 0; i < numOfFiles; i++) {  
				jsonFile = jsonFilesArray.getJSONObject(i);
				response += jsonFile.get("path") + "\n  "
					+ jsonFile.getString("size") + ", "
					+ jsonFile.getString("ts") + ", "
					+ jsonFile.getString("md5") + "\n";
			}
			
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
