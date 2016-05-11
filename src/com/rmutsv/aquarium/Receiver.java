package com.rmutsv.aquarium;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class Receiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Log.d("recc", "ACTION_BOOT_COMPLETED");
			context.startService(new Intent(context, BootUpService.class));
		}
		else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
			Log.d("recc", "ACTION_TIME_TICK");
			context.startService(new Intent(context, BootUpService.class));
			if (SharedValues.getStringPref(context, SharedValues.KEY_USERNAME) != null) {
				CheckLostTask task = new CheckLostTask(context);
				task.execute();
			}
		}
	}
	
	private class CheckLostTask extends AsyncTask<Void, Void, String> {
		
		private Context context;
		
		public CheckLostTask(Context context) {
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(Void... params) {
			String res = "";
			try {
				res = Request.checkLost(context);
				Log.d("recc", "ตรวจสอบแปป");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return res;
		}

		@Override
		protected void onPostExecute(String json) {
			NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			
			try {
				JSONArray ja = new JSONArray(json);
				Log.d("recc", "เจอ " + ja.length() + " รายการ");
				for (int i = 0; i < ja.length(); i++) {
					String bid = ja.getJSONObject((i)).getString("bid");
					String date = ja.getJSONObject((i)).getString("date");
					
					Listening l = new Listening(bid, date);
					Log.d("recc", "=== เริ่ม" + (i+1));
					Log.d("recc", "-bid: " + l.getBid() + " date: " + l.getDate());
					
					if (!DBHelper.getInstance(context).isListening(l)) {
						Log.d("recc", "-ยังไม่มีอยู่ในรายการ งั้นลบ bid: " + l.getBid() + " เดิมที่มีอยู่ก่อนนะ");
						DBHelper.getInstance(context).remove(l.getBid());
						
						Log.d("recc", "-เพิ่มไปละนะ จะได้ไม่เตือนซ้ำ");
						DBHelper.getInstance(context).addListening(l);
						
						
						Log.d("recc", "-แจ้งเตือนละนะ");
						NotificationCompat.Builder mBuilder =
							    new NotificationCompat.Builder(context)
							    .setSmallIcon(R.drawable.ic_launcher)
							    .setContentTitle("บอร์ดขาดการติดต่อ")
							    .setContentText(bid  + " ตั้งแต่เวลา " + date);
						
						mNotifyMgr.notify((int) (new Date().getTime() % 65535), mBuilder.build());
					}
					else {
						Log.d("recc", "-มีอยู่ในรายการแล้ว ไม่แจ้งเตือน");
					}

					Log.d("recc", "=== จบ" + (i+1));
					
//					if (DBHelper.getInstance(context).addUpListeningId(context, bid, date)) {
//						NotificationCompat.Builder mBuilder =
//							    new NotificationCompat.Builder(context)
//							    .setSmallIcon(R.drawable.ic_launcher)
//							    .setContentTitle("บอร์ดขาดการติดต่อ")
//							    .setContentText(bid  + " ตั้งแต่เวลา " + date);
//						
//						mNotifyMgr.notify((int) (new Date().getTime() % 65535), mBuilder.build());
//						Log.d("nott", bid  + " ตั้งแต่เวลา " + date);
//					}
//					else {
//						Log.d("nott", bid + " " + date + " ไม่เพิ่ม");
//					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
}