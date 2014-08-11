package com.iresearch.android.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import com.android.sdk.utils.SystemUtils;

/**
 * Request thread executor. We use a thread pool to execute the thread.
 * 
 * @author lilong@qiyi.com
 * @version 1.0.0.0
 * 
 */
public class RequestExecutor {

    private static ExecutorService threadPool;
    
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        // 以原子方式增加线程
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "Application thread #" + mCount.getAndIncrement());
        }
    };

    static {
        /**
         * Return an ExecutorService (global to the entire application) that may be
         * used by clients when running long tasks in the background.
         */
        final int THREAD_NUMBER = SystemUtils.getDefaultThreadPoolSize(5);
        threadPool = Executors.newFixedThreadPool(THREAD_NUMBER, sThreadFactory);
    }
    
    public static ExecutorService getThreadExecutor() {
        return threadPool;
    }
    
    /**
     * Executor thread task
     */
    public static void doAsync(Runnable mRunnable) {
        threadPool.execute(mRunnable);
    }
    
    public static Future<?> doAsync(Callable<?> mCallable) {
        return threadPool.submit(mCallable);
    }
}