package com.iresearch.cn.android.manager;

import android.app.Activity;
import android.os.Debug;

public class ViewManager {

	private boolean enabled;
	
	public ViewManager(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * 开启视图层次优化调试
	 */
	public void onAppStart(Activity activity) {
		if (enabled) {
			ViewServer.get(activity).addWindow(activity);
		}
	}

	/**
	 * 改变视图层次焦点
	 */
	public void onAppResume(Activity activity) {
		if (enabled) {
			ViewServer.get(activity).setFocusedWindow(activity);
		}
	}

	/**
	 * 终止视图层次优化调试
	 */
	public void onAppEnd(Activity activity) {
		if (enabled) {
			ViewServer.get(activity).removeWindow(activity);
		}
	}

	/**
	 * 开始trace性能调试
	 */
	public void startTrace(String traceName) {
		if (enabled) {
			Debug.startMethodTracing(traceName);
		}
	}
	
	/**
	 * 终止性能分析,调试代码会生成trace文件
	 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>  
	 */
	public void stopTrace() {
		if (enabled) {
			Debug.stopMethodTracing();
		}
	}
}
