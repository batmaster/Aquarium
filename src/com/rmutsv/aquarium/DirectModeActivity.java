package com.rmutsv.aquarium;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DirectModeActivity extends Activity {
	
	private EditText editTextIp;
	private EditText editTextPort;
	private Button buttonConnect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_direct_mode);
		
		editTextIp = (EditText) findViewById(R.id.editTextIp);
		String ip = SharedValues.getStringPref(getApplicationContext(), SharedValues.KEY_IP);
		if (ip != null)
			editTextIp.setText(ip);
		
		editTextPort = (EditText) findViewById(R.id.editTextPort);
		String port = SharedValues.getStringPref(getApplicationContext(), SharedValues.KEY_PORT);
		if (port != null)
			editTextPort.setText(port);
		
		buttonConnect = (Button) findViewById(R.id.buttonConnect);
		buttonConnect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedValues.setStringPref(getApplicationContext(), SharedValues.KEY_IP, editTextIp.getText().toString());
				SharedValues.setStringPref(getApplicationContext(), SharedValues.KEY_PORT, editTextPort.getText().toString());
				
				TryToConnectTask task = new TryToConnectTask(getApplicationContext());
				task.execute();
			}
		});
	}
	
	private class TryToConnectTask extends AsyncTask<Void, Void, String> {
		
		private Context context;
		private ProgressDialog loading;
		
		public TryToConnectTask(Context context) {
			this.context = context;
			
			loading = new ProgressDialog(DirectModeActivity.this);
			loading.setTitle("ตรวจสอบการเชื่อมต่อ");
			loading.setMessage("กำลังโหลด...");
//			loading.setCancelable(false);
			loading.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
		}

		@Override
		protected String doInBackground(Void... params) {
			return Service.sendHttpRequest(context, "A", Service.SOCKET_TIMEOUT_TRYING);
		}
		
		@Override
		protected void onPreExecute() {
			loading.show();
			super.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(String result) {
			loading.dismiss();
			
			if (result.length() > 4) {
				
				Intent intent = new Intent(getApplicationContext(), ConsoleActivity.class);
				intent.putExtra("A", result);
				startActivity(intent);
				finish();
			}
			else {
				Toast.makeText(context, "เกิดข้อผิดพลาด", Toast.LENGTH_SHORT).show();
			}
			
//			Intent intent = new Intent(getApplicationContext(), ConsoleActivity.class);
////			String bid = result.substring(4, result.indexOf("-"));
//			intent.putExtra(SharedValues.KEY_BID, "AAA");
//			startActivity(intent);
//			finish();
			
		}
	}
}
