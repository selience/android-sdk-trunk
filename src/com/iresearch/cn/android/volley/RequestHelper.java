package com.iresearch.cn.android.volley;

import java.io.File;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

/**
 * Manager for the queue
 * 
 * @author Jacky Lee
 *
 */
public class RequestHelper {
	
    private static final String DEFAULT_CACHE_DIR = "photos";
    
    private static final int DEFAULT_DISK_USAGE_BYTES = 10 * 1024 * 1024;
    
	/**
	 * the queue :-)
	 */
	private static RequestQueue mRequestQueue;

	/**
	 * Nothing to see here.
	 */
	private RequestHelper() {
	 // no instances
	} 

	/**
	 * @param context
	 * 			application context
	 */
	public static void init(Context context) {
	    // 设置本地缓存目录和最高缓存限制
	    File cacheDir = getDiskCacheDir(context, DEFAULT_CACHE_DIR);
	    if (!cacheDir.exists()) cacheDir.mkdirs();
	    Cache cache = new DiskBasedCache(cacheDir, DEFAULT_DISK_USAGE_BYTES);
	    // 设置网络请求线程并发数目
	    HttpStack httpStack = new HurlStack();
	    Network network = new BasicNetwork(httpStack);
		mRequestQueue = new RequestQueue(cache, network);
		// 完成相关工作线程的创建开启
		mRequestQueue.start();
	}

	/**
	 * @return
	 * 		instance of the queue
	 * @throws
	 * 		IllegalStatException if init has not yet been called
	 */
	public static RequestQueue getRequestQueue() {
	    if (mRequestQueue != null) {
	        return mRequestQueue;
	    } else {
	        throw new IllegalStateException("Not initialized");
	    }
	}
	
	public static File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                        ? getExternalCacheDir(context).getPath() : context.getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }
	
	public static File getExternalCacheDir(Context context) {
        if (Build.VERSION.SDK_INT >= 8) {
           return context.getExternalCacheDir();
       }
       final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
       return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
   }
}
