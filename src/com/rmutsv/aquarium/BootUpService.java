package com.rmutsv.aquarium;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.app.Service;

public class BootUpService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		registerReceiver(new Receiver(), new IntentFilter(Intent.ACTION_TIME_TICK));
	}
	
}