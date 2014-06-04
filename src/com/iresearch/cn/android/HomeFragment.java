
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.android.volley.VolleyError;
import com.iresearch.cn.android.base.BaseFragment;
import com.iresearch.cn.android.log.XLog;
import com.iresearch.cn.android.model.request.TestRequest;
import com.iresearch.cn.android.uninstall.NativeMethod;
import com.iresearch.cn.android.uninstall.UninstallObserver;
import com.iresearch.cn.android.utils.ToastUtils;
import com.iresearch.cn.android.volley.toolbox.RequestCallback;
import com.iresearch.cn.android.volley.toolbox.RequestManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

public class HomeFragment extends BaseFragment implements 
    OnRefreshListener, OnGlobalLayoutListener, OnItemClickListener {

    private ListView mListView;
    private List<String> mDataList;
    private SwipeRefreshLayout mRefreshLayout;
    private ArrayAdapter<String> mListAdapter;

    @Override
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View convertView = inflater.inflate(R.layout.home, null);
        mListView = (ListView) convertView.findViewById(android.R.id.list);
        mListView.setOnItemClickListener(this);
        mRefreshLayout = (SwipeRefreshLayout) convertView.findViewById(R.id.swipeLayout);
        mRefreshLayout.setColorScheme(android.R.color.holo_green_dark, android.R.color.holo_orange_dark, 
                android.R.color.holo_blue_dark, android.R.color.holo_red_dark);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);
        
        // 构造数据源
        mDataList = new ArrayList<String>();
        String[] mResources=getResources().getStringArray(R.array.main_list_item); 
        mDataList.addAll(Arrays.asList(mResources));
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
                mListAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);
            }
        }, 2000L);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            //Queue use custom listener
            RequestManager.queue()
                    .useBackgroundQueue()
                    .addRequest(new TestRequest(), mRequestCallback)
                    .start();
        } else if (position == 1) {
            // 启动卸载应用监听
            UninstallObserver.startTask(mActivity);
            // JNI方法调用
            NativeMethod nativeMethod = new NativeMethod();
            System.out.println("C实现有参的java方法：" + nativeMethod.sayHi("test"));
            System.out.println("C实现两个整数相加方法:" + nativeMethod.add(120, 130));
            System.out.println("C实现数据元素加5的方法:"+ nativeMethod.intMethod(new int[]{2, 5, 8}));
            
            nativeMethod.callPrint();  // C调用静态方法
            nativeMethod.callMethod(); // C调用实例方法
        }
    }
    
    private RequestCallback<String, Void> mRequestCallback = new RequestCallback<String, Void>() {
        @Override
        public Void doInBackground(String response) {
            XLog.d("\n"+response.toString());
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            ToastUtils.show(mActivity, "Toast from UI");
        }

        @Override
        public void onError(VolleyError error) {
            XLog.e(error.toString());
        }
    };
    
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
