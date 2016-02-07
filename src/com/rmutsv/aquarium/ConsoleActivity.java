package com.rmutsv.aquarium;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ConsoleActivity extends Activity {
	
	private TextView textViewBid;
	private ToggleButton toggleRelayHeater;
	private ToggleButton toggleRelayFilter;
	private Button buttonFeed;
	private Button buttonRefresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_console);
		
		String bid = getIntent().getStringExtra(SharedValues.KEY_BID);
		
		textViewBid = (TextView) findViewById(R.id.textViewBid);
		textViewBid.setText(bid);
		
		toggleRelayHeater = (ToggleButton) findViewById(R.id.toggleRelayHeater);
		toggleRelayHeater.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				
			}
		});
		
		toggleRelayFilter = (ToggleButton) findViewById(R.id.toggleRelayFilter);
		toggleRelayFilter.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				
			}
		});
		
		buttonFeed = (Button) findViewById(R.id.buttonFeed);
		buttonFeed.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		buttonRefresh = (Button) findViewById(R.id.buttonRefresh);
		buttonRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
}
