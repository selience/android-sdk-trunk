
package com.iresearch.android.ui;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.iresearch.android.R;
import com.iresearch.android.base.BaseActionBarFragment;
import com.iresearch.android.widget.PagerSlidingTabStrip;

public class SwipeFragment extends BaseActionBarFragment implements OnClickListener {

    private ViewPager pager;
    private PagerSlidingTabStrip tabs;
    
    private MyPagerAdapter adapter;

    private int currentColor = 0xFF666666;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.swipe_viewpager, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        pager = (ViewPager) view.findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);

        view.findViewById(R.id.image01).setOnClickListener(this);
        view.findViewById(R.id.image02).setOnClickListener(this);
        view.findViewById(R.id.image03).setOnClickListener(this);
        view.findViewById(R.id.image04).setOnClickListener(this);
        view.findViewById(R.id.image05).setOnClickListener(this);
        view.findViewById(R.id.image06).setOnClickListener(this);
        
        pager.setPageTransformer(false, new PageTransformer() {
            @Override
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void transformPage(View page, float position) {
                page.setRotationY(position * -30);
            }
        });
        adapter = new MyPagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        
        tabs.setViewPager(pager);
        
        changeColor(currentColor);
    }
    
    private void changeColor(int newColor) {
        currentColor = newColor;
        tabs.setIndicatorColor(newColor);
        getActionBar().setBackgroundDrawable(new ColorDrawable(newColor));
    }
    
    @Override
    public void onClick(View v) {
        changeColor(Color.parseColor(v.getTag().toString()));
    }
    
    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = { "Categories", "Home", "Top Paid", "Top Free", 
                "Top Grossing", "Top New Paid", "Top New Free", "Trending" };

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return PlanetFragment.newInstance(TITLES[position]);
        }
    }

}
