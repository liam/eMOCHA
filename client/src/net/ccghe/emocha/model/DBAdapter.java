package net.ccghe.emocha.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	private static final String TAG = "EMOCHA";

	private static final String DATABASE_NAME 	= "eMOCHA";
	private static final String SDCARD_TABLE 	= "sdcard";
	private static final int DATABASE_VERSION 	= 1;

	private static final String DATABASE_CREATE = 
		"CREATE TABLE sdcard ("
		+ "path TEXT NOT NULL, " 
		+ "ts INTEGER, " 
		+ "size INTEGER, " 
		+ "md5 TEXT NOT NULL, " 
		+ "to_delete INTEGER, " 
		+ "to_download INTEGER);"; 
		
	private static DBHelper sDBHelper;
	private static SQLiteDatabase sDB;

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
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS sdcard");
			onCreate(db);
		}
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
		return sDB.insert(SDCARD_TABLE, null, values);
	}

	public static boolean deleteFile(String path) {
		return sDB.delete(SDCARD_TABLE, "path='" + path + "'", null) > 0;
	}
	public static boolean deleteMarked() {
		return sDB.delete(SDCARD_TABLE, "to_delete=1", null) > 0;		
	}

	public static Cursor getFile(String path) throws SQLException {
		Cursor c = sDB.query(true, SDCARD_TABLE, 
				new String[] { "path", "ts", "size", "md5", "to_delete", "to_download" },
				"path='" + path + "'", null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	public static boolean updateFile(String path, long ts, long size, String md5, boolean delete, boolean download) {
		ContentValues values = getValues(ts, size, md5, delete, download);
		return sDB.update(SDCARD_TABLE, values, "path='" + path + "'", null) > 0;
	}
	public static boolean markForDeletion(String path, boolean state) {
		ContentValues values = new ContentValues();
		values.put("to_delete", state ? 1 : 0);
		return sDB.update(SDCARD_TABLE, values, "path='" + path + "'", null) > 0;
	}
	public static boolean markForDeletion(boolean state) {
		ContentValues values = new ContentValues();
		values.put("to_delete", state ? 1 : 0);
		return sDB.update(SDCARD_TABLE, values, null, null) > 0;
	}
	
	public static Cursor getFiles() {
		return sDB.query(SDCARD_TABLE, 
				new String[] { "path", "ts", "size", "md5", "to_delete", "to_download"}, 
				null, null, null, null, null);
	}
	public static Cursor getToDeleteFiles() {
		return sDB.query(SDCARD_TABLE, 
				new String[] { "path"}, 
				"to_delete=1", null, null, null, null);		
	}
}
