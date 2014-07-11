
package com.iresearch.android.app.compat;

import android.app.Activity;
import android.os.Bundle;
import com.iresearch.android.log.XLog;
import com.iresearch.android.app.AppManager;

public class ActivityLifecycleCallbacksAdapter implements ActivityLifecycleCallbacksCompat {

    private static final String TAG = "ActivityLifecycle";

    public ActivityLifecycleCallbacksAdapter() {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        XLog.v(TAG, activity.toString());
        // 添加Activity到堆栈
        AppManager.getAppManager().addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        XLog.v(TAG, activity.toString());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        XLog.v(TAG, activity.toString());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        XLog.v(TAG, activity.toString());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        XLog.v(TAG, activity.toString());
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        XLog.v(TAG, activity.toString());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        XLog.v(TAG, activity.toString());
        // 从堆栈中移除Activity
        AppManager.getAppManager().removeActivity(activity);
    }
}
