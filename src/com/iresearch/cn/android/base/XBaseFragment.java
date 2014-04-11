package com.iresearch.cn.android.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import com.iresearch.cn.android.log.XLog;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;

/**
 * @file XBaseFragment.java
 * @create 2012-8-20 上午11:23:16
 * @author Jacky.Lee
 * @description 增加ActionBar栏相关操作
 */
public class XBaseFragment extends BaseFragment {

	public ActionBar getActionBar() {
		final Activity activity = getActivity();
		if (activity != null) { 
			ActionBarActivity activityImpl = ((ActionBarActivity) activity);
			return activityImpl.getSupportActionBar();
		}
		return null;
	}

	public ActionMode startActionMode(Callback callback) {
		final Activity activity = getActivity();
		if (activity != null) {
			ActionBarActivity activityImpl = ((ActionBarActivity) activity);
			return activityImpl.startSupportActionMode(callback);
		}
		return null;
	}
	
	public void onActionModeStarted(ActionMode mode) {
		XLog.d(TAG, "onActionModeStarted");
	}
	
	public void onActionModeFinished(ActionMode mode) {
		XLog.d(TAG, "onActionModeFinished");
	}

	public void invalidateOptionsMenu() {
		final Activity activity = getActivity();
		if (activity != null) { 
			ActionBarActivity activityImpl = ((ActionBarActivity) activity);
			activityImpl.supportInvalidateOptionsMenu(); 
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// ActionBar添加菜单项
		setHasOptionsMenu(true);
		setMenuVisibility(true);
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
