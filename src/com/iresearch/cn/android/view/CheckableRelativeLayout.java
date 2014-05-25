
package com.iresearch.cn.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.RelativeLayout;
import com.iresearch.cn.android.log.XLog;

public class CheckableRelativeLayout extends RelativeLayout implements Checkable {

    private static final String TAG = "CheckableRelativeLayout";

    private static final int[] CHECKED_STATE_SET = {
        android.R.attr.state_checked
    };

    private boolean mChecked = false;

    private OnCheckedChangeListener mOnCheckedChangeListener;

    public CheckableRelativeLayout(Context context) {
        super(context);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean checked) {
        XLog.d(TAG, "setChecked");
        if (checked != mChecked) {
            mChecked = checked;
            refreshDrawableState();

            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
            }
        }
    }

    @Override
    public boolean isChecked() {
        XLog.d(TAG, "isChecked");
        return mChecked;
    }

    @Override
    public void toggle() {
        XLog.d(TAG, "toggle");
        setChecked(!mChecked);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    /**
     * Register a callback to be invoked when the checked state of this view
     * changes.
     * 
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of this View is changed.
     */
    public static interface OnCheckedChangeListener {

        /**
         * Called when the checked state of a compound button has changed.
         * 
         * @param checkableView The view whose state has changed.
         * @param isChecked The new checked state of checkableView.
         */
        void onCheckedChanged(View checkableView, boolean isChecked);
    }
}
