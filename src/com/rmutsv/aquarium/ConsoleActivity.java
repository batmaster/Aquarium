package com.rmutsv.aquarium;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * หน้า activity แสดงปุ่มควบคุมบอร์ด
 *
 */
public class ConsoleActivity extends Activity {

	// ประกาศตัวแปรสำหรับปุ่มสวิตซ์ทุกอัน
	private TextView textViewBid;
	private TextView textViewTemp;
	private Switch switchRelayHeater;
	private ToggleButton toggleRelayHeater;
	private Switch switchRelayFilter;
	private ToggleButton toggleRelayFilter;
	private Button buttonFeed;
	private Button buttonRefresh;
	private Button buttonSetting;
	
	// ประกาศตัวแปรที่จำเป็นต้องใช้
	private OnCheckedChangeListener listener;
	private boolean busy = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_console);
		
		// เชื่อมตัวแปรกับชิ้นส่วนในหน้า xml
		textViewBid = (TextView) findViewById(R.id.textViewBid);
		
		textViewTemp = (TextView) findViewById(R.id.textViewTemp);

		listener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// พวกฟังก์ชั่นที่เรียกใช้เวลากดสวิตซ์ จะเป็นแนวนี้หมดคือ
				// ตัวแปร busy เช็คว่าหลังจากส่งคำสั่งไปครั้งก่อนหน้า ได้รับผลลัพธ์มาแล้วหรือยัง
				// ถ้ายัง ให้แสดงขึ้นว่า "กรุณารอประมวลผล"
				// # เขียนอธิบายแค่ครั้งเดียวนะสำหรับ activity นี้
				if (!busy) {
					CommandTask task = new CommandTask(getApplicationContext());
					task.execute();
				}
				else {
					Toast.makeText(getApplicationContext(), "กรุณารอประมวลผล", Toast.LENGTH_SHORT).show();
				}
			}
		};

		switchRelayHeater = (Switch) findViewById(R.id.switchRelayHeater);
		toggleRelayHeater = (ToggleButton) findViewById(R.id.toggleRelayHeater);

		switchRelayFilter = (Switch) findViewById(R.id.switchRelayFilter);
		toggleRelayFilter = (ToggleButton) findViewById(R.id.toggleRelayFilter);

		buttonFeed = (Button) findViewById(R.id.buttonFeed);
		buttonFeed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FeedTask task = new FeedTask(getApplicationContext());
				task.execute();
			}
		});

		buttonRefresh = (Button) findViewById(R.id.buttonRefresh);
		buttonRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!busy) {
					CheckStatusTask task = new CheckStatusTask(getApplicationContext());
					task.execute();
				}
				else {
					Toast.makeText(getApplicationContext(), "กรุณารอประมวลผล", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		buttonSetting = (Button) findViewById(R.id.buttonSetting);
		buttonSetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
				startActivity(intent);
			}
		});
		
		// อันนี้คือฟื้นฟูปุ่มสวิตซ์ให้ตรงกับผลลัพธ์ที่ได้จากบอร์ด #getIntent().getXXXExtra() คือรับต่าที่ส่งมาจาก activity ก่อนหน้า
		String A = getIntent().getStringExtra("A");
		textViewBid.setText(A.substring(4, A.indexOf("-")));
		refresh(A);
	}
	
	private void refresh(String A) {
		// เช็คค่า 4 หลักที่ได้จากเรียก relay=A ไปที่บอร์ด
		boolean heaterAuto = A.charAt(0) == '1';
		boolean filterAuto = A.charAt(1) == '1';
		boolean relay1 = A.charAt(2) == '0';
		boolean relay2 = A.charAt(3) == '0';
		
		switchRelayHeater.setOnCheckedChangeListener(null);
		toggleRelayHeater.setOnCheckedChangeListener(null);
		switchRelayFilter.setOnCheckedChangeListener(null);
		toggleRelayFilter.setOnCheckedChangeListener(null);

		//ตั้งค่าปุ่มสวิตซ์ที่ 4 ให้สอดคล้องกัน
		switchRelayHeater.setChecked(heaterAuto);
		switchRelayFilter.setChecked(filterAuto);
		toggleRelayHeater.setChecked(relay1);
		toggleRelayFilter.setChecked(relay2);
		
		toggleRelayHeater.setEnabled(!heaterAuto);
		toggleRelayFilter.setEnabled(!filterAuto);
		
		switchRelayHeater.setOnCheckedChangeListener(listener);
		toggleRelayHeater.setOnCheckedChangeListener(listener);
		switchRelayFilter.setOnCheckedChangeListener(listener);
		toggleRelayFilter.setOnCheckedChangeListener(listener);
		
		// แสดงเลขอุณหภูมิ
		String temp = A.substring(A.indexOf("-") + 1);
		textViewTemp.setText(temp);
	}

	@Override
	protected void onResume() {
		if (!busy) {
			CheckStatusTask task = new CheckStatusTask(getApplicationContext());
			task.execute();
		}
		else {
			Toast.makeText(getApplicationContext(), "กรุณารอประมวลผล", Toast.LENGTH_SHORT).show();
		}
		busy = false;
		super.onResume();
	}
	
	// คลาสสำหรับ แค่ตรวจสอบสถานะล่าสุดในบอร์ด
	private class CheckStatusTask extends AsyncTask<Void, Void, String> {

		private Context context;
		private ProgressDialog loading;

		public CheckStatusTask(Context context) {
			this.context = context;

			loading = new ProgressDialog(ConsoleActivity.this);
			loading.setTitle("ตรวจสอบสถานะ");
			loading.setMessage("กำลังโหลด...");
			// loading.setCancelable(false);
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
				refresh(result);
			} else {
				Toast.makeText(context, "เกิดข้อผิดพลาด", Toast.LENGTH_SHORT).show();
			}
			
			busy = false;
		}
	}
	
	// คลาสสำหรับ ส่งคำสั่ง และตรวจสอบสถานะหลังจากนั้น ในบอร์ด
	private class CommandTask extends AsyncTask<Void, Void, String> {

		private Context context;
		private ProgressDialog loading;

		public CommandTask(Context context) {
			this.context = context;

			loading = new ProgressDialog(ConsoleActivity.this);
			loading.setTitle("ส่งคำสั่ง");
			loading.setMessage("กำลังโหลด...");
			// loading.setCancelable(false);
			loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		}

		@Override
		protected String doInBackground(Void... params) {
			String heaterAuto = switchRelayHeater.isChecked() ? "1" : "0";
			String filterAuto = switchRelayFilter.isChecked() ? "1" : "0";
			String relay1 = !toggleRelayHeater.isChecked() ? "1" : "0";
			String relay2 = !toggleRelayFilter.isChecked() ? "1" : "0";
			
			return Service.sendHttpRequest(context, "B" + heaterAuto + filterAuto + relay1 + relay2, Service.SOCKET_TIMEOUT_TRYING);
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
				refresh(result);
			} else {
				Toast.makeText(context, "เกิดข้อผิดพลาด", Toast.LENGTH_SHORT).show();
			}
			
			busy = false;
		}
	}
	
	private class FeedTask extends AsyncTask<Void, Void, String> {

		private Context context;
		private ProgressDialog loading;

		public FeedTask(Context context) {
			this.context = context;

			loading = new ProgressDialog(ConsoleActivity.this);
			loading.setTitle("ส่งคำสั่ง");
			loading.setMessage("กำลังโหลด...");
			// loading.setCancelable(false);
			loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		}

		@Override
		protected String doInBackground(Void... params) {
			return Service.sendHttpRequest(context, "C", Service.SOCKET_TIMEOUT_TRYING);
		}

		@Override
		protected void onPreExecute() {
			loading.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			loading.dismiss();

			if (result.equals("OK")) {
				Toast.makeText(context, "ให้อาหารเรียบร้อย", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "เกิดข้อผิดพลาด", Toast.LENGTH_SHORT).show();
			}
			
			busy = false;
		}
	}

}
