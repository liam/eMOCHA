package net.ccghe.emocha.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PatientDB {
    private static final String TAG = "EMOCHA";
    
    private static final String DATABASE_NAME   = "eMOCHA_PATIENT";
    private static final String DATABASE_TABLE  = "patient";
    private static final int DATABASE_VERSION   = 1;

    public static final String KEY_ROWID    = "_id";
    public static final String KEY_NAME     = "name";
    public static final String KEY_STATUS   = "status";


    private static final String DATABASE_CREATE = 
        "CREATE TABLE patient ("
        + "_id integer primary key autoincrement, "
        + "name text not null," 
        + "status text not null);"; 
    
    private static DBHelper sDBHelper;
    private static SQLiteDatabase sDB;

    public static void init(Context tContext) throws SQLException {
        sDBHelper = new DBHelper(tContext);
        try {
        sDB = sDBHelper.getWritableDatabase();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Database is allready open, this should not be the case. : " + e.getMessage() );
        }
            
 
    }
    public static void destroy() throws IllegalStateException{
        if( sDB!=null || !sDB.isOpen() ){
            throw new IllegalStateException("Database is allready closed");
        }else{
            sDB = null;
            sDBHelper.close();
            sDBHelper = null;
        }
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
            Log.w(TAG, "Upgrading patient database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS patient");
            onCreate(db);
        }
        
    }
    
    public static long insertPatient(String name, String status) {      
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_STATUS, status);
        return sDB.insert(DATABASE_TABLE, null, values);
    }
    
    public static Cursor getAllPatients() 
    {
        return sDB.query(DATABASE_TABLE, new String[] {
                KEY_ROWID, 
                KEY_NAME , KEY_STATUS}, 
                null, 
                null, 
                null, 
                null, 
                null);
    }
    
    public static Cursor getPatient(long rowId) throws SQLException 
    {
        Cursor mCursor =
            sDB.query(true, DATABASE_TABLE, new String[] {
                        KEY_ROWID,
                        KEY_NAME,
                        KEY_STATUS
                        }, 
                        KEY_ROWID + "=" + rowId, 
                        null,
                        null, 
                        null, 
                        null, 
                        null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
}
