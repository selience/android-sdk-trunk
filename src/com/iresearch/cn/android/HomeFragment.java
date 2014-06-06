package com.iresearch.cn.android;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.iresearch.cn.android.adapter.MenuListAdapter;
import com.iresearch.cn.android.app.iResearch;
import com.iresearch.cn.android.base.BaseFragment;
import com.iresearch.cn.android.log.XLog;
import com.iresearch.cn.android.model.request.TestRequest;
import com.iresearch.cn.android.service.SocketService;
import com.iresearch.cn.android.uninstall.NativeMethod;
import com.iresearch.cn.android.utils.NetworkUtils;
import com.iresearch.cn.android.utils.ToastUtils;
import com.iresearch.cn.android.volley.toolbox.RequestCallback;
import com.iresearch.cn.android.volley.toolbox.RequestManager;

import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;

public class HomeFragment extends BaseFragment implements 
    OnRefreshListener, OnGlobalLayoutListener, OnItemClickListener {

    private ListView mListView;
    private List<String> mDataList;
    private SwipeRefreshLayout mRefreshLayout;
    private MenuListAdapter mListAdapter;

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
        mListAdapter=new MenuListAdapter(mActivity, mDataList);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        searchView.setQueryHint(getText(R.string.main_menu_search_hint));
        searchView.setIconified(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(false); // 默认不展开
        // http://developer.android.com/guide/topics/search/search-dialog.html
        ComponentName componentName=new ComponentName(mActivity, SearchActivity.class);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        
        ShareActionProvider shareProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.menu_share));
        shareProvider.setShareIntent(new Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, "Text I want to share"));
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                ToastUtils.show(mActivity, item.getTitle());
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
            // JNI方法调用
            NativeMethod nativeMethod = new NativeMethod();
            XLog.i("C实现有参的java方法：" + nativeMethod.sayHi("test"));
            XLog.i("C实现两个整数相加方法:" + nativeMethod.add(120, 130));
            XLog.i("C实现数据元素加5的方法:"+ nativeMethod.intMethod(new int[]{2, 5, 8}));
            
            nativeMethod.callPrint();  // C调用静态方法
            nativeMethod.callMethod(); // C调用实例方法
        } else if (position == 2) {
            String url="http://" + NetworkUtils.ipToString(mActivity);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url+":"+SocketService.CONNECTION_POST));
            startActivity(intent);
        } else if (position == 3) {
            Intent mapIntent = new Intent(mActivity, MapViewerActivity.class);
            mapIntent.setData(Uri.parse("wuxian://map?lat="+iResearch.latitude+"&lng="+iResearch.longitude));
            startActivity(mapIntent);
        }
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
    
    public static class SettingsActionProvider extends ActionProvider {
        /** An intent for launching the system settings. */
        private static final Intent sSettingsIntent = new Intent(Settings.ACTION_SETTINGS);
        
        public SettingsActionProvider(Context context) {
            super(context);
        }

        @Override
        public View onCreateActionView() {
            TextView txtView = new TextView(getContext());
            txtView.setText("setting");
            return txtView;
        }
        
        @Override
        public boolean onPerformDefaultAction() {
            getContext().startActivity(sSettingsIntent);
            return true;
        }
    }
}
