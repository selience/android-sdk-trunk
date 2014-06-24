
package com.iresearch.android.app;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.Application;
import android.annotation.TargetApi;
import com.iresearch.android.log.XLog;
import android.app.Application.ActivityLifecycleCallbacks;

/**
 * API Level>=14 系统用于监听应用中所有Activity的运行情况
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ActivityLifecycleCallbackImpl implements ActivityLifecycleCallbacks {

    private static final String TAG = "ActivityLifecycle";

    public ActivityLifecycleCallbackImpl() {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        XLog.v(TAG, activity.toString());
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
    }

    public void register(Application mAppClient) {
        mAppClient.registerActivityLifecycleCallbacks(this);
    }

    public void unregister(Application mAppClient) {
        mAppClient.unregisterActivityLifecycleCallbacks(this);
    }
}
