package com.iresearch.android.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.Listener;
import com.android.volley.VolleyError;
import com.android.volley.core.RequestCallback;
import com.android.volley.core.RequestManager;
import com.android.volley.core.RequestOptions;
import com.iresearch.android.MapViewerActivity;
import com.iresearch.android.SearchActivity;
import com.iresearch.android.R;
import com.iresearch.android.SendActivity;
import com.iresearch.android.adapter.MenuListAdapter;
import com.iresearch.android.app.AppContext;
import com.iresearch.android.base.BaseFragment;
import com.iresearch.android.crop.Crop;
import com.iresearch.android.log.DebugLog;
import com.iresearch.android.model.request.TestRequest;
import com.iresearch.android.service.NotificationService;
import com.iresearch.android.service.SocketService;
import com.iresearch.android.tools.accessor.EnvironmentAccessor;
import com.iresearch.android.ui.AlertDialogFragment.AlertDialogListener;
import com.iresearch.android.uninstall.NativeMethod;
import com.iresearch.android.uninstall.UninstallObserver;
import com.iresearch.android.utils.IntentUtils;
import com.iresearch.android.utils.NetworkUtils;
import com.iresearch.android.utils.Toaster;
import com.iresearch.android.zing.view.CaptureActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;

public class HomeFragment extends BaseFragment implements OnRefreshListener, OnItemClickListener {

    private static final int REQUEST_CODE_QRCODE = 0x2000;
    private static final int REQUEST_CODE_PICK_IMAGE = 0x2001;
    private static final int REQUEST_CODE_TAKE_PHOTO = 0x2002;
    
    private ListView mListView;
    private List<String> mDataList;
    private SwipeRefreshLayout mRefreshLayout;

    private File tempFile;
    private Handler mHandler;
    private MenuListAdapter mListAdapter;

