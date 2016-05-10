package com.rmutsv.aquarium;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class Listening {
	public String bid;
	public String date;
	
	public Listening(String bid, String date) {
		this.bid = bid;
		this.date = date;
	}
}

public class DBHelper extends SQLiteOpenHelper {
	
	private String CREATE_TABLE = "CREATE TABLE bids (id INTEGER PRIMARY KEY AUTOINCREMENT, bid TEXT, date TEXT)";
	
	private static DBHelper instance;

    private DBHelper(Context context) {
        super(context, "db.arduino", null, 1);
    }
    
    public static DBHelper getInstance(Context context) {
    	if (instance == null)
    		instance = new DBHelper(context);
    	return instance;
    }
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public ArrayList<Listening> getListening() {
		ArrayList<Listening> list = new ArrayList<Listening>();
		
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT bid, date FROM bids";
	    Cursor cursor = db.rawQuery(sql, null);

	    if (cursor != null) {
	        cursor.moveToFirst();
	    }

	    while (!cursor.isAfterLast()) {
	    	list.add(new Listening(cursor.getString(0), cursor.getString(1)));
	        cursor.moveToNext();
	    }

		return list;
	}
	
	public boolean addUpListeningId(Context context, String bid, String date) {
		int count = 0;
		
		SQLiteDatabase db = getReadableDatabase();
		String sql = String.format("SELECT COUNT(*) count FROM bids WHERE bid = '%s' AND date = '%s'", bid, date);
	    Cursor cursor = db.rawQuery(sql, null);

	    if (cursor != null) {
	        cursor.moveToFirst();
	    }

	    while (!cursor.isAfterLast()) {
	    	count = cursor.getInt(0);
	        cursor.moveToNext();
	    }
	    
	    if (count > 0)
	    	return false;
	    else {
	    	SQLiteDatabase db2 = getWritableDatabase();
			String sql2 = String.format("DELETE FROM bids WHERE bid = '%s'", bid);
			db2.execSQL(sql2);
		    
		    SQLiteDatabase db3 = getWritableDatabase();
			String sql3 = String.format("INSERT INTO bids (bid, date) VALUES ('%s', '%s')", bid, date);
			db3.execSQL(sql3);
	    }
	    
	    return true;
	}
}
