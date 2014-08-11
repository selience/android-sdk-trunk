package com.iresearch.android.service;

import android.os.Build;
import android.annotation.TargetApi;
import com.android.sdk.log.DebugLog;
import com.android.sdk.utils.Toaster;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * 确认开启通知监听服务
 * 
 * Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
 * startActivity(intent);
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService  {

	private static final String TAG = NotificationService.class.getSimpleName();
	
	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		String message = "ID :" + sbn.getId() + "--" + sbn.getNotification().tickerText + "--" + sbn.getPackageName(); 
		DebugLog.i(TAG, message);
		Toaster.show(getBaseContext(), message);
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		String message = "ID :" + sbn.getId() + "--" + sbn.getNotification().tickerText + "--" + sbn.getPackageName();
		DebugLog.i(TAG, message);
		Toaster.show(getBaseContext(), message);
	}

}
