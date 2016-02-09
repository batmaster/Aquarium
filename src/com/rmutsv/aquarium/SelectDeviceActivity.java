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
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_device);
	
		buttonAdd = (Button) findViewById(R.id.buttonAdd);
		buttonAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "ยังเพิ่มอุปกรณ์ไม่ได้", Toast.LENGTH_SHORT).show();
			}
		});
		
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Intent intent = new Intent(getApplicationContext(), ConsoleActivity.class);
//				ViewGroup row = (ViewGroup) listView.getChildAt(position);
//				TextView textViewBid = (TextView) row.findViewById(R.id.textViewBid);
//				intent.putExtra(SharedValues.KEY_BID, textViewBid.getText().toString());
//				startActivity(intent);
				
				Toast.makeText(getApplicationContext(), "ยังไม่เปิดให้บริการ กรุณาเชื่อมต่อบอร์ดโดยตรง", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected void onResume() {
		GetDevicesTask task = new GetDevicesTask(getApplicationContext());
		task.execute();
		
		super.onResume();
	}
	
	private class GetDevicesTask extends AsyncTask<Void, Void, Void> {

		private Context context;
		private List<DeviceListViewRowItem> list;
		
		private ProgressDialog loading;
		
		public GetDevicesTask(Context context) {
			this.context = context;
			
			loading = new ProgressDialog(SelectDeviceActivity.this);
			loading.setTitle("รายการแจ้งเตือน");
			loading.setMessage("กำลังโหลด...");
//			loading.setCancelable(false);
			loading.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
		}
		
		@Override
		protected Void doInBackground(Void[] params) {
			list = new ArrayList<DeviceListViewRowItem>();
			
			try {
				String parsed = Request.getDevices(getApplicationContext());
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
			DeviceListViewRowAdapter adapter = new DeviceListViewRowAdapter(context, list);
			listView.setAdapter(adapter);
		}
	}
	
}
