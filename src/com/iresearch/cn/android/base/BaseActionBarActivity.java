package com.iresearch.cn.android.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import com.iresearch.cn.android.log.XLog;
import com.iresearch.cn.android.manager.ActivityStack;
import com.iresearch.cn.android.manager.FragmentStack;

public class BaseActionBarActivity extends ActionBarActivity {
	
	private FragmentManager fm;
	private FragmentStack mStack;
	private ActivityStack mActivityStack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		XLog.d("onCreate");
		
		fm = getSupportFragmentManager();
		FragmentManager.enableDebugLogging(false);
		
		mActivityStack = ActivityStack.getInstance();
		mActivityStack.pushActivity(this);
		
		mStack = FragmentStack.newInstance(this, fm, 0);
		mStack.restoreState(savedInstanceState);
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
		mActivityStack.removeActivity(this);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		XLog.d("onSaveInstanceState");
		mStack.savedState(outState);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onSupportActionModeStarted(ActionMode mode) {
		Fragment f = getFragment();
		if (f != null) ((XBaseFragment)f).onActionModeStarted(mode);
	}
	
	@Override
	public void onSupportActionModeFinished(ActionMode mode) {
		Fragment f = getFragment();
		if (f != null) ((XBaseFragment)f).onActionModeFinished(mode);
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		XLog.d("onSupportNavigateUp");
		if (mStack.stackSize()>1) {
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
		if (mStack.stackSize()>1) {
			if (mStack.peekFragment().onBackPress()) {
				return;
			} else {
				mStack.popFragment();
				return;
			}
		}
		super.onBackPressed();
	}
	
	private XBaseFragment getFragment() {
		Fragment f = mStack.peekFragment();
		if (f != null && f instanceof XBaseFragment) {
			return (XBaseFragment) f;
		}
		return null;
	}
}
