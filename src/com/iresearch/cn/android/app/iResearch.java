package com.iresearch.cn.android.app;

import java.util.Iterator;
import java.util.List;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Application;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.os.Build;
import android.os.Process;
import android.os.StrictMode;
import com.iresearch.cn.android.volley.toolbox.RequestManager;

public class iResearch extends Application {

	private static iResearch instance;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		
		if (needCallInit()) {
			initialize();
		}
	}

	/*
	 * 初始化操作
	 */
	private void initialize() {
	    RequestManager.initializeWith(this);
	    
		checkStrictMode();
	}
	
	@Override
	public void onTerminate() {
		// FIXME: 根据android文档，onTerminate不会在真实机器上被执行到
		// 因此这些清理动作需要再找合适的地方放置，以确保执行。
		instance = null;
		super.onTerminate();
	}
	
	public static iResearch getInstance() {
		return instance;
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void checkStrictMode() {
		// FIXME: StrictMode类在1.6以下的版本中没有，会导致类加载失败。因此将这些代码设成关闭状态，仅在做性能调试时才打开。
		// NOTE: StrictMode模式需要2.3+ API支持。设置严苛模式；
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) { 
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
		    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build()); 
		}
	}
	
	/*
	 * 防止多进程重复执行初始化操作
	 */
	private boolean needCallInit() {
		try {
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
			Iterator<RunningAppProcessInfo> process = list.iterator();
			while (process.hasNext()) {
				RunningAppProcessInfo info = (RunningAppProcessInfo) process.next();
				if (info.pid == Process.myPid()) {
					String processName = info.processName;
					if ("com.iresearch.cn.android".equals(processName)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}
}
