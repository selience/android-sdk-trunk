package com.iresearch.cn.android.base;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @file BaseGroupAdapter.java
 * @create 2012-9-4 下午12:30:56
 * @author Jacky.Lee
 * @description TODO 封装常用数据适配操作
 */
public abstract class BaseGroupAdapter<T> extends BaseAdapter {

	private LayoutInflater inflater;

	private List<T> dataList = null;

	public BaseGroupAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}

	public BaseGroupAdapter(Context context, List<T> dataList) {
	    this.dataList = dataList;
	    this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		int size = 0;
		if (dataList != null) {
			size += dataList.size();
		}
		return size;
	}

	@Override
	public boolean isEmpty() {
		return dataList!=null && !dataList.isEmpty();
	}

	@Override
	public T getItem(int position) {
		if (dataList == null) {
			return null;
		}
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public final View getView(int position, View v, ViewGroup parent) {
		if (v == null) {
			v = newView(inflater, v, position);
		}

		bindView(v, dataList.get(position));

		return v;
	}

	public List<T> getItems() {
		return dataList;
	}

	public void setItems(List<T> items) {
	    setItems(items, false);
	}
	
	public void setItems(List<T> items, boolean redrawList) {
	    this.dataList=items;
	    if (redrawList) {
            notifyDataSetChanged();
        }
	}
	
	public abstract View newView(LayoutInflater inflater, View v, int position);

	
	public abstract void bindView(View v, T item);

}
