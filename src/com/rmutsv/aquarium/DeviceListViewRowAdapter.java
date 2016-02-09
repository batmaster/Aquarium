package com.rmutsv.aquarium;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * เอาไว้ปลับค่าที่ได้จาก server มาแสดงเป็น listview ในหน้า SelectDevice activity
 *
 */
public class DeviceListViewRowAdapter extends ArrayAdapter<DeviceListViewRowItem> {
	
	private Context context;
	private List<DeviceListViewRowItem> list;

	public DeviceListViewRowAdapter(Context context, List<DeviceListViewRowItem> list) {
		super(context, R.layout.listview_row_device, list);
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.listview_row_device, parent, false);
		
		TextView textViewBid = (TextView) row.findViewById(R.id.textViewBid);
		textViewBid.setText(list.get(position).getBid());
		
		TextView textViewIp = (TextView) row.findViewById(R.id.textViewIp);
		textViewIp.setText(list.get(position).getIp());
		
		TextView textViewPort = (TextView) row.findViewById(R.id.textViewPort);
		textViewPort.setText(list.get(position).getPort());
		
		TextView textViewLastTime = (TextView) row.findViewById(R.id.textViewLastTime);
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d = format.parse(list.get(position).getLastTime());
			Log.d("d", d + "");
			
			textViewLastTime.setText(getDateDiff(d, new Date()));
			
		} catch (ParseException e) {
			
		}
		
		Button buttonRemove = (Button) row.findViewById(R.id.buttonRemove);
		buttonRemove.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getContext(), list.get(position).getBid() + " ยังลบไม่ได้", Toast.LENGTH_SHORT).show();
			}
		});
		
		return row;
	}
	
	// ฟังก์ชั่นหรับค่าเวลาที่บันทึกไว้ เทียบว่าจากปัจจุบัน ผ่านไปแล้วนานเท่าไหร่
	private String getDateDiff(Date before, Date after) {
	    long diffInMillies = after.getTime() - before.getTime();
	    Log.d("diff", after + "");
	    Log.d("diff", before + "");
	    
	    if (TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS) < 60) {
	    	return TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS) + " น.";
	    }
	    else if (TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS) < 24) {
	    	long hours = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	    	diffInMillies -= TimeUnit.MILLISECONDS.convert(hours, TimeUnit.HOURS);
	    	long mins = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
    		return hours + " ชม. " + mins + " น.";
	    }
	    else {
	    	long days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	    	diffInMillies -= TimeUnit.MILLISECONDS.convert(days, TimeUnit.DAYS);
	    	long hours = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    		return days + " วัน " + hours + " ชม.";
	    }
	}
}
