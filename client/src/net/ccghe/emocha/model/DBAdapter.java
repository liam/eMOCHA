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
package net.ccghe.emocha.model;

import java.util.ArrayList;

import net.ccghe.emocha.Constants;
import net.ccghe.utils.FileInfo;
import net.ccghe.utils.Sdcard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	private static final String DATABASE_NAME 	= "eMOCHA";
	private static final int DATABASE_VERSION 	= 2;
	
	private static final String TABLE_DOWNLOADS = "downloads";
	private static final String TABLE_UPLOADS	= "uploads";

	private static final String TABLE_DOWNLOADS_CREATE = 
		"CREATE TABLE " + TABLE_DOWNLOADS + " ("
		+ "path TEXT NOT NULL, " 
		+ "ts INTEGER, " 
		+ "size INTEGER, " 
		+ "md5 TEXT NOT NULL, " 
		+ "to_delete INTEGER, " 
		+ "to_download INTEGER);"; 
	private static final String TABLE_UPLOADS_CREATE = 
		"CREATE TABLE " + TABLE_UPLOADS + " ("
		+ "path TEXT NOT NULL, " 
		+ "ts INTEGER, " 
		+ "size INTEGER);"; 

	public final static int DL_COL_PATH 		= 0;
	public final static int DL_COL_TS 			= 1;
	public final static int DL_COL_SIZE			= 2;
	public final static int DL_COL_MD5 			= 3;
	public final static int DL_COL_TO_DELETE 	= 4;
	public final static int DL_COL_TO_DOWNLOAD 	= 5;

	public final static int UL_COL_PATH 		= 0;
	public final static int UL_COL_TS 			= 1;
	public final static int UL_COL_SIZE			= 2;
	
	private static DBHelper sDBHelper;
	private static SQLiteDatabase sDB;

	public static final String FILTER_DOWNLOAD = "to_download=1";
	public static final String FILTER_DELETE   = "to_delete=1";
	
	public static void init(Context tContext) throws SQLException {
		sDBHelper = new DBHelper(tContext);
		sDB = sDBHelper.getWritableDatabase();
	}
	public static void destroy() {
		sDB = null;
		sDBHelper.close();
		sDBHelper = null;
	}
	
	private static class DBHelper extends SQLiteOpenHelper {
		DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_DOWNLOADS_CREATE);
			db.execSQL(TABLE_UPLOADS_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(Constants.LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
			switch(oldVersion) {
			case 1:
				db.execSQL("ALTER TABLE sdcard RENAME TO " + TABLE_DOWNLOADS);				
				db.execSQL(TABLE_UPLOADS_CREATE);
			}
		}
	}
	

	public static FileInfo getFile(String path) throws SQLException {
		FileInfo info;
		Cursor c = sDB.query(true, TABLE_DOWNLOADS, 
				new String[] { "path", "ts", "size", "md5", "to_delete", "to_download" },
				"path='" + path + "'", null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		info = new FileInfo(c); 
		c.close();
		return info;
	}
	
	
	private static String getFirst(String column, String filter, String table, int columnId) {
		String result = null;
		Cursor c = sDB.query(table, new String[] { column }, filter, null, null, null, null);
		if(c.getCount() > 0) {
			c.moveToFirst();
			result = c.getString(columnId); 
		}
		c.close();
		return result;		
	}
	private static ContentValues getValues(long ts, long size, String md5, boolean delete, boolean download) {
		ContentValues values = new ContentValues();
		values.put("ts", 			ts);
		values.put("size", 			size);
		values.put("md5",			md5);
		values.put("to_delete",		delete ? 1 : 0);
		values.put("to_download",	download ? 1 : 0);		
		return values;
	}
	
	
	public static long insertFile(String path, long ts, long size, String md5, boolean delete, boolean download) {		
		ContentValues values = getValues(ts, size, md5, delete, download);
		values.put("path", path);
		return sDB.insert(TABLE_DOWNLOADS, null, values);
	}

	public static boolean deleteFile(String path) {
		return sDB.delete(TABLE_DOWNLOADS, "path='" + path + "'", null) > 0;
	}
	public static boolean deleteMarked() {
		return sDB.delete(TABLE_DOWNLOADS, FILTER_DELETE, null) > 0;		
	}

	public static boolean updateFile(String path, long ts, long size, String md5, boolean delete, boolean download) {
		ContentValues values = getValues(ts, size, md5, delete, download);
		return sDB.update(TABLE_DOWNLOADS, values, "path='" + path + "'", null) > 0;
	}
	public static boolean markForDeletion(String path, boolean state) {
		ContentValues values = new ContentValues();
		values.put("to_delete", state ? 1 : 0);
		return sDB.update(TABLE_DOWNLOADS, values, "path='" + path + "'", null) > 0;
	}
	public static boolean markForDeletion(boolean state) {
		ContentValues values = new ContentValues();
		values.put("to_delete", state ? 1 : 0);
		return sDB.update(TABLE_DOWNLOADS, values, null, null) > 0;
	}
	public static boolean markForDownload(String path, String newMD5) {
		ContentValues values = new ContentValues();
		values.put("to_download", 1);
		values.put("md5", newMD5);
		return sDB.update(TABLE_DOWNLOADS, values, "path='" + path + "'", null) > 0;
	}
	
	public static Cursor getFiles() {
		return sDB.query(TABLE_DOWNLOADS, 
				new String[] { "path", "ts", "size", "md5", "to_delete", "to_download"}, 
				null, null, null, null, null);
	}
	public static ArrayList<String> getFilePaths() {
		Cursor c = sDB.query(TABLE_DOWNLOADS, 
				new String[] { "path"}, 
				null, null, null, null, null);
		ArrayList<String> result = new ArrayList<String>();
		
		int numRows = c.getCount();
		for (int i = 0; i < numRows; i++) {
			c.moveToPosition(i);
			result.add(c.getString(DL_COL_PATH));
		}		
		c.close();
		return result;
	}
	public static int pendingFileTransfersNum() {
		int result = 0;
		Cursor c;
		
		c = sDB.query(TABLE_DOWNLOADS, new String[] { "1" }, 
				FILTER_DOWNLOAD, null, null, null, null);
		if (c != null) {
			result += c.getCount();
		}
		c.close();

		c = sDB.query(false, TABLE_UPLOADS, new String[] { "1" }, 
				null, null, null, null, null, null);
		if (c != null) {
			result += c.getCount();
		}
		c.close();
		return result;
	}
	public static String getFirstDownloadID() {
		return getFirst("path", FILTER_DOWNLOAD, TABLE_DOWNLOADS, DL_COL_PATH);
	}
	public static String getFirstUploadID() {
		return getFirst("path", null, TABLE_UPLOADS, UL_COL_PATH);
	}
	public static ArrayList<String> getFilesFiltered(String filter) {
		ArrayList<String> result = new ArrayList<String>();
		Cursor c = sDB.query(TABLE_DOWNLOADS, new String[] { "path"}, filter, null, null, null, null);
		int numRows = c.getCount();
		Log.i(Constants.LOG_TAG, filter + " : " + numRows);
		for (int i = 0; i < numRows; i++) {
			c.moveToPosition(i);
			result.add(c.getString(DL_COL_PATH));
			Log.i(Constants.LOG_TAG, i + " : " + c.getString(DL_COL_PATH));
		}
		c.close();
		return result;
	}
	public static int updateFromJSON(JSONArray jsonFilesArray) {
		// Mark all files for database removal 
		// Later un-mark files that are supposed to be there
		markForDeletion(true);
        Log.i(Constants.LOG_TAG, "UPDJ: Mark all for deletetion");
        
        String 		path;
		FileInfo 	dbFile;
		JSONObject 	jsonFile;
		int 		filesToDownload = 0;

		try {
			int numOfFiles = jsonFilesArray.length(); 
			
			// go through the list of files found
			// in server side, and make the local sqlite3
			// database look like it, marking what needs
			// to be downloaded.
			for (int i = 0; i < numOfFiles; i++) {  
				jsonFile = jsonFilesArray.getJSONObject(i);	
				
				// is the current json file found in the database?
				path = '/' + jsonFile.getString("path");
				dbFile = getFile(path);
				if (dbFile.isInDB()) {
					String targetMD5 = jsonFile.getString("md5"); 
					if (targetMD5.equals(dbFile.md5())) {
						// the file looks identical
						markForDeletion(path, false);
						Log.i(Constants.LOG_TAG, "UPDJ: Unchanged " + path);
					} else {
						// the file has changed. 
						// don't delete, download a new version.
						markForDeletion(path, false);
						markForDownload(path, targetMD5);
						filesToDownload++;
						Log.i(Constants.LOG_TAG, "UPDJ: Changed " + path);
						Log.w(Constants.LOG_TAG, jsonFile.getString("md5") + " != " + dbFile.md5() );
					}
				} else {
					// file not in local database. insert it
					// and mark it for download.
					Long r = insertFile(path, 
						jsonFile.getLong("ts"), 
						jsonFile.getLong("size"), 
						jsonFile.getString("md5"), 
						false, 
						true);
					filesToDownload++;
					Log.i(Constants.LOG_TAG, "UPDJ: New " + path + ", result=" + r);
				}
			}			
		} catch (JSONException e) {
			Log.e("EMOCHA", "Error parsing json file list");
			e.printStackTrace();
		}
        deleteMarked();
		Sdcard.deleteUnwantedFiles(getFiles());
        return filesToDownload;
	}
	public static void markAsDownloaded(FileInfo file) {
		ContentValues values = new ContentValues();
		values.put("to_download", 0);
				
		int affected = sDB.update(TABLE_DOWNLOADS, values, 
				"path='" + file.path() + "' AND md5='" + file.md5() + "'", null);
		
		Log.i(Constants.LOG_TAG, "Mark as downloaded: " + file.path() + 
				" (" + file.md5() + ") " + affected);
	}
	public static boolean markAsUploaded(String path) {
		return sDB.delete(TABLE_UPLOADS, "path='" + path + "'", null) > 0;
	}
	public static int clean() {
		ContentValues values = new ContentValues();
		values.put("to_download", 0);
				
		return sDB.update(TABLE_DOWNLOADS, values, null, null);
	}

	public static long insertFilesNewerThan(Long lastUploadTimestamp,
			ArrayList<FileInfo> sdcardFiles) {			

		Long newUploadTimeStamp = lastUploadTimestamp; 
		for(FileInfo sdcardFile : sdcardFiles) {
			if (sdcardFile.lastModified() > lastUploadTimestamp) {
				ContentValues values = new ContentValues(3);
				values.put("path",	sdcardFile.path());
				values.put("ts", 	sdcardFile.lastModified());
				values.put("size", 	sdcardFile.length());
				sDB.insert(TABLE_UPLOADS, null, values);
				
				newUploadTimeStamp = Math.max(newUploadTimeStamp, sdcardFile.lastModified());
			}
		}
		return newUploadTimeStamp;
	}
	
}
