package com.dhruv.transparency;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	
    public static final String KEY_ROWID = "id";
    public static final String KEY_UnlockTime = "UnlockTime";
    public static final String KEY_UnlockDate = "UnlockDate";
    public static final String KEY_PackageName = "PackageName";
    public static final String KEY_TimeUsed = "TimeUsed";
    private static final String DATABASE_NAME = "UnlockHistoryDB";
    private static final String DATABASE_TABLE = "UnlockHistory";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "create table if not exists UnlockHistory (id integer primary key autoincrement, "
            + "UnlockTime VARCHAR not null, UnlockDate VARCHAR not null, TimeUsed VARCHAR not null, PackageName VARCHAR);";
    private final Context context;    
    private static DatabaseHelper DBHelper;
    private static SQLiteDatabase db;
    public DBAdapter(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
    public static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) 
        {
        	try {
        		db.execSQL(DATABASE_CREATE);	
        	} catch (SQLException e) {
        		e.printStackTrace();
        	}
        }

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
		
		//---opens the database---
	    public DatabaseHelper open() throws SQLException 
	    {
	        db = DBHelper.getWritableDatabase();
	        return this;
	    }
	    /*
	    //---insert a record into the database---
	    public long insertRecord(String UnlockTime,String UnlockDate, Double TimeUsed) 
	    {		// TODO Auto-generated method stub
	        ContentValues initialValues = new ContentValues();
	        initialValues.put(KEY_UnlockTime, UnlockTime);
	        initialValues.put(KEY_UnlockDate, UnlockDate);
	        initialValues.put(KEY_TimeUsed, TimeUsed.toString());
	        initialValues.put(KEY_PackageName, UnlockDate);

	        return db.insert(DATABASE_TABLE, null, initialValues);
	    }*/
	    
	    public Cursor getAllRecords() 
	    {
	        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_UnlockTime,KEY_UnlockDate,
	                KEY_TimeUsed,KEY_PackageName}, null, null, null, null, null);
	    }



    }
	public DBAdapter open() throws SQLException{
		// TODO Auto-generated method stub
		db = DBHelper.getWritableDatabase();
        return this;
	}
	public long insertRecord(String UnlockTime,String UnlockDate, String timeUsed, String PackageName) {
		// TODO Auto-generated method stub
		ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_UnlockTime, UnlockTime);
        initialValues.put(KEY_UnlockDate, UnlockDate);        
        initialValues.put(KEY_TimeUsed, timeUsed);
        initialValues.put(KEY_PackageName, PackageName);        
        return db.insert(DATABASE_TABLE, null, initialValues);
	}
	public void close() {
		// TODO Auto-generated method stub
        DBHelper.close();
	}
	public Cursor getAllRecords() {
		// TODO Auto-generated method stub
		 return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_UnlockTime,KEY_UnlockDate,
	                KEY_TimeUsed,KEY_PackageName}, null, null, null, null, null);
		 }

    //---deletes a particular record---
	public boolean deleteData(long rowId) {
		// TODO Auto-generated method stub
		return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}
	public static void deleteAll() {
		// TODO Auto-generated method stub
    	db.delete("UnlockHistory", null, null);
    	Log.e("Delete", "Deleted");
    	
	}
	
	

    
}
