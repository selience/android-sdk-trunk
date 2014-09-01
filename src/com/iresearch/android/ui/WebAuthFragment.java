package com.iresearch.android.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import com.android.sdk.base.WebViewFragment;

public class WebAuthFragment extends WebViewFragment {

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setWebViewClient(new AuthWebViewClient(mActivity));
    }
    
    class AuthWebViewClient extends DefaultWebViewClient {

        public AuthWebViewClient(final Activity activity) {
            super(activity);
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            Uri uri = Uri.parse(url);
            String code = uri.getQueryParameter("code");
            if (!TextUtils.isEmpty(code)) {
                Bundle bundle = new Bundle();
                bundle.putString("code", code);
                setResult(ShareFragment.SHARE_SINA_WEIBO, Activity.RESULT_OK, bundle);
                // 弹出栈顶Fragment
                popFragment();
            }
            
            return true;
        }
    }
}
