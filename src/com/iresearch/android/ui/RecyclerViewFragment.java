package com.iresearch.android.ui;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.support.v7.extensions.DividerItemDecoration;
import android.support.v7.extensions.GridLayoutManager;
import android.support.v7.extensions.OnRecyclerViewItemClickListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.iresearch.android.R;
import org.mariotaku.android.fragment.BaseFragment;
import com.iresearch.android.model.ViewModel;
import com.iresearch.android.adapter.RecyclerViewAdapter;

public class RecyclerViewFragment extends BaseFragment implements OnRecyclerViewItemClickListener<ViewModel> {
	private static final String MOCK_URL = "http://lorempixel.com/800/400/nightlife/";
	
	private RecyclerView mRecyclerView;
	private RecyclerViewAdapter mViewAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View convertView=inflater.inflate(R.layout.recyclerview, container, false);
		mRecyclerView=(RecyclerView)convertView.findViewById(R.id.recyclerView);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity));
		mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, LinearLayoutManager.VERTICAL));
		return convertView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (mViewAdapter==null) {
			mViewAdapter=new RecyclerViewAdapter(createMockList());
			mViewAdapter.setOnItemClickListener(this);
		}
		mRecyclerView.setAdapter(mViewAdapter);
	}
	
	private List<ViewModel> createMockList() {
        List<ViewModel> items = new ArrayList<ViewModel>();
        for (int i = 0; i < 20; i++) {
            items.add(new ViewModel(i, "Item " + (i + 1), MOCK_URL + (i % 10 + 1)));
        }
        return items;
    }

	@Override
	public void onItemClick(View view, ViewModel model) {
		// 移除视图元素
		mViewAdapter.remove(model);
	}
	
	@Override
	public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
		if(enter) {
			return AnimationUtils.loadAnimation(mActivity, R.anim.abc_slide_in_top);
		} else {
			return AnimationUtils.loadAnimation(mActivity, R.anim.abc_slide_in_bottom);
		}
	}
}
