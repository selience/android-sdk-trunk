
package com.iresearch.cn.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;
import com.iresearch.cn.android.log.XLog;

public class CheckableRelativeLayout extends RelativeLayout implements Checkable {

    private static final String TAG = "CheckableRelativeLayout";

    public CheckableRelativeLayout(Context context) {
        super(context);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean checked) {
        XLog.d(TAG, "setChecked");
    }

    @Override
    public boolean isChecked() {
        XLog.d(TAG, "isChecked");
        return false;
    }

    @Override
    public void toggle() {
        XLog.d(TAG, "toggle");
    }

}
