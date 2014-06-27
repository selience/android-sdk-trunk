
package com.iresearch.android.utils;

import android.os.Handler;
import android.os.Looper;

public class HandlerUtils {

    private static Handler sMainHandler;
    private static Thread sUiThread;

    /**
     * Returns the main handler, which lives in the main thread of the
     * application.
     */
    public static Handler getMainHandler() {
        if (sMainHandler == null) {
            sMainHandler = new Handler(Looper.getMainLooper());
        }
        return sMainHandler;
    }

    /**
     * Runs the specified action on the UI thread. If the current thread is the
     * UI thread, then the action is executed immediately. If the current thread
     * is not the UI thread, the action is posted to the event queue of the UI
     * thread.
     * 
     * @param action the action to run on the UI thread
     */
    public static void runOnUiThread(Runnable action) {
        if (sUiThread == null) {
            sUiThread = Looper.getMainLooper().getThread();
        }
        if (Thread.currentThread() != sUiThread) {
            getMainHandler().post(action);
        } else {
            action.run();
        }
    }
}
