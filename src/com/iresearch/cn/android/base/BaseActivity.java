package com.iresearch.cn.android.base;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.iresearch.cn.android.log.XLog;
import com.iresearch.cn.android.manager.ActivityStack;
import com.iresearch.cn.android.manager.FragmentStack;
import com.iresearch.cn.android.manager.ViewManager;
import com.iresearch.cn.android.settings.Config;

public abstract class BaseActivity extends FragmentActivity {

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
	public void onBackPressed() {
		XLog.d("onBackPressed");
		if (mStack.stackSize()>1) {
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
	
	
	// TODO content layout resource
	protected abstract int layout();
	
	/*
	 * 装载Fragment到Activity
	 */
	public void replace(Class<BaseFragment> clazz, String tag, Bundle args) {
		mStack.replace(clazz, tag, args);
	}
}
