package com.iresearch.android.manager;

import java.util.Stack;
import android.app.Activity;

/**
 * @file ActivityStack.java
 * @create 2012-9-20 下午6:04:05
 * @author Jacky.Lee
 * @description Activity窗口管理类
 */
public class ActivityStack {
	
	private Stack<Activity> activityStack;
	//定义一个私有的静态全局变量来保存该类的唯一实例
	private static ActivityStack instance;
	
	/*
	 * 构造函数必须是私有的 
	 * 这样在外部便无法使用 new 来创建该类的实例 
	 */
	private ActivityStack() {
		this.activityStack = new Stack<Activity>();
	}

	public synchronized static ActivityStack getInstance() {
		//这里可以保证只实例化一次 
        //即在第一次调用时实例化 
        //以后调用便不会再实例化 
		if (instance == null) {
			instance = new ActivityStack();
		}
		return instance;
	}

	/**
	 * 将当前Activity压入栈顶
	 */
	public void pushActivity(Activity activity) {
		if (activity!=null) {
			activityStack.push(activity);
		}
	}
	
	/**
	 * 移除指定Activity
	 */
	public void removeActivity(Activity activity) {
		if (activity!=null) {
			activityStack.remove(activity);
		}
	}
	
	/**
	 * 移除当前栈顶的Activity
	 */
	public void popActivity() {
		if (!activityStack.isEmpty()) {
			activityStack.pop();
		}
	}

	/**
	 *	获取当前栈顶Activity 
	 */
	public Activity peekActivity() {
		if (!activityStack.isEmpty()) {
			return activityStack.peek();
		}
		return null;
	}
	
	/**
	 * 	清空栈中存储的Activity
	 */
	public void clearActivity() {
		activityStack.clear();
	}

	/**
	 * 栈中存储Activity数目
	 */
	public int getStackSize() {
		return activityStack.size();
	}
	
	/**
	 * 栈中存储Activity是否为空
	 */
	public boolean isEmpty() {
		return activityStack.isEmpty();
	}
}