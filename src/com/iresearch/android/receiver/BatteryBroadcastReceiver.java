package com.iresearch.android.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;

/**
 * @file BatteryBroadcastReceiver.java
 * @create 2013-5-31 下午06:25:25
 * @author lilong
 * @description TODO 监控充电状态的变化
 * 
 * <intent-filter>
 * 		<action android:name="android.intent.action.BATTERY_CHANGED" />
 * </intent-filter>
 */
public class BatteryBroadcastReceiver extends BroadcastReceiver {

	public interface OnBatteryChangedListener {
		
		void onChanged(Intent intent);
    }
	
	private OnBatteryChangedListener onBatteryChangedListener;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (onBatteryChangedListener!=null) {
			onBatteryChangedListener.onChanged(intent);
		}
	}

	public void register(Context context) {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		context.registerReceiver(this, intentFilter);
	}

	public void unregister(Context context) {
		context.unregisterReceiver(this);
	}
	
	public void setOnBatteryChangedListener(OnBatteryChangedListener listener) {
		this.onBatteryChangedListener = listener;
	}
}
