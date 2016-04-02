package com.rmutsv.aquarium;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingActivity extends Activity {
	
	private Switch switchHeaterMode;
	private Switch switchFilterMode;
	private EditText editTextHeaterMin;
	private EditText editTextFilterMax;
	private RadioButton radioWifiMode1;
	private RadioButton radioWifiMode2;
	private RadioButton radioWifiMode3;
	private EditText editTextWifiSSID;
	private EditText editTextWifiPassword;
	private EditText editTextBoardName;
	private EditText editTextStaticIP;
	private EditText editTextPort;
	private Button buttonSet;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		switchHeaterMode = (Switch) findViewById(R.id.switchHeaterMode);
		switchFilterMode = (Switch) findViewById(R.id.switchFilterMode);
		
		editTextHeaterMin = (EditText) findViewById(R.id.editTextHeaterMin);
		editTextFilterMax = (EditText) findViewById(R.id.editTextFilterMax);
		
		radioWifiMode1 = (RadioButton) findViewById(R.id.radioWifiMode1);
		radioWifiMode2 = (RadioButton) findViewById(R.id.radioWifiMode2);
		radioWifiMode3 = (RadioButton) findViewById(R.id.radioWifiMode3);
		
		editTextWifiSSID = (EditText) findViewById(R.id.editTextWifiSSID);
		editTextWifiPassword = (EditText) findViewById(R.id.editTextWifiPassword);
		editTextBoardName = (EditText) findViewById(R.id.editTextBoardName);
		editTextStaticIP = (EditText) findViewById(R.id.editTextStaticIP);
		editTextPort = (EditText) findViewById(R.id.editTextPort);
		
		buttonSet = (Button) findViewById(R.id.buttonSet);
		buttonSet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SettingTask task = new SettingTask(getApplicationContext());
				task.execute();
			}
		});
		
		CheckTask task = new CheckTask(getApplicationContext());
		task.execute();
	}
	
	private boolean busy = false;
	// คลาสสำหรับ ส่งคำสั่ง และตรวจสอบสถานะหลังจากนั้น ในบอร์ด
		private class SettingTask extends AsyncTask<Void, Void, String> {

			private Context context;
			private ProgressDialog loading;

			public SettingTask(Context context) {
				this.context = context;

				loading = new ProgressDialog(SettingActivity.this);
				loading.setTitle("ส่งคำสั่ง");
				loading.setMessage("กำลังโหลด...");
				// loading.setCancelable(false);
				loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				busy = true;
			}

			@Override
			protected String doInBackground(Void... params) {
				String param = (switchHeaterMode.isChecked() ? "0" : "1") + "~";
				param += (switchFilterMode.isChecked() ? "0" : "1") + "~";
				param += editTextHeaterMin.getText().toString() + "~";
				param += editTextFilterMax.getText().toString() + "~";
				param += (radioWifiMode1.isChecked() ? "1" : ( radioWifiMode2.isChecked() ? "2" : "3")) + "~";
				param += editTextWifiSSID.getText().toString() + "~";
				param += editTextWifiPassword.getText().toString() + "~";
				param += editTextBoardName.getText().toString() + "~";
				param += editTextStaticIP.getText().toString() + "~";
				param += editTextPort.getText().toString();
				
				return Service.sendHttpRequest(context, "E" + param, Service.SOCKET_TIMEOUT_TRYING);
			}

			@Override
			protected void onPreExecute() {
				loading.show();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String result) {
				loading.dismiss(); 
				
				busy = false;
			}
		}
		
		// คลาสสำหรับ ส่งคำสั่ง
		private class CheckTask extends AsyncTask<Void, Void, String> {

			private Context context;
			private ProgressDialog loading;

			public CheckTask(Context context) {
				this.context = context;

				loading = new ProgressDialog(SettingActivity.this);
				loading.setTitle("ส่งคำสั่ง");
				loading.setMessage("กำลังโหลด...");
				// loading.setCancelable(false);
				loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				
				busy = true;
			}

			@Override
			protected String doInBackground(Void... params) {
				return Service.sendHttpRequest(context, "D", Service.SOCKET_TIMEOUT_TRYING);
			}

			@Override
			protected void onPreExecute() {
				loading.show();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String result) {
				loading.dismiss();
				
				String[] res = result.split("~");
				
				Log.d("checktask", res.toString());
				
				switchHeaterMode.setChecked(res[0].equals("0"));
				switchFilterMode.setChecked(res[1].equals("0"));
				
				editTextHeaterMin.setText(res[2]);
				editTextFilterMax.setText(res[3]);
				
				radioWifiMode1.setChecked(res[4].equals("1"));
				radioWifiMode2.setChecked(res[4].equals("2"));
				radioWifiMode3.setChecked(res[4].equals("3"));
				
				editTextWifiSSID.setText(res[5]);
				editTextWifiPassword.setText(res[6]);
				editTextBoardName.setText(res[7]);
				editTextStaticIP.setText(res[8]);
				editTextPort.setText(res[9]);
				
				busy = false;
			}
		}
}
