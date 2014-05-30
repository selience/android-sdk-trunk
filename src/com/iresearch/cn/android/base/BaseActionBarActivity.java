package com.iresearch.cn.android.base;

import java.lang.reflect.Field;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.ViewConfiguration;
import com.iresearch.cn.android.log.XLog;
import com.iresearch.cn.android.manager.ActivityStack;
import com.iresearch.cn.android.manager.FragmentStack;
import com.iresearch.cn.android.manager.ViewManager;
import com.iresearch.cn.android.settings.Config;

public abstract class BaseActionBarActivity extends ActionBarActivity {
	
	private FragmentManager fm;
	private FragmentStack mStack;
	private ViewManager mViewManager;
	private ActivityStack mActivityStack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		XLog.d("onCreate");
		
		fm = getSupportFragmentManager();
		FragmentManager.enableDebugLogging(Config.DEBUG);
		forceShowActionBarOverflowMenu();
		
		mActivityStack=ActivityStack.getInstance();
		mActivityStack.pushActivity(this);
		
		mStack=FragmentStack.newInstance(this, fm, layout());
		mStack.restoreState(savedInstanceState);
		
		mViewManager=new ViewManager(Config.DEBUG);
		mViewManager.onAppStart(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		XLog.d("onStart");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		XLog.d("onRestart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		XLog.d("onResume");
		
		mViewManager.onAppResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		XLog.d("onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		XLog.d("onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		XLog.d("onDestroy");
		
		mViewManager.onAppEnd(this);
		mActivityStack.removeActivity(this);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		XLog.d("onSaveInstanceState");
		mStack.savedState(outState);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		XLog.d("onConfigurationChanged");
	    super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onSupportActionModeStarted(ActionMode mode) {
		Fragment f = getFragment();
		if (f != null) ((BaseActionBarFragment) f).onActionModeStarted(mode);
	}
	
	@Override
	public void onSupportActionModeFinished(ActionMode mode) {
		Fragment f = getFragment();
		if (f != null) ((BaseActionBarFragment) f).onActionModeFinished(mode);
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		XLog.d("onSupportNavigateUp");
		if (mStack.stackSize() > 1) {
			if (getFragment().onNavigateUp()) {
				return true;
			} else {
				mStack.popFragment();
				return true;
			}
		}
		
		return super.onSupportNavigateUp();
	}
	
	@Override
	public void onBackPressed() {
		XLog.d("onBackPressed");
		if (mStack.stackSize() > 1) {
			if (mStack.peekFragment().onBackPressed()) {
				return;
			} else {
				mStack.popFragment();
				return;
			}
		} else {
			if (mStack.peekFragment().onBackPressed()) {
				return;
			} else {
				super.onBackPressed();
			}
		}
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
	
	/*
	 * 装载Fragment到Activity
	 */
	public void replace(Class<? extends BaseFragment> clazz, String tag, Bundle args) {
		mStack.replace(clazz, tag, args);
	}
	
	
	/** 在有 menu按键的手机上面，ActionBar 上的 overflow menu 默认不会出现，只有当点击了 menu按键时才会显示 */
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
