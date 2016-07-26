package com.rmutsv.aquarium;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class SharedValues {
	
	public static final String HOST_DB = "http://188.166.180.204:8888/arduinoping.php";
	
	public static final String SETTING_PREF = "SETTING_PREF";
	
	public static final String KEY_USERNAME = "KEY_USERNAME";
	public static final String KEY_BID = "KEY_BID";
	
	public static final String KEY_IP = "KEY_IP";
	public static final String KEY_PORT = "KEY_PORT";
	
	private SharedValues () {
		
	}
	
	public static void setShouldNoti(Context context, boolean shouldNoti) {
		SharedPreferences sp = context.getSharedPreferences(SETTING_PREF, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean("shouldNoti", shouldNoti);
		editor.commit();
	}
	
	public static boolean getShouldNoti(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SETTING_PREF, Context.MODE_PRIVATE);
		return sp.getBoolean("shouldNoti", false);
	}
	
	public static void setStringPref(Context context, String key, String value) {
		SharedPreferences sp = context.getSharedPreferences(SETTING_PREF, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static String getStringPref(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(SETTING_PREF, Context.MODE_PRIVATE);
		return sp.getString(key, null);
	}
	
	public static void remove(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(SETTING_PREF, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.commit();
	}
	
	
}