    @Override
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View convertView = inflater.inflate(R.layout.home, container, false);
        mListView = (ListView) convertView.findViewById(android.R.id.list);
        mListView.setOnItemClickListener(this);
        mRefreshLayout = (SwipeRefreshLayout) convertView.findViewById(R.id.swipeLayout);
        mRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark, android.R.color.holo_orange_dark, 
                android.R.color.holo_blue_dark, android.R.color.holo_red_dark);
        mRefreshLayout.setOnRefreshListener(this);
        
        // 构造数据源
        mHandler = new Handler();
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
        mHandler.postDelayed(new Runnable() {
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
        searchView.setIconified(true);
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
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                Toaster.show(mActivity, item.getTitle());
                return true;
            case R.id.menu_refresh:
                setRefreshActionItem(item, true);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionItem(item, false);
                    }
                }, 2000L);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void setRefreshActionItem(MenuItem refreshItem, boolean refreshing) {
        if (refreshItem != null) {
            if (refreshing) {
                MenuItemCompat.setActionView(refreshItem, R.layout.actionbar_indeterminate_progress);
            } else {
                MenuItemCompat.setActionView(refreshItem, null);
            }
        }
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            //Queue use custom listener
            RequestManager.queue().useBackgroundQueue().addRequest(new TestRequest(), 
                    new RequestCallback<String, String>() {
                    @Override
                    public String doInBackground(String response) {
                        return Thread.currentThread().getName()+"@"+response;
                    }

                    @Override
                    public void onPostExecute(String result) {
                        Toaster.show(mActivity, result);
                    }
            });
        } else if (position == 1) {
            // 启动卸载应用监听
            UninstallObserver.startTask(mActivity);
            // JNI方法调用
            NativeMethod nativeMethod = new NativeMethod();
            DebugLog.i("C实现有参的java方法：" + nativeMethod.sayHi("test"));
            DebugLog.i("C实现两个整数相加方法:" + nativeMethod.add(120, 130));
            DebugLog.i("C实现数据元素加5的方法:"+ nativeMethod.intMethod(new int[]{2, 5, 8}));
            
            nativeMethod.callPrint();  // C调用静态方法
            nativeMethod.callMethod(); // C调用实例方法
        } else if (position == 2) {
            // 启动socket服务，监听本地4392端口
            startService(new Intent(mActivity, SocketService.class));
            // 打开本地界面
            String url="http://" + NetworkUtils.ipToString(mActivity);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url+":"+SocketService.CONNECTION_POST));
            startActivity(intent);
        } else if (position == 3) {
            Intent mapIntent = new Intent(mActivity, MapViewerActivity.class);
            mapIntent.setData(Uri.parse("wuxian://map?lat="+AppContext.latitude+"&lng="+AppContext.longitude));
            startActivity(mapIntent);
        } else if (position == 4) { // 下载远程文件
            String url="http://dldir1.qq.com/qqfile/qq/QQ6.0/11743/QQ6.0.exe";
            if (EnvironmentAccessor.isExternalStorageAvailable()) {
                File rootDir=new File(EnvironmentAccessor.getExternalCacheDir(mActivity), RequestOptions.FILE_CACHE_PATH);
                if (!rootDir.exists()) rootDir.mkdirs();
                String storeFilePath=rootDir.getPath() + File.separator + "QQ6.0.exe";
                // 启动文件下载
                RequestManager.downloader()
                        .useDefaultLoader().obtain()
                        .add(storeFilePath, url, new DownloadListener());
            }
           
        } else if (position == 5) { 
            // 裁剪本地图片
            tempFile=new File(Environment.getExternalStorageDirectory(), "photo.jpg");
            // 显示Fragment
            AlertDialogFragment f=AlertDialogFragment.newInstance(0x1000);
            f.setItems(R.array.pick_image);
            f.setOnAlertDialogListener(new DialogListener());
            f.show(getChildFragmentManager());
        } else if (position == 6) {
        	// 开启通知服务
            startService(new Intent(mActivity, NotificationService.class));
            // 构建通知信息
        	NotificationManagerCompat.from(mActivity).cancelAll();
        	NotificationCompat.Builder notificationBuilder = createBuilder();
            Notification notification = notificationBuilder.build();
            NotificationManagerCompat.from(mActivity).notify(1000, notification);
        } else if (position == 7) { // 扫描二维码
            Intent openCameraIntent=new Intent(mActivity, CaptureActivity.class);
            startActivityForResult(openCameraIntent, REQUEST_CODE_QRCODE);
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        DebugLog.e("requestCode = " + requestCode);
        DebugLog.e("resultCode = " + resultCode);
        DebugLog.e("data = " + data);

        if (resultCode!=Activity.RESULT_OK)
            return;
        
        if (tempFile==null) 
            tempFile=new File(Environment.getExternalStorageDirectory(), "photo.jpg");
        
        if (requestCode==REQUEST_CODE_QRCODE) { // 二维码扫描
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            Toaster.show(mActivity, scanResult);
        } else if (requestCode==REQUEST_CODE_TAKE_PHOTO) { // 拍照
            beginCrop(Uri.fromFile(tempFile), Uri.fromFile(tempFile));
        } else if (requestCode==REQUEST_CODE_PICK_IMAGE) { // 选择照片
            beginCrop(data.getData(), Uri.fromFile(tempFile));
        } else if (requestCode==Crop.REQUEST_CROP) { // 输出裁剪结果
            Toaster.show(mActivity, "result:" + Crop.getOutput(data));
        }
    }
    
    /*
     * 自定义本地通知
     */
    private NotificationCompat.Builder createBuilder() {
    	Intent resultIntent = new Intent(mActivity, SendActivity.class);
    	TaskStackBuilder stackBuilder = TaskStackBuilder.create(mActivity);
    	stackBuilder.addParentStack(SendActivity.class);// 添加后台堆栈
    	stackBuilder.addNextIntent(resultIntent);// 添加Intent到栈顶
    	// 获得一个PendingIntent包含整个后台堆栈 containing the entire back stack
    	PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    	
        return new NotificationCompat.Builder(mActivity)
                .setSmallIcon(R.drawable.icon)
                .setAutoCancel(true)
                .setTicker("消息提示文字")
                .setContentText("本地消息通知")
                .setLights(0xff0000ff, 300, 1000)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle("消息标题")
                .setContentIntent(resultPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("消息内容"))
                .addAction(android.R.drawable.ic_menu_share, "Fix Now", resultPendingIntent)
                .addAction(android.R.drawable.ic_menu_revert, "Remind Me", resultPendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_image));
    }
    
    /*
     * 开始裁剪图片
     */
    private void beginCrop(Uri source, Uri outputUri) {
        new Crop(source).output(outputUri).asSquare().start(mActivity, this);
    }
    
    @Override
    public void onDestroyView() {
        // 移除所有message消息
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
    
    public static class SettingsActionProvider extends ActionProvider {
        /** An intent for launching the system settings. */
        private static final Intent sSettingsIntent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        
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
        	final Context context = getContext();
        	if (IntentUtils.isIntentAvailable(context, sSettingsIntent)) {
        		context.startActivity(sSettingsIntent);
        	} else {
        		context.startActivity(IntentUtils.newManageApplicationIntent());
        	}
            
            return true;
        }
    }
    
    private class DownloadListener extends Listener<Void> {
        private ProgressDialogFragment mFragment; 
        
        @Override
        public void onStart() {
            mFragment = ProgressDialogFragment.newInstance();
            mFragment.showFragment(getChildFragmentManager());
        }
        
        @Override
        public void onProgressUpdate(long fileSize, long downloadedSize) {
            mFragment.setMessage("正在下载文件：" + (int)((downloadedSize * 100) / fileSize) + "%");
        }
        
        @Override
        public void onSuccess(Void response) {
            Toaster.show(mActivity, "request is success");
        }
        
        @Override
        public void onFinish() {
            mFragment.hideFragment();
        }
        
        @Override
        public void onError(VolleyError error) {
            Toaster.show(mActivity, error.getMessage());
        }
    }

    private class DialogListener implements AlertDialogListener {
        @Override
        public void onClickPositive(int tag, Object payload) {
        }
        
        @Override
        public void onClickNegative(int tag, Object payload) {
        }
        
        @Override
        public void onClickListItem(int tag, int index, Object payload) {
            if (index == 0) { // 启动相机拍照
                startActivityForResult(IntentUtils.newTakePictureIntent(tempFile), REQUEST_CODE_TAKE_PHOTO);
            } else { // 相册选择照片
                startActivityForResult(IntentUtils.newMediaIntent("image/*"), REQUEST_CODE_PICK_IMAGE);
            }
        }
    }
}
