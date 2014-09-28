package com.iresearch.android.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.iresearch.android.R;
import org.mariotaku.android.fragment.BaseActionBarFragment;
import com.iresearch.android.widget.TextureVideoView;

public class VideoCropFragment extends BaseActionBarFragment implements OnClickListener {

	// Video file url
	private static final String FILE_URL = "http://www.w3schools.com/html/mov_bbb.mp4";

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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.video_crop, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_crop_center:
			mTextureVideoView.stop();
			mTextureVideoView.setScaleType(TextureVideoView.ScaleType.CENTER_CROP);
			mTextureVideoView.setDataSource(FILE_URL);
			mTextureVideoView.play();
			break;
		case R.id.menu_crop_top:
			mTextureVideoView.stop();
			mTextureVideoView.setScaleType(TextureVideoView.ScaleType.TOP);
			mTextureVideoView.setDataSource(FILE_URL);
			mTextureVideoView.play();
			break;
		case R.id.menu_crop_bottom:
			mTextureVideoView.stop();
			mTextureVideoView.setScaleType(TextureVideoView.ScaleType.BOTTOM);
			mTextureVideoView.setDataSource(FILE_URL);
			mTextureVideoView.play();
			break;
		}
		return super.onOptionsItemSelected(item);
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
	public void onDestroyView() {
		super.onDestroyView();
		mTextureVideoView.release();
	}
	
}
