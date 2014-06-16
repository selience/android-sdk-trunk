package com.iresearch.cn.android.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.iresearch.cn.android.R;
import com.iresearch.cn.android.base.BaseFragment;

public class PlanetFragment extends BaseFragment {

	public static final String ARG_PLANET_EXTRAS = "planet";
	
	public static PlanetFragment newInstance(String title) {
		PlanetFragment fragment = new PlanetFragment();
		Bundle bundle = new Bundle();
		bundle.putString(ARG_PLANET_EXTRAS, title);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View mainView=inflater.inflate(R.layout.swipe_list_item, null);
	    TextView textView = (TextView)mainView.findViewById(android.R.id.text1);
	    textView.setText(getArguments().getString(ARG_PLANET_EXTRAS));
	    return mainView;
	}
}
