
package com.iresearch.android;

import android.content.Intent;
import android.os.Bundle;
import com.android.sdk.base.BaseActionBarActivity;
import com.android.sdk.utils.Toaster;

public class SendActivity extends BaseActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent receivedIntent = getIntent();
        if (Intent.ACTION_SEND.equals(receivedIntent.getAction())) {
            String receivedType = receivedIntent.getType();
            if ("text/plain".equals(receivedType)) {
                Toaster.show(this, "分享文字信息");
            } else if (receivedType.startsWith("image/")) {
                Toaster.show(this, "分享图片信息");
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(receivedIntent.getAction())) {
            Toaster.show(this, "分享多张图片资源");
        }
    }

    @Override
    protected int layout() {
        return 0;
    }
}
