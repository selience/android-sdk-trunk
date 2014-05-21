
package com.iresearch.cn.android;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.iresearch.cn.android.base.BaseActionBarActivity;
import com.iresearch.cn.android.extras.WebViewFragment;

public class MainActivity extends BaseActionBarActivity implements OnItemClickListener {

    private ActionBar mActionBar;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private String[] mPlanetTitles;

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
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
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
                Bundle bundle = new Bundle();
                String url = "file:///android_asset/www/info.html";
                url = "http://blog.sina.com.cn/selienceblog";
                bundle.putString(WebViewFragment.INTENT_KEY_URI, url);
                replace(WebPageFragment.class, "WebPageFragment", bundle);
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
