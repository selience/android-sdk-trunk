
package com.iresearch.android.base;

import java.util.ArrayList;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.content.Context;
import com.iresearch.android.log.DebugLog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public abstract class BasePagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private ArrayList<String> mFragments;
    private SparseArray<Fragment> registerFragments;

    public BasePagerAdapter(FragmentManager fm) {
        super(fm);
        this.mFragments = new ArrayList<String>();
        this.registerFragments = new SparseArray<Fragment>();
    }

    @Override
    public Fragment getItem(int position) {
        DebugLog.d("getItem >> " + position);
        if (mFragments != null && mFragments.size() > 0) {
            return Fragment.instantiate(mContext, mFragments.get(position));
        }
        return null;
    }

    @Override
    public int getCount() {
        return mFragments!=null ? mFragments.size() : 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registerFragments.put(position, fragment);
        DebugLog.d("instantiateItem >> " + fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registerFragments.remove(position);
        super.destroyItem(container, position, object);
        DebugLog.d("destroyItem >> " + position + "-" + object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public Fragment getFragment(int position) {
        return registerFragments.get(position);
    }
    
    public void addFragment(Fragment fragment) {
        mFragments.add(fragment.getClass().getName());
    }

    public void clearFragment() {
        mFragments.clear();
    }
}
