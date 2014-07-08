package com.iresearch.android.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;
import com.iresearch.android.R;
import android.annotation.SuppressLint;
import com.iresearch.android.app.AppManager;

/**
 * 再点击一次推出应用程序
 * 
 * @author Jacky.Lee
 */
public class ClickExitHelper {

    private final Activity mActivity;
    
    private Handler mHandler;
    private Toast mBackToast;
    private boolean isOnKeyBacking;
    
    public ClickExitHelper(Activity activity) {
        mActivity = activity;
        mHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Activity onKeyDown事件
     **/
    @SuppressLint("ShowToast")
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode != KeyEvent.KEYCODE_BACK) {
            return false;
        }
        if(isOnKeyBacking) {
            mHandler.removeCallbacks(onBackTimeRunnable);
            if(mBackToast != null){
                mBackToast.cancel();
            }
            // 退出
            AppManager.getAppManager().AppExit(mActivity);
            return true;
        } else {
            isOnKeyBacking = true;
            if(mBackToast == null) {
                mBackToast = Toast.makeText(mActivity, R.string.back_exit_tips, 2000);
            }
            mBackToast.show();
            mHandler.postDelayed(onBackTimeRunnable, 2000);
            return true;
        }
    }
    
    private Runnable onBackTimeRunnable = new Runnable() {
        
        @Override
        public void run() {
            isOnKeyBacking = false;
            if(mBackToast != null){
                mBackToast.cancel();
            }
        }
    };
}

