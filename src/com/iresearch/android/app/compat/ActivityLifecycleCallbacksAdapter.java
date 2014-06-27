
package com.iresearch.android.app.compat;

import android.app.Activity;
import android.os.Bundle;
import com.iresearch.android.log.XLog;
import com.iresearch.android.manager.ActivityStack;

public class ActivityLifecycleCallbacksAdapter implements ActivityLifecycleCallbacksCompat {

    private static final String TAG = "ActivityLifecycle";

    private ActivityStack mActivityStack;

    public ActivityLifecycleCallbacksAdapter() {
        this.mActivityStack = ActivityStack.getInstance();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        XLog.v(TAG, activity.toString());
        // 保存Activity实例
        mActivityStack.pushActivity(activity);
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
        // 移除Activity实例
        mActivityStack.removeActivity(activity);
    }
}
