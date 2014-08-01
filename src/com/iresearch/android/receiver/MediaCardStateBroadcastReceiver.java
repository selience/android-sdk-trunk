/**
 * 
 */
package com.iresearch.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.iresearch.android.log.DebugLog;
import com.iresearch.android.utils.Toaster;

/**
 * @file MediaCardStateBroadcastReceiver.java
 * @create 2012-9-20 上午11:24:33
 * @author lilong
 * @description 监听SDCard装载状态
 */
public class MediaCardStateBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "MediaCardStateBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		DebugLog.d(TAG, "Media state changed, reloading resource managers");
		if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) {
		    Toaster.show(context, "存储卡已被卸载::" + intent.getData().getPath());
		} else if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
		    Toaster.show(context, "存储卡已被装载::" + intent.getData().getPath());
		}
	}

	/* 
	 <intent-filter>
	     <action android:name="android.intent.action.MEDIA_MOUNTED" />
	     <action android:name="android.intent.action.MEDIA_EJECT" />
	     <action android:name="android.intent.action.MEDIA_UNMOUNTED" />
	     <action android:name="android.intent.action.MEDIA_SHARED" />
	     <action android:name="android.intent.action.MEDIA_SCANNER_STARTED" />
	     <action android:name="android.intent.action.MEDIA_SCANNER_FINISHED" />
	     <action android:name="android.intent.action.MEDIA_REMOVED" />
	     <action android:name="android.intent.action.MEDIA_BAD_REMOVAL" />
	     <data android:scheme="file" />
     </intent-filter>
    */
}
