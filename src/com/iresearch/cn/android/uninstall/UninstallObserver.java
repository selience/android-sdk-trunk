
package com.iresearch.cn.android.uninstall;

import java.io.File;
import android.os.Build;
import android.util.Log;
import android.content.Context;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @file UninstallObserver.java
 * @create 2013-5-31 上午11:17:49
 * @author lilong@qiyi.com
 * @description TODO 应用卸载监听实现类
 */
public class UninstallObserver {

    private static final String TAG = "UninstallObserver";
    
    private static final String LOCK_FILE = "uninstall.data";
    
    private static final String FEEDBACK_URL = "http://www.diaochapai.com/";
    
    static {
        System.loadLibrary("uninstall");
    }

    public static void startTask(Context context) {
        if (context==null) return; 
        // 创建监听文件
        createLockFile(context, LOCK_FILE);
        // 开启监听任务
        File dirFile = context.getFilesDir(); // /data/data/{package}/files
        final String PATH = dirFile.getParent();    // /data/data/{package}
        final String LOCAL_FILE=dirFile + File.separator + LOCK_FILE; // /data/data/{package}/uninstall.data
        startObserver(LOCAL_FILE, PATH, FEEDBACK_URL, Build.VERSION.SDK_INT);
    }
    
    /**
     * 启动卸载任务监听
     * @param lockfilepath
     * @param observerpath  监听文件路径
     * @param feedbackurl   反馈链接地址
     * @param version       
     * @return
     */
    public static native String startObserver(
            String lockfilepath, String observerpath, 
            String feedbackurl, int version);

    
    /**
     * 卸载回调反馈，常用于数据统计操作
     */
    public static void uninstallCallback() {
        System.out.println("卸载应用成功");
    }
    
    
    /**
     * 创建监听文件，用于进程锁定，避免重复进入再次创建新的进程
     * @param context
     * @param lockfile
     */
    private static void createLockFile(Context context,String lockfile) {
        if (context == null)
            return;
        FileOutputStream out = null;
        try {
            out = context.openFileOutput(lockfile, Context.MODE_PRIVATE);
            Log.i(TAG, lockfile + " create success");
            out.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "can't create FileOutputStream");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
