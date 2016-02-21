package com.smarthome.zerocool.smarthome;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.smarthome.zerocool.smarthome.MQTTservice;

public class BootReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{	
		Log.d(getClass().getCanonicalName(), "onReceive");
		context.startService(new Intent(context, MQTTservice.class));
	}
}