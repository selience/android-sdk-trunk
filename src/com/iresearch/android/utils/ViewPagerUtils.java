package com.iresearch.android.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

/**
 * Created by Issac on 10/1/13.
 */
public class ViewPagerUtils {
    /**
     * Find fragment in certain position of viewpager
     * 
     * @param fragmentManager
     * @param viewPager
     * @param position
     * @return The Fragment if found or null otherwise.
     */
    public static Fragment findFragment(FragmentManager fragmentManager, ViewPager viewPager,
                                        long position) {
        return fragmentManager.findFragmentByTag(makeFragmentName(viewPager.getId(), position));
    }

    private static String makeFragmentName(long viewPagerId, long position) {
        return "android:switcher:" + viewPagerId + ":" + position;
    }
}