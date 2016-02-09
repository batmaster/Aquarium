package com.rmutsv.aquarium;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private EditText editTextUsername;
	private EditText editTextPassword;
	private Button buttonLogin;
	private Button buttonDirectMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		editTextUsername = (EditText) findViewById(R.id.editTextUsername);
		String username = SharedValues.getStringPref(getApplicationContext(), SharedValues.KEY_USERNAME);
		if (username != null)
			editTextUsername.setText(username);
		
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		
		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		buttonLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CheckUsernameTask task = new CheckUsernameTask(getApplicationContext());
				task.execute();
			}
		});
		
		buttonDirectMode = (Button) findViewById(R.id.buttonDirectMode);
		buttonDirectMode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), DirectModeActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
	private class CheckUsernameTask extends AsyncTask<Void, Void, Integer> {

		private Context context;
		
		private ProgressDialog loading;
		
		public CheckUsernameTask(Context context) {
			this.context = context;
			
			loading = new ProgressDialog(MainActivity.this);
			loading.setTitle("ตรวจสอบชื่อผู้ใช้");
			loading.setMessage("กำลังโหลด...");
//			loading.setCancelable(false);
			loading.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
		}
		
		@Override
		protected Integer doInBackground(Void[] params) {
			try {
				String parsed = Request.checkUsername(getApplicationContext(), editTextUsername.getText().toString(), editTextPassword.getText().toString());
				JSONArray js = new JSONArray(parsed);
				return js.length();
				
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ConnectTimeoutException e) {
				loading.setMessage("เชื่อมต่อเซิร์ฟนานเกินไป");
			} catch (SocketTimeoutException e) {
				loading.setMessage("รอผลตอบกลับนานเกินไป");
			} catch (HttpHostConnectException e) {
				loading.setMessage("เชื่อมต่อเซิร์ฟเวอร์ไม่ได้");
				e.printStackTrace();
			} catch (IOException e) {
				loading.setMessage("มีปัญหาการเชื่อมต่อ");
				e.printStackTrace();
			}
			
			return 0;
		}
		
		@Override
		protected void onPreExecute() {
			loading.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Integer length) {
			loading.dismiss();
			
			if (length > 0) {
				SharedValues.setStringPref(context, SharedValues.KEY_USERNAME, editTextUsername.getText().toString());
				Intent intent = new Intent(getApplicationContext(), SelectDeviceActivity.class);
				startActivity(intent);
				finish();
			}
			else {
				Toast.makeText(context, "ไม่พบชื่อผู้ใช้", Toast.LENGTH_SHORT).show();
			}
			
		}
	}
}
