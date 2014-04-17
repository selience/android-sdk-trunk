package com.iresearch.cn.android.base;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import com.iresearch.cn.android.log.XLog;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;

/**
 * @file BaseActionBarFragment.java
 * @create 2012-8-20 上午11:23:16
 * @author Jacky.Lee
 * @description 增加ActionBar栏相关操作
 */
public abstract class BaseActionBarFragment extends BaseFragment {

	protected ActionBarActivity getActionBarActivity() {
		final Activity activity = getActivity();
		if (activity!=null && activity instanceof ActionBarActivity) {
			return (ActionBarActivity) activity;
		}
		return null;
	}
	
	protected ActionBar getActionBar() {
		final ActionBarActivity activity = getActionBarActivity();
		if (activity != null) return activity.getSupportActionBar();
		return null;
	}

	protected ActionMode startActionMode(Callback callback) {
		final ActionBarActivity activity = getActionBarActivity();
		if (activity != null) return activity.startSupportActionMode(callback);
		return null;
	}
	
	protected void invalidateOptionsMenu() {
		final ActionBarActivity activity = getActionBarActivity();
		if (activity != null) activity.supportInvalidateOptionsMenu();
	}
	
	protected void onActionModeStarted(ActionMode mode) {
		XLog.d(TAG, "onActionModeStarted");
	}
	
	protected void onActionModeFinished(ActionMode mode) {
		XLog.d(TAG, "onActionModeFinished");
	}
	
	@Override
	protected void setTitle(CharSequence title) {
		super.setTitle(title);
		ActionBar actionBar=getActionBar();
		if (actionBar!=null) actionBar.setTitle(title);
	}
	
	/*
	 * 监听UP按钮事件
	 */
	public boolean onNavigateUp() {
		return false;
	}
}
