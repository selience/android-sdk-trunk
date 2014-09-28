
package com.iresearch.android.utils;

import android.content.Context;
import java.io.File;
import android.os.Environment;
import org.mariotaku.android.backport.EnvironmentAccessor;

public class UIUtils {

    /**
     * 检查是否安装SD卡
     * 
     * @return
     */
    public static boolean isExternalStorageMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
    
    /**
     * 获取APP数据缓存目录
     * @param context
     * @return
     */
    public static File getExternalCacheDir(Context context) {
        if (isExternalStorageMounted()) {
            Context appContext=context.getApplicationContext();
            File rootDir=EnvironmentAccessor.getExternalCacheDir(appContext);
            if (rootDir!=null && !rootDir.exists()) {
                rootDir.mkdirs();
            }
            return rootDir;
        }
        return null;
    }
    
    /**
     * 获取APP文件存储目录
     * @param context
     * @return
     */
    public static File getExternalFilesDir(Context context) {
        if (isExternalStorageMounted()) {
            Context appContext=context.getApplicationContext();
            File rootDir=EnvironmentAccessor.getExternalFilesDir(appContext, null);
            if (rootDir!=null && !rootDir.exists()) {
                rootDir.mkdirs();
            }
            return rootDir;
        }
        return null;
    }
}
