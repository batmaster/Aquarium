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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * หน้า activity แสดงหน้า select device
 *
 */
public class SelectDeviceActivity extends Activity {
	
	private Button buttonAdd;
	private Button buttonLogout;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_device);
	
		buttonAdd = (Button) findViewById(R.id.buttonAdd);
		buttonAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(SelectDeviceActivity.this);
				dialog.setContentView(R.layout.popup_add_device);
				dialog.setTitle("เพิ่มอุปกรณ์");
				
				final EditText editTextBid = (EditText) dialog.findViewById(R.id.editTextBid);
				
				Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
				buttonCancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				
				Button buttonAdd = (Button) dialog.findViewById(R.id.buttonAdd);
				buttonAdd.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						AddDevicesTask task = new AddDevicesTask(getApplicationContext(), dialog, editTextBid.getText().toString());
						task.execute();
					}
				});
				
				dialog.show();
			}
		});
		
		buttonLogout = (Button) findViewById(R.id.buttonLogout);
		buttonLogout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SharedValues.remove(getApplicationContext(), SharedValues.KEY_USERNAME);
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ViewGroup row = (ViewGroup) listView.getChildAt(position);
				TextView textViewIp = (TextView) row.findViewById(R.id.textViewIp);
				TextView textViewPort = (TextView) row.findViewById(R.id.textViewPort);
				
				SharedValues.setStringPref(getApplicationContext(), SharedValues.KEY_IP, textViewIp.getText().toString());
//				SharedValues.setStringPref(getApplicationContext(), SharedValues.KEY_IP, "192.168.43.38");
				SharedValues.setStringPref(getApplicationContext(), SharedValues.KEY_PORT, textViewPort.getText().toString());
				
				TryToConnectTask task = new TryToConnectTask(getApplicationContext());
				task.execute();
				
			}
		});
	}

	@Override
	protected void onResume() {
		refreshListView();
		
		super.onResume();
	}
	
	private class GetDevicesTask extends AsyncTask<Void, Void, Void> {

		private Context context;
		private List<DeviceListViewRowItem> list;
		
		private ProgressDialog loading;
		
		public GetDevicesTask(Context context) {
			this.context = context;
			
			loading = new ProgressDialog(SelectDeviceActivity.this);
			loading.setTitle("รายการอุปกรณ์");
			loading.setMessage("กำลังโหลด...");
//			loading.setCancelable(false);
			loading.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
		}
		
		@Override
		protected Void doInBackground(Void[] params) {
			list = new ArrayList<DeviceListViewRowItem>();
			
			try {
				String parsed = Request.getDevices(context);
				JSONArray js = new JSONArray(parsed);
				for (int i = 0; i < js.length(); i++) {
					JSONObject jo = js.getJSONObject(i);
					DeviceListViewRowItem item = new DeviceListViewRowItem(jo.getString("bid"), jo.getString("ip"), jo.getString("port"), jo.getString("date"));
					list.add(item);
				}
				
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
			
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			loading.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void message) {
			loading.dismiss();
			
			listView.setAdapter(null);
			DeviceListViewRowAdapter adapter = new DeviceListViewRowAdapter(context, SelectDeviceActivity.this, list);
			listView.setAdapter(adapter);
		}
	}
	
	public void refreshListView() {
		GetDevicesTask task = new GetDevicesTask(getApplicationContext());
		task.execute();
	}
	
	private class AddDevicesTask extends AsyncTask<Void, Void, Void> {

		private Context context;
		private String bid;
		
		private ProgressDialog loading;
		private Dialog outerDialog;
		
		public AddDevicesTask(Context context, Dialog outerDialog, String bid) {
			this.context = context;
			this.outerDialog = outerDialog;
			this.bid = bid;
			
			loading = new ProgressDialog(SelectDeviceActivity.this);
			loading.setTitle("เพิ่มอุปกรณ์");
			loading.setMessage("กำลังโหลด...");
//			loading.setCancelable(false);
			loading.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
		}
		
		@Override
		protected Void doInBackground(Void[] params) {
			try {
				Request.addDevice(context, bid);
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
			
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			loading.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void message) {
			loading.dismiss();
			
			refreshListView();
			
			
			outerDialog.dismiss();
		}
	}
	
	// คลาสสำหรับ แค่ตรวจสอบสถานะล่าสุดในบอร์ด
		private class TryToConnectTask extends AsyncTask<Void, Void, String> {
			
			private Context context;
			private ProgressDialog loading;
			
			public TryToConnectTask(Context context) {
				this.context = context;
				
				loading = new ProgressDialog(SelectDeviceActivity.this);
				loading.setTitle("ตรวจสอบการเชื่อมต่อ");
				loading.setMessage("กำลังโหลด...");
//				loading.setCancelable(false);
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
			}
		}
	
}
