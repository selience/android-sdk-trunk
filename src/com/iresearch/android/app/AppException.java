package com.iresearch.android.app;

import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import java.util.Locale;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import com.iresearch.android.utils.DeviceUtils;
import com.iresearch.android.utils.UIUtils;
import java.lang.Thread.UncaughtExceptionHandler;
import android.provider.Settings.SettingNotFoundException;

/**
 * @file ExceptionHandler.java
 * @create 2012-8-15 下午5:08:28
 * @author Jacky.Lee
 * @description 异常处理类，当程序发生Uncaught异常的时候, 由该类 来接管程序,并记录 发送错误报告.
 */
public class AppException implements UncaughtExceptionHandler {
    private static final String TAG = "ExceptionHandler";

    /** 程序的Context对象 */
    private Context mContext;
    /** 系统默认的UncaughtException处理类 */
    private UncaughtExceptionHandler mDefaultHandler;
    
    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     */
    public AppException(Context context) {
        this.mContext=context;
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
            try {
                // sleep一会后结束程序
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e(TAG, "Error: ", e);
            }
            // 如果自己处理了异常,则不会弹出错误对话框,则需要手动退出App
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(-1); // 非正常退出
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成, 可以根据自己的情况来自定义异常处理逻辑
     * 
     * @return true 代表处理该异常,不再向上抛异常， false代表不处理该异常(可以将该log信息存储起来)然后交给上层(这里就到了系统的异常处理)去处理，
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        final String msg = ex.getLocalizedMessage();
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "程序出错啦:" + msg, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        
        // 收集APP异常信息
        String crashReport=collectCrashReport(ex);
        // 保存错误报告文件
        saveCrashInfoToFile(crashReport);
        
        return true;
    }
    
    /**
     * 保存APP异常信息到文件
     * @param crashReport
     * @return
     */
    private void saveCrashInfoToFile(String crashReport) {
        OutputStream output=null;
        try {
            String fileName = "crash-" + System.currentTimeMillis() + ".log";
            // 获取异常信息存储路径
            File rootFile = UIUtils.getExternalFilesDir(mContext);
            if (rootFile==null) {
                rootFile = mContext.getFilesDir();
            } 
            File crashFile = new File(rootFile, fileName);
            output = new FileOutputStream(crashFile);
            output.write(crashReport.getBytes());
            output.flush();
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing report file.", e);
        } finally {
            try {
                if (output!=null) {
                    output.close();
                }
            } catch (IOException ex) { }
        }
    }
    
    /**
     * 收集APP崩溃异常报告
     * @return
     */
    @SuppressWarnings("deprecation")
    private String collectCrashReport(Throwable tr) {
        StringBuilder sb = new StringBuilder();
        
        PackageInfo packInfo = AppContext.getInstance().getPackageInfo();;
        sb.append("App version: " + packInfo.versionName + "(v" + packInfo.versionCode + ")\n");
        sb.append("Device locale: " + Locale.getDefault().toString() + "\n\n");
        sb.append("Android ID: " + DeviceUtils.generateDeviceId(mContext));

        // phone information
        sb.append("PHONE SPECS\n");
        sb.append("model: " + Build.MODEL + "\n");
        sb.append("brand: " + Build.BRAND + "\n");
        sb.append("product: " + Build.PRODUCT + "\n");
        sb.append("device: " + Build.DEVICE + "\n\n");

        // android information
        sb.append("PLATFORM INFO\n");
        sb.append("Android " + Build.VERSION.RELEASE + " " + Build.ID + " (build " + Build.VERSION.INCREMENTAL + ")\n");
        sb.append("build tags: " + Build.TAGS + "\n");
        sb.append("build type: " + Build.TYPE + "\n\n");

        // settings
        sb.append("SYSTEM SETTINGS\n");
        String networkMode = null;
        ContentResolver resolver = mContext.getContentResolver();
        try {
            if (Settings.Secure.getInt(resolver, Settings.Global.WIFI_ON) == 0) {
                networkMode = "DATA";
            } else {
                networkMode = "WIFI";
            }
            sb.append("network mode: " + networkMode + "\n");
            sb.append("HTTP proxy: " + Settings.Secure.getString(resolver, Settings.Secure.HTTP_PROXY) + "\n\n");
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }

        sb.append("STACK TRACE FOLLOWS\n\n");

        sb.append(getStackMessageString(tr));
        sb.append("\n\n");
        sb.append(getStackTraceString(tr));

        return sb.toString();
    }
    
    private String getStackMessageString(Throwable e) {
        StringBuilder message = new StringBuilder();
        StackTraceElement[] stack = e.getStackTrace();
        StackTraceElement stackLine = stack[(stack.length - 1)];
        message.append(stackLine.getFileName());
        message.append(":");
        message.append(stackLine.getLineNumber());
        message.append(":");
        message.append(stackLine.getMethodName());
        message.append(" ");
        message.append(e.getMessage());
        return message.toString();
    }

    private String getStackTraceString(Throwable tr) {
        if (tr == null) return "";
        
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            tr.printStackTrace(pw);
            String error = sw.toString();
            sw.close();
            pw.close();
            return error;
        } catch (IOException e) {
            return e.getMessage();
        }
    }
}