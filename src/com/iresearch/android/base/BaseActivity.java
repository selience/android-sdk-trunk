
package com.iresearch.android.base;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.iresearch.android.app.compat.MainLifecycleDispatcher;
import com.iresearch.android.log.L;
import com.iresearch.android.manager.FragmentStack;

public abstract class BaseActivity extends FragmentActivity {
    protected String TAG = "BaseActivity";

    private boolean isConfigChange;
    private FragmentStack mStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        L.d(TAG, "onCreate");

        FragmentManager fm = getSupportFragmentManager();
        FragmentManager.enableDebugLogging(false);

        mStack = FragmentStack.newInstance(this, fm, layout());
        mStack.restoreState(savedInstanceState);

        MainLifecycleDispatcher.get().onActivityCreated(this, savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        L.d(TAG, "onNewIntent");
    }

    @Override
    protected void onStart() {
        super.onStart();
        L.d(TAG, "onStart");
        MainLifecycleDispatcher.get().onActivityStarted(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        L.d(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.d(TAG, "onResume");
        MainLifecycleDispatcher.get().onActivityResumed(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        L.d(TAG, "onPause");
        MainLifecycleDispatcher.get().onActivityPaused(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        L.d(TAG, "onStop");
        MainLifecycleDispatcher.get().onActivityStopped(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.d(TAG, "onDestroy");
        MainLifecycleDispatcher.get().onActivityDestroyed(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        L.d(TAG, "onSaveInstanceState");
        mStack.savedState(outState);
        super.onSaveInstanceState(outState);
        // FIXME 兼容API低于11版本
        isConfigChange = true;
        
        MainLifecycleDispatcher.get().onActivitySaveInstanceState(this, outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        L.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean isChangingConfigurations() {
        // TODO 是否因为屏幕发生改变导致Activity重新构建
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            return super.isChangingConfigurations();
        } else {
            return isConfigChange;
        }
    }

    @Override
    public void onBackPressed() {
        L.d(TAG, "onBackPressed");
        if (onBackTaskStart()) {
        	return;
        }
        if (mStack.stackSize() > 1) {
            if (mStack.peekFragment().onBackPressed()) {
                return;
            } else {
                mStack.popFragment();
                return;
            }
        }
        onBackTaskEnd();
    }

    protected boolean onBackTaskStart() {
    	L.d(TAG, "onBackTaskStart");
    	return false;
    }
    
    protected void onBackTaskEnd() {
        L.d(TAG, "onBackTaskEnd");
        super.onBackPressed();
    }
    
    // TODO content layout resource
    protected abstract int layout();

    /**
     * 装载Fragment
     */
    public void replace(final int layout, Fragment fragment, String tag) {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        mTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        mTransaction.replace(layout, fragment, tag);
        mTransaction.commitAllowingStateLoss();
        mTransaction = null;

        mFragmentManager.executePendingTransactions();
    }

    /**
     * 装载Fragment，添加到栈管理队列
     */
    public void replace(Class<? extends BaseFragment> clazz, String tag, Bundle args) {
        mStack.replace(clazz, tag, args);
    }
}
