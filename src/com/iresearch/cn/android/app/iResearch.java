package com.iresearch.cn.android.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.os.Process;
import android.os.StrictMode;
import com.iresearch.cn.android.constants.Config;
import com.iresearch.cn.android.log.XLog;
import com.iresearch.cn.android.utils.ManifestUtils;
import com.iresearch.cn.android.volley.toolbox.RequestManager;
import com.iresearch.cn.android.crash.CrashHandler;
import com.iresearch.cn.android.crash.CrashHandler.OnCrashHandlerListener;

public class iResearch extends Application implements OnCrashHandlerListener {
    
    public static double latitude=39.90960456049752;   // 纬度    
    public static double longitude=116.3972282409668;  // 经度
    
	/**
     * A singleton instance of the application class for easy access in other places
     */
	private static iResearch instance;

	@Override
	public void onCreate() {
		super.onCreate();
		// initialize the singleton
		instance = this;
		
		if (isRunning()) {
			initialize();
		}
	}

	/*
	 * 初始化操作
	 */
	private void initialize() {
	    XLog.enableDebugLogging(Config.DEBUG);
	    RequestManager.initializeWith(this);
	    
		checkStrictMode();
		sendCrashReports();
	}
	
	@Override
	public void onTerminate() {
		// FIXME: 根据android文档，onTerminate不会在真实机器上被执行到
		// 因此这些清理动作需要再找合适的地方放置，以确保执行。
		instance = null;
		super.onTerminate();
	}
	
	/**
     * @return iResearch singleton instance
     */
	public static synchronized iResearch getInstance() {
		return instance;
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void checkStrictMode() {
		// FIXME: StrictMode类在1.6以下的版本中没有，会导致类加载失败。因此将这些代码设成关闭状态，仅在做性能调试时才打开。
		// NOTE: StrictMode模式需要2.3+ API支持。设置严苛模式；
		if (Config.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) { 
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
		    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build()); 
		}
	}
	
	// 防止多进程重复执行初始化操作
	private boolean isRunning() {
	    return ManifestUtils.checkIfIsAppRunning(this, Process.myPid(), "com.iresearch.cn.android");
	}

	// 收集设备崩溃日志信息
    private void sendCrashReports() {
        if (!Config.DEBUG) { 
            /** 在Release状态下开启日志收集功能，以精确分析应用性能 */
            CrashHandler mCrashHandler=new CrashHandler(this);
            mCrashHandler.setOnCrashHandlerListener(this);
        }
    }
	
    @Override
    public void handleCrashResponse(String message) {
        // TODO 处理异常信息，常见操作是将奔溃日志发送到服务器端用于分析
    }
}
