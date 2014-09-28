package com.iresearch.android;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.view.View.OnClickListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.iresearch.android.R;
import org.mariotaku.android.activity.BaseFragmentActivity;
import com.iresearch.android.constants.Constants;
import com.iresearch.android.ui.NativeMapV2Fragment;
import com.iresearch.android.ui.WebMapFragment;

/**
 * 谷歌地图使用，详细说明参考官方文档
 * @file MapViewerActivity.java
 * @create 2013-9-27 下午03:39:49
 * @author lilong@qiyi.com
 * @description TODO http://www.androidhive.info/2013/08/android-working-with-google-maps-v2/
 */
public class MapViewerActivity extends BaseFragmentActivity implements Constants, OnClickListener {
    private static final String FRAGMENT_TAG = "MapFragment";
    
    private Fragment mMapFragment=null;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_viewer);
        
        final Uri uri = getIntent().getData();
        if (uri == null || !AUTHORITY_MAP.equals(uri.getAuthority())) {
            finish();
            return;
        }
        
        final Bundle bundle = new Bundle();
        final String param_lat = uri.getQueryParameter(QUERY_PARAM_LAT);
        final String param_lng = uri.getQueryParameter(QUERY_PARAM_LNG);
        if (param_lat == null || param_lng == null) {
            finish();
            return;
        }
        
        try {
            bundle.putDouble(INTENT_KEY_LATITUDE, Double.valueOf(param_lat));
            bundle.putDouble(INTENT_KEY_LONGITUDE, Double.valueOf(param_lng));
        } catch (final NumberFormatException e) {
            finish();
            return;
        }

        if (savedInstanceState==null) {
            mMapFragment=isNativeMapSupported() ? new NativeMapV2Fragment() : new WebMapFragment();
            mMapFragment.setArguments(bundle);
            replace(layout(), mMapFragment, FRAGMENT_TAG);
        } else {
            mMapFragment=getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        }
    }
    
    private boolean isNativeMapSupported() {
        // 获取谷歌服务的状态
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // 判断谷歌服务状态是否有效
        if (status != ConnectionResult.SUCCESS) {
            Toast.makeText(this, "Google Play service are not available", Toast.LENGTH_LONG).show();
            //GooglePlayServicesUtil.getErrorDialog(status, this, GOOGLE_PLAY_SERVICE_REQUEST_CODE).show();
            return false;
        } 
        return true;
    }
    
    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.mapview_close_btn: {
                finish();
                break;
            }
            case R.id.mapview_center_btn: {
                if (!(mMapFragment instanceof MapInterface)) {
                    break;
                }
                ((MapInterface) mMapFragment).center();
                break;
            }
        }
    }
    
    @Override
    protected int layout() {
        return R.id.content;
    }
    
    public static interface MapInterface {

        public void center();
    }
}
