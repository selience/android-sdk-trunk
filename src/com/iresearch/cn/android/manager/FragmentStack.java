package com.iresearch.cn.android.manager;

import java.util.Iterator;
import java.util.Stack;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.iresearch.cn.android.base.BaseFragment;

/**
 * @file FragmentStack.java
 * @create 2012-9-20 下午6:04:05
 * @author Jacky.Lee
 * @description Fragment状态管理类
 */
public final class FragmentStack {
	private static final String STACK_TAG = "stack";
	
	private static final byte[] lock = new byte[1];
    
	private int layout;
	private Stack<BaseFragment> mStack;
	private FragmentManager mFragmentManager;
    private FragmentActivity mFragmentActivity;
    private FragmentTransaction mTransaction;
    
    public static FragmentStack newInstance(FragmentActivity activity, 
    		FragmentManager fragmentManager, int layout) {
    	return new FragmentStack(activity, fragmentManager, layout);
    }
    
	private FragmentStack(FragmentActivity activity, FragmentManager fragmentManager, int layout) {
		this.layout = layout;
		this.mStack=new Stack<BaseFragment>();
		this.mFragmentActivity=activity;
		this.mFragmentManager=fragmentManager;
	}
	
	public void replace(Class<? extends BaseFragment> clazz, String tag, Bundle args) {
		BaseFragment f = (BaseFragment) getFragment(clazz, tag, args);
		// 清空栈顶Fragment
		cleanBackStack(f);
		// 挂载当前Fragment
		attachFragment(f, tag);
		// 提交Fragment事务
		commitTransactions();
		// 添加Fragment到栈
		addFragment(f);
	}
	
	private BaseFragment getFragment(Class<? extends BaseFragment> clazz, String tag, Bundle args) {
		BaseFragment f = (BaseFragment) mFragmentManager.findFragmentByTag(tag);
        if (f == null || !f.isSingleton()) {
            f = (BaseFragment) Fragment.instantiate(mFragmentActivity, clazz.getName(), args);
        } else {
            resetFragment(f, tag);
        }
        return f;
    }
	
	/*
	 * 处理栈顶Fragment
	 */
	private void resetFragment(BaseFragment f, String tag) {
		if (mStack.size() > 0) {
			BaseFragment element = mStack.peek();
			// 当前Fragment位于栈顶直接返回
			if (tag.equals(element.getTag())) {
			    if (element.isCleanStack() || element.isSingleton()) {
			        return;    
			    }
			}
			
			// 当前Fragment位于栈底，则弹出上面Fragment
			if (mStack.contains(f)) {
				while (!tag.equals(mStack.peek().getTag())) {
					synchronized (lock) {
						mStack.pop();						
					}
					mFragmentManager.popBackStack();
				}
				return;
			}
		}
	}
	
	/*
	 * 挂载Fragment到Activity
	 */
	private void attachFragment(Fragment f, String tag) {
        if (f != null) {
            if (f.isDetached()) {
                ensureTransaction();
                mTransaction.attach(f);
                addToBackStack(tag);
            } else if (!f.isAdded()) {
                ensureTransaction();
                mTransaction.replace(layout, f, tag);
                addToBackStack(tag);
            }
        }
    }

	/*
     * 添加FragmentManager后退栈
     */
    private void addToBackStack(String tag) {
    	if (mStack.size() > 0) {
    		mTransaction.addToBackStack(tag);
    	}
    }
	
    /*
     * 清空栈顶Fragment
     */
    private void cleanBackStack(BaseFragment f) {
    	// 清空栈顶Fragment
		if (f.isCleanStack()) {
			while (mStack.size() > 0) {
				synchronized (lock) {
					mStack.pop(); 
				}
				mFragmentManager.popBackStack();
			}
		}
    }
    
	/*
	 * 移除Fragment
	 */
    protected void detachFragment(Fragment f) {
        if (f != null && !f.isDetached()) {
            ensureTransaction();
            mTransaction.detach(f);
        }
    }

    /*
     * 提交Fragment事务
     */
    protected void commitTransactions() {
        if (mTransaction != null && !mTransaction.isEmpty()) {
        	mTransaction.commitAllowingStateLoss();
        	mTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }
	
    /*
     * 初始化Fragment事务
     */
    protected FragmentTransaction ensureTransaction() {
        if (mTransaction == null) {
        	mTransaction = mFragmentManager.beginTransaction();
        	mTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }

        return mTransaction;
    }
    
	/*
	 * 存储Fragment状态
	 */
	public final void savedState(Bundle outState) {
		int index = 0;
		String[] stackTags=new String[mStack.size()];
		Iterator<BaseFragment> iterator=mStack.iterator();
		while (iterator.hasNext()) {
			stackTags[index++]=iterator.next().getTag();
		}
		outState.putStringArray(STACK_TAG, stackTags);
	}

	/*
	 * 恢复Fragment状态
	 */
	public final void restoreState(Bundle outState) {
		if (outState != null) {
			BaseFragment f = null;
			String[] stackTags=outState.getStringArray(STACK_TAG);
			for (String tag : stackTags) {
				f =(BaseFragment)mFragmentManager.findFragmentByTag(tag);
				mStack.add(f);
			}
		}
	}
	
	/*
	 * 获取栈顶Fragment个数
	 */
	public int stackSize() {
		synchronized (lock) {
			return mStack.size();
		}
	}
	
	/*
	 * 添加Fragment到栈顶
	 */
	private void addFragment(BaseFragment f) {
		synchronized (lock) {
			mStack.add(f);
		}
	}
	
	/*
	 * 获取栈顶Fragment
	 */
	public BaseFragment peekFragment() {
		synchronized (lock) {
			return mStack.peek();
		}
	}
	
	/*
	 * 弹出栈顶Fragment
	 */
	public boolean popFragment() {
		if (mStack.size() > 1) {
			synchronized (lock) {
				mStack.pop();
			}
			mFragmentManager.popBackStackImmediate();
			return true;
		}
		return false;
	}
}
