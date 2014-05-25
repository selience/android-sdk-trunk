
package com.iresearch.cn.android;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.iresearch.cn.android.base.BaseFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

public class HomeFragment extends BaseFragment implements OnRefreshListener, OnGlobalLayoutListener {

    private ListView mListView;
    private List<String> mDataList;
    private SwipeRefreshLayout mRefreshLayout;
    private ArrayAdapter<String> mListAdapter;

    private String[] values = new String[] {
            "Android", "iPhone", "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X", "Linux",
            "OS/2"
    };

    @Override
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View convertView = inflater.inflate(R.layout.home, null);
        mListView = (ListView) convertView.findViewById(android.R.id.list);
        mRefreshLayout = (SwipeRefreshLayout) convertView.findViewById(R.id.swipeLayout);
        mRefreshLayout.setColorScheme(android.R.color.holo_green_dark, android.R.color.holo_orange_dark, 
                android.R.color.holo_blue_dark, android.R.color.holo_red_dark);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);
        
        // 构造数据源
        mDataList = new ArrayList<String>();
        mDataList.addAll(Arrays.asList(values));
        // 设置列表适配器
        mListAdapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mDataList);
        mListView.setAdapter(mListAdapter);
        
        return convertView;
    }

    @Override
    public void onRefresh() {
        // 执行下拉刷新方法
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDataList.add(0, "Windows8");
                mListAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);
            }
        }, 2000L);
    }

    @Override
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onGlobalLayout() {
        // 反射调整下拉滑动距离
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        Float mDistanceToTriggerSync = Math.min(
                ((View) mRefreshLayout.getParent()).getHeight() * 0.6f,
                120 * metrics.density);

        try {
            // Set the internal trigger distance using reflection.
            Field field = SwipeRefreshLayout.class.getDeclaredField("mDistanceToTriggerSync");
            field.setAccessible(true);
            field.setFloat(mRefreshLayout, mDistanceToTriggerSync);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Only needs to be done once so remove listener.
        ViewTreeObserver obs = mRefreshLayout.getViewTreeObserver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            obs.removeOnGlobalLayoutListener(this);
        } else {
            obs.removeGlobalOnLayoutListener(this);
        }
    }
}
