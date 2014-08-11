
package com.iresearch.android.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import com.iresearch.android.R;
import com.android.sdk.base.BaseFragment;
import com.android.sdk.facecropper.FaceCropper;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class FaceDetectorFragment extends BaseFragment implements OnSeekBarChangeListener {

    private ImageView mFaceView;
    private FaceCropper mFaceCropper;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.face_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        mFaceView = (ImageView)view.findViewById(R.id.imageView);
        seekBar.setOnSeekBarChangeListener(this);
        
        mFaceCropper = new FaceCropper(1f);
        mFaceCropper.setFaceMinSize(0);
        mFaceCropper.setDebug(true);
        
        updateView();
    }

    private void updateView() {
        Bitmap bitmap=mFaceCropper.getCroppedImage(mActivity, R.drawable.lluis);
        mFaceView.setImageBitmap(bitmap);
    }
    
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mFaceCropper.setEyeDistanceFactorMargin((float) progress / 10);
        updateView();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
