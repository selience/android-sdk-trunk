package com.iresearch.cn.android.crash;

import android.util.Log;
import android.content.Context;
import com.iresearch.cn.android.utils.DeviceUtils;
import com.iresearch.cn.android.utils.ManifestUtils;

/**
 * @file CrashHandler.java
 * @create 2012-8-15 下午5:08:28
 * @author Jacky.Lee
 * @description 异常处理类，当程序发生Uncaught异常的时候, 由该类 来接管程序,并记录 发送错误报告.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

	/** Debug Log tag */
	private static final String TAG = "CrashHandler";
    
	/** 程序的Context对象 */
    private Context mContext;
	/** 系统默认的UncaughtException处理类 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	/** 发送Crash日志到服务器监听函数  */
	private OnCrashHandlerListener mCrashHandlerListener;
	
	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 */
	public CrashHandler(Context context) {
	    this.mContext=context;
		Thread.setDefaultUncaughtExceptionHandler(this);
		mDefaultHandler=Thread.getDefaultUncaughtExceptionHandler();
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler!=null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
		    // 如果自己处理了异常,则不会弹出错误对话框,则需要手动退出app
			try {
				// sleep一会后结束程序
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "Error: ", e);
			}
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(10); // 非正常退出
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成, 可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @return true 代表处理该异常,不再向上抛异常， false
	 *         代表不处理该异常(可以将该log信息存储起来)然后交给上层(这里就到了系统的异常处理)去处理，
	 *         简单来说就是true不会弹出那个错误提示框，false就会弹出
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}

		// 收集当前设备的唯一标识
		String deviceId=DeviceUtils.generateDeviceId(mContext);
		// 获取当前应用版本号
		int version=ManifestUtils.getApplicationForVersionCode(mContext);
		// 收集应用崩溃信息
		String trace=StackFormatting.collectCrashReport(mContext, version+"", deviceId, ex);
		// 发送错误报告到服务器
		sendCrashReportsToServer(trace);

		return true;
	}

	/**
     * 使用HTTP Post发送错误报告到服务器 上传的时候还可以将该app的version，该手机的机型等信息一并发送的服务器
     * Android的兼容性众所周知，所以可能错误不是每个手机都会报错，还是有针对性的去debug比较好；
     */
	private void sendCrashReportsToServer(String message) {
	    if (mCrashHandlerListener != null) {
            mCrashHandlerListener.handleCrashResponse(message);
        }
	}

	public void setOnCrashHandlerListener(OnCrashHandlerListener mCrashHandlerListener) {
		this.mCrashHandlerListener = mCrashHandlerListener;
	}
	
	
	public interface OnCrashHandlerListener {
		/**
         * Processes the responses send crash log to the HTTP server
         * 
         */
		void handleCrashResponse(String message);
	}
}
