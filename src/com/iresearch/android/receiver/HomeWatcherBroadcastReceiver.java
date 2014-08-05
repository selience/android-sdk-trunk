package com.iresearch.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * @file HomeWatcherBroadcastReceiver.java
 * @create 2013-7-8 上午11:58:24
 * @author lilong
 * @description TODO Home键监听封装 
 * 
 * <intent-filter>
 * 		<action android:name="android.intent.action.CLOSE_SYSTEM_DIALOGS" />
 * </intent-filter>
 */
public class HomeWatcherBroadcastReceiver extends BroadcastReceiver {

	private final String SYSTEM_DIALOG_REASON_KEY = "reason";
	private final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
	private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

	public interface OnHomePressedListener {
		void onHomePressed();

		void onHomeLongPressed();
	}
	
	private OnHomePressedListener onHomePressedListener;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
			String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
			if (reason!=null && onHomePressedListener!=null) {
				if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
					onHomePressedListener.onHomePressed();	// 短按home键
				} else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
					onHomePressedListener.onHomeLongPressed(); // 长按home键
				}
			}
		}
	}

	/**
	 * 设置监听
	 */
	public void setOnHomePressedListener(OnHomePressedListener listener) {
		this.onHomePressedListener = listener;
	}

	/**
	 * 开始监听，注册广播
	 */
	public void register(Context context) {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		context.registerReceiver(this, intentFilter);
	}

	/**
	 * 停止监听，注销广播
	 */
	public void unregister(Context context) {
		context.unregisterReceiver(this);
	}
}
