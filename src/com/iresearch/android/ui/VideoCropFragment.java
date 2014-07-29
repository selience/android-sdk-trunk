package com.iresearch.android.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import com.iresearch.android.R;
import com.iresearch.android.base.BaseActionBarFragment;
import com.iresearch.android.widget.TextureVideoView;

public class VideoCropFragment extends BaseActionBarFragment implements
		OnClickListener, ActionBar.OnNavigationListener {

	// Video file url
	private static final String FILE_URL = "http://www.w3schools.com/html/mov_bbb.mp4";

	private final int indexCropCenter = 0;
	private final int indexCropTop = 1;
	private final int indexCropBottom = 2;

	private ActionBar mActionBar;
	private TextureVideoView mTextureVideoView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.textureview, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mTextureVideoView = (TextureVideoView) view.findViewById(R.id.cropTextureView);

		view.findViewById(R.id.btnPlay).setOnClickListener(this);
		view.findViewById(R.id.btnPause).setOnClickListener(this);
		view.findViewById(R.id.btnStop).setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mActionBar = getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		mActionBar.setDisplayShowTitleEnabled(false);

		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(
				mActivity, R.array.action_list,
				android.R.layout.simple_spinner_dropdown_item);
		mActionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnPlay:
			mTextureVideoView.play();
			break;
		case R.id.btnPause:
			mTextureVideoView.pause();
			break;
		case R.id.btnStop:
			mTextureVideoView.stop();
			break;
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		switch (itemPosition) {
		case indexCropCenter:
			mTextureVideoView.stop();
			mTextureVideoView.setScaleType(TextureVideoView.ScaleType.CENTER_CROP);
			mTextureVideoView.setDataSource(FILE_URL);
			mTextureVideoView.play();
			break;
		case indexCropTop:
			mTextureVideoView.stop();
			mTextureVideoView.setScaleType(TextureVideoView.ScaleType.TOP);
			mTextureVideoView.setDataSource(FILE_URL);
			mTextureVideoView.play();
			break;
		case indexCropBottom:
			mTextureVideoView.stop();
			mTextureVideoView.setScaleType(TextureVideoView.ScaleType.BOTTOM);
			mTextureVideoView.setDataSource(FILE_URL);
			mTextureVideoView.play();
			break;
		}
		return true;
	}
	
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mTextureVideoView.release();
	}
	
}
