
package com.iresearch.android;

import android.view.View;
import android.os.Bundle;
import android.view.MenuItem;
import android.app.Activity;
import android.content.res.Configuration;
import android.location.Location;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.iresearch.android.R;
import com.iresearch.android.app.AppContext;
import com.android.sdk.app.AppManager;
import com.android.sdk.base.BaseActionBarActivity;
import com.android.sdk.base.WebViewFragment;
import com.android.sdk.location.LocationHelper;
import com.android.sdk.location.LocationHelper.LocationResult;
import com.android.sdk.utils.NetworkUtils;
import com.android.sdk.utils.Toaster;
import com.iresearch.android.ui.HomeFragment;
import com.iresearch.android.ui.RecyclerViewFragment;
import com.iresearch.android.ui.ShareFragment;
import com.iresearch.android.ui.SwipeFragment;
import com.iresearch.android.ui.VideoCropFragment;
import com.iresearch.android.ui.WebFlotr2Fragment;
import com.iresearch.android.ui.WebPageFragment;
import com.iresearch.android.utils.ClickExitHelper;

public class MainActivity extends BaseActionBarActivity implements OnItemClickListener, LocationResult {

    private ActionBar mActionBar;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private String[] mPlanetTitles;
    
    private ClickExitHelper mExitHelper;
    private LocationHelper mLocationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initViews();

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        
        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.nav_drawer_items);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, 
                android.R.id.text1, mPlanetTitles));
        mDrawerList.setOnItemClickListener(this);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerToggle = new ActionBarDrawerToggleImpl(this, mDrawerLayout, R.drawable.ic_drawer,
                R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
        
        mExitHelper=new ClickExitHelper(this);
        // 启动定位功能
        mLocationHelper=new LocationHelper();
        mLocationHelper.getLocation(this, this);
        
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toaster.show(this, R.string.network_not_connected);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void initViews() {
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        onItemChanged(position);
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void onItemChanged(int position) {
        switch (position) {
            case 0:
                replace(HomeFragment.class, "HomeFragment", null);
                break;
            case 1:
                replace(WebFlotr2Fragment.class, "WebFlotr2Fragment", null);
                break;
            case 2:
                replace(SwipeFragment.class, "SwipeFragment", null);
                break;
            case 3:
            	replace(VideoCropFragment.class, "VideoCropFragment", null);
            	break;
            case 4: {
                Bundle bundle = new Bundle();
                String url = "http://blog.sina.com.cn/selienceblog";
                bundle.putString(WebViewFragment.INTENT_KEY_URI, url);
                replace(WebPageFragment.class, "WebPageFragment", bundle);
                break;
            }
            case 5:
            	replace(RecyclerViewFragment.class, "RecyclerViewFragment", null);
            	break;
            case 6:
                replace(ShareFragment.class, "ShareFragment", null);
                break;
            default:
                break;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        mActionBar.setTitle(mTitle);
    }

    @Override
    protected int layout() {
        return R.id.content_frame;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void gotLocation(final Location location) {
        if (location != null) {
            AppContext.latitude=location.getLatitude();            
            AppContext.longitude=location.getLongitude();
            
            String message = location.getLatitude() + "," + location.getLongitude();
            Toaster.show(this, message);
        } else {
            Toaster.show(this, R.string.location_not_found);
        }
    }
    
    @Override
    protected boolean onBackTaskStart() {
    	// 关闭菜单显示
    	if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
    		mDrawerLayout.closeDrawer(mDrawerList);
    		return true;
    	} 
    	return false;
    }
    
    @Override
    protected void onBackTaskEnd() {
    	// 退出应用程序
    	if (mExitHelper.onBackPressed()) {
            AppManager.getAppManager().AppExit(this);
        }
    }
    
    @Override
    public void onDestroy() {
        if (mLocationHelper!=null) {
            mLocationHelper.cancelTimer();
        }
        super.onDestroy();
    }
    
    private class ActionBarDrawerToggleImpl extends ActionBarDrawerToggle {

        public ActionBarDrawerToggleImpl(Activity activity, DrawerLayout drawerLayout,
                int drawerImageRes, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes,
                    closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            mActionBar.setTitle(mTitle);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            mActionBar.setTitle(mDrawerTitle);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            super.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            super.onDrawerStateChanged(newState);
        }
    }
}
