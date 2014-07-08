package com.iresearch.android.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.iresearch.android.app.AppContext;
import com.iresearch.android.log.XLog;

/**
 * @file BaseFragment.java
 * @create 2012-8-20 上午11:23:16
 * @author Jacky.Lee
 * @description Fragment基类，对Fragment栈的管理
 */
public abstract class BaseFragment extends Fragment {
	protected String TAG = "BaseFragment";
	private static final String STATE_TITLE = "_title";
	
	private CharSequence title; // 设置标题
	
	protected Activity mActivity;
	
	/**
	 * 必须有此构造方法，Layout中的静态Fragment会调用,旋转屏幕也会调用 ;
	 */
	public BaseFragment() {
		TAG = getClass().getSimpleName();
    }
	
    public AppContext getApplication() {
    	final Activity activity = getActivity();
		if (activity != null) return (AppContext) activity.getApplication();
		return null;
    }
    
    public ContentResolver getContentResolver() {
		final Activity activity = getActivity();
		if (activity != null) return activity.getContentResolver();
		return null;
	}
    
    public SharedPreferences getSharedPreferences(final String name, final int mode) {
		final Activity activity = getActivity();
		if (activity != null) return activity.getSharedPreferences(name, mode);
		return null;
	}
    
    public Object getSystemService(final String name) {
		final Activity activity = getActivity();
		if (activity != null) return activity.getSystemService(name);
		return null;
	}
    
    public void registerReceiver(final BroadcastReceiver receiver, final IntentFilter filter) {
		final Activity activity = getActivity();
		if (activity == null) return;
		activity.registerReceiver(receiver, filter);
	}
    
    public void unregisterReceiver(final BroadcastReceiver receiver) {
		final Activity activity = getActivity();
		if (activity == null) return;
		activity.unregisterReceiver(receiver);
	}
    
    public void startService(Intent service) {
    	final Activity activity = getActivity();
		if (activity == null) return;
		activity.startService(service);
    }
    
    public void stopService(Intent name) {
    	final Activity activity = getActivity();
		if (activity == null) return;
		activity.stopService(name);
    }
    
    public void setProgressBarIndeterminateVisibility(final boolean visible) {
		final Activity activity = getActivity();
		if (activity == null) return;
		activity.setProgressBarIndeterminateVisibility(visible);
	}

    
    @Override
	public void onInflate(Activity activity, AttributeSet attrs,
			Bundle savedInstanceState) {
    	XLog.d(TAG, "onInflate");
    	super.onInflate(activity, attrs, savedInstanceState);
    }
    
	@Override
	public void onAttach(Activity activity) {
		XLog.d(TAG, "onAttach");
		super.onAttach(activity);
		this.mActivity=activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		XLog.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
		setMenuVisibility(true);
		
		if (savedInstanceState!=null) {
			restoreState(savedInstanceState); // 恢复Fragment标题
			this.title=savedInstanceState.getCharSequence(STATE_TITLE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		XLog.d(TAG, "onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		XLog.d(TAG, "onViewCreated");
		super.onViewCreated(view, savedInstanceState);
		
		// 设置Fragment标题
		if (title != null) {
			setTitle(title);
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		XLog.d(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
	    XLog.d(TAG, "setUserVisibleHint:" + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
	public void onStart() {
		XLog.d(TAG, "onStart");
		super.onStart();
	}
	
	@Override
	public void onResume() {
		XLog.d(TAG, "onResume");
		super.onResume();
	}

	@Override
	public void onPause() {
		XLog.d(TAG, "onPause");
		super.onPause();
	}

	@Override
	public void onStop() {
		XLog.d(TAG, "onStop");
		super.onStop();
	}
	
	@Override
	public void onDestroyView() {
		XLog.d(TAG, "onDestroyView");
		super.onDestroyView();
	}
	
	@Override
	public void onDestroy() {
		XLog.d(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		XLog.d(TAG, "onDetach");
		super.onDetach();
		mActivity=null;
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
		XLog.d(TAG, "onSaveInstanceState");
		storeSavedState(outState); // 保存Fragment标题
		outState.putCharSequence(STATE_TITLE, title);
		super.onSaveInstanceState(outState);
    }
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		XLog.d(TAG, "onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * 获取当前Fragment标题
	 * @return 标题
	 */
	protected CharSequence getTitle() {
		return title;
	}
	
	/**
	 * 设置Fragment标题
	 * @param textId 资源ID 
	 */
	protected void setTitle(int textId) {
		setTitle(getString(textId));
	}
	
	/**
	 * 设置Fragment标题
	 * @param title 标题名称
	 */
	protected void setTitle(CharSequence title) {
		this.title = title;
		final Activity activity = getActivity();
		if (activity!=null) activity.setTitle(title);
	}
	
	/**
	 * 装载Fragment
	 */
	protected void replace(Class<? extends BaseFragment> clazz, String tag, Bundle args) {
	    final Activity activity = getActivity();
	    if (activity == null) return;
	    
	    if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).replace(clazz, tag, args);
        } else if (activity instanceof BaseActionBarActivity) {
            ((BaseActionBarActivity) activity).replace(clazz, tag, args);
        }
	}
	
	/**
	 * 存储Fragment状态
	 */
	protected void storeSavedState(Bundle outState) {
	}
	
	/**
	 * 恢复Fragment状态
	 * @param savedState
	 */
	protected void restoreState(Bundle savedState) {
	}
	
	
	/**
	 * 是否为单例模式
	 * @return true 单例；false 非单例
	 */
	public boolean isSingleton() {
		return true;
	}
	
	
	/**
	 * 是否清空后退栈
	 * @return true 清空；false 不清空
	 */
	public boolean isCleanStack() {
		return false;
	}
	
	/**
	 * 子类back键处理方法，如需特殊处理，请覆写该方法
	 */
	public boolean onBackPressed() {
		return false;
	}
}
