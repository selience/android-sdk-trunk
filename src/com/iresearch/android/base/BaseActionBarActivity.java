
package com.iresearch.android.base;

import java.lang.reflect.Field;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.ViewConfiguration;
import com.iresearch.android.app.compat.MainLifecycleDispatcher;
import com.iresearch.android.log.XLog;
import com.iresearch.android.manager.FragmentStack;

public abstract class BaseActionBarActivity extends ActionBarActivity {
    protected String TAG = "BaseActionBarActivity";

    private FragmentStack mStack;
    private boolean isConfigChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        XLog.d(TAG, "onCreate");

        forceShowActionBarOverflowMenu();
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
        XLog.d(TAG, "onNewIntent");
    }

    @Override
    protected void onStart() {
        super.onStart();
        XLog.d(TAG, "onStart");
        MainLifecycleDispatcher.get().onActivityStarted(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        XLog.d(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        XLog.d(TAG, "onResume");
        MainLifecycleDispatcher.get().onActivityResumed(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        XLog.d(TAG, "onPause");
        MainLifecycleDispatcher.get().onActivityPaused(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        XLog.d(TAG, "onStop");
        MainLifecycleDispatcher.get().onActivityStopped(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XLog.d(TAG, "onDestroy");
        MainLifecycleDispatcher.get().onActivityDestroyed(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        XLog.d(TAG, "onSaveInstanceState");
        mStack.savedState(outState);
        super.onSaveInstanceState(outState);
        // FIXME 兼容API低于11版本
        isConfigChange = true;
        
        MainLifecycleDispatcher.get().onActivitySaveInstanceState(this, outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        XLog.d(TAG, "onConfigurationChanged");
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
    public void onSupportActionModeStarted(ActionMode mode) {
        Fragment f = getFragment();
        if (f != null)
            ((BaseActionBarFragment) f).onActionModeStarted(mode);
    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {
        Fragment f = getFragment();
        if (f != null)
            ((BaseActionBarFragment) f).onActionModeFinished(mode);
    }

    @Override
    public boolean onSupportNavigateUp() {
        XLog.d(TAG, "onSupportNavigateUp");
        if (mStack.stackSize() > 1) {
            if (getFragment().onNavigateUp()) {
                return true;
            } else {
                mStack.popFragment();
                return true;
            }
        }
        onBackTask();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        XLog.d(TAG, "onBackPressed");
        if (mStack.stackSize() > 1) {
            if (mStack.peekFragment().onBackPressed()) {
                return;
            } else {
                mStack.popFragment();
                return;
            }
        }
        onBackTask();
    }

    protected void onBackTask() {
        XLog.d(TAG, "onBack");
        super.onBackPressed();
    }
    
    private BaseActionBarFragment getFragment() {
        Fragment f = mStack.peekFragment();
        if (f != null && f instanceof BaseActionBarFragment) {
            return (BaseActionBarFragment) f;
        }
        return null;
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
     * 装载Fragment,添加到栈管理队列
     */
    public void replace(Class<? extends BaseFragment> clazz, String tag, Bundle args) {
        mStack.replace(clazz, tag, args);
    }

    /** 在有 menu按键的手机上面，ActionBar上的 overflow menu默认不会出现，只有当点击了 menu按键时才会显示 */
    private void forceShowActionBarOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ignored) {
        }
    }
}
