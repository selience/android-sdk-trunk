package com.iresearch.android.app;

import android.app.Application;
import android.content.pm.PackageInfo;
import com.activeandroid.ActiveAndroid;
import com.android.volley.Volley;
import org.mariotaku.android.log.DebugLog;
import com.android.volley.core.RequestManager;
import com.iresearch.android.constants.Config;
import org.mariotaku.android.utils.ManifestUtils;
import org.mariotaku.android.utils.StorageOptions;
import org.mariotaku.android.utils.Utils;
import org.mariotaku.android.app.AppException;
import android.content.pm.PackageManager.NameNotFoundException;
import org.mariotaku.android.app.compat.ActivityLifecycleCallbacksCompat;
import org.mariotaku.android.app.compat.ApplicationHelper;
import org.mariotaku.android.app.compat.ActivityLifecycleCallbacksAdapter;

/**
 * 
 * @file AppContext.java
 * @create 2012-8-15 下午5:08:28
 * @author lilong
 * @description TODO Application基类，存储全局数据
 */
public class AppContext extends Application {
    private static final boolean DEBUG = Config.DEBUG;
    
    public static double latitude=39.90960456049752;   // 纬度    
    public static double longitude=116.3972282409668;  // 经度
    
    private ActivityLifecycleCallbacksCompat mCallback;
    
	/**
     * A singleton instance of the application class for easy access in other places
     */
	private static AppContext instance;

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
	    Volley.setLoggable(DEBUG);
	    DebugLog.enableDebugLogging(DEBUG);
	    StorageOptions.getInstance().init(this);
	    
	    Utils.initializeAsyncTask();
	    ActiveAndroid.initialize(this);
	    RequestManager.initializeWith(this);

		// 注册监听Activity生命周期变化
		mCallback=new ActivityLifecycleCallbacksAdapter();
		ApplicationHelper.registerActivityLifecycleCallbacks(this, mCallback);
		
		// 在Release模式下开启日志收集功能，以精确分析应用性能；开发模式下默认关闭以精确查看异常信息；
		if (!DEBUG) {
		    Thread.setDefaultUncaughtExceptionHandler(new AppException(this));
		}
	}
	
	@Override
	public void onLowMemory() {
	    super.onLowMemory();
	}
	
	@Override
	public void onTerminate() {
		// FIXME: 根据android文档，onTerminate不会在真实机器上被执行到
		// 因此这些清理动作需要再找合适的地方放置，以确保执行。
		instance = null;
		// 取消注册监听Activity声明周期变化
		ApplicationHelper.unregisterActivityLifecycleCallbacks(this, mCallback);
		// 释放ActiveAndroid数据对象
		ActiveAndroid.dispose();
		super.onTerminate();
	}
	
	/**
     * @return AppContext singleton instance
     */
	public static synchronized AppContext getInstance() {
		return instance;
	}
	
	// 防止多进程重复执行初始化操作
	private boolean isRunning() {
	    return ManifestUtils.checkIfIsAppRunning(this, "com.iresearch.android");
	}

    /**
     * 获取App安装包信息
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try { 
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {    
            e.printStackTrace(System.err);
        } 
        if(info == null) info = new PackageInfo();
        return info;
    }
}
