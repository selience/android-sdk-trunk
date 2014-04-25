package com.iresearch.cn.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.iresearch.cn.android.base.BaseFragment;

public class MainFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TextView textview = new TextView(getActivity());
		textview.setPadding(10, 10, 10, 10);
		textview.setText("test");
		return textview;
	}

}
