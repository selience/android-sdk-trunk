
package com.iresearch.android.app.compat;

import android.app.Activity;
import android.os.Bundle;
import com.iresearch.android.log.DebugLog;
import com.iresearch.android.app.AppManager;

public class ActivityLifecycleCallbacksAdapter implements ActivityLifecycleCallbacksCompat {

    private static final String TAG = "ActivityLifecycle";

    public ActivityLifecycleCallbacksAdapter() {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        DebugLog.v(TAG, activity.toString());
        // 添加Activity到堆栈
        AppManager.getAppManager().addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        DebugLog.v(TAG, activity.toString());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        DebugLog.v(TAG, activity.toString());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        DebugLog.v(TAG, activity.toString());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        DebugLog.v(TAG, activity.toString());
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        DebugLog.v(TAG, activity.toString());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        DebugLog.v(TAG, activity.toString());
        // 从堆栈中移除Activity
        AppManager.getAppManager().removeActivity(activity);
    }
}
