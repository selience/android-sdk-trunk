
package com.iresearch.cn.android;

import android.content.Intent;
import android.os.Bundle;
import com.iresearch.cn.android.base.BaseActionBarActivity;
import com.iresearch.cn.android.utils.ToastUtils;

public class SendActivity extends BaseActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent receivedIntent = getIntent();
        if (Intent.ACTION_SEND.equals(receivedIntent.getAction())) {
            String receivedType = receivedIntent.getType();
            if ("text/plain".equals(receivedType)) {
                ToastUtils.show(this, "分享文字信息");
            } else if (receivedType.startsWith("image/")) {
                ToastUtils.show(this, "分享图片信息");
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(receivedIntent.getAction())) {
            ToastUtils.show(this, "分享多张图片资源");
        }
    }

    @Override
    protected int layout() {
        return 0;
    }
}
