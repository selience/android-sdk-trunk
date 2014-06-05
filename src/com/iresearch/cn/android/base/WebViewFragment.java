package com.iresearch.cn.android.base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.RenderPriority;
import com.iresearch.cn.android.utils.ReflectionUtils;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewFragment extends BaseFragment {
	public static final String INTENT_KEY_URI = "uri";
	
	private String mWebUrl;
	private WebView mWebView;
	private long mZoomTimeout;
	private boolean mIsWebViewAvailable;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		if (mWebView != null) {
            mWebView.destroy();
        }
        mWebView = new WebView(mActivity);
        mIsWebViewAvailable = true;
        return mWebView;
	}

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		WebSettings webSettings = mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUserAgentString("android-native");
        webSettings.setSavePassword(false);
        webSettings.setRenderPriority(RenderPriority.HIGH);
		
		setLayerType(mWebView, View.LAYER_TYPE_SOFTWARE, null);
		mZoomTimeout=ViewConfiguration.getZoomControlsTimeout();
		
		mWebView.setWebViewClient(new DefaultWebViewClient(mActivity));
		mWebView.setWebChromeClient(new DefaultWebChromeClient(mActivity));
		
		final Bundle bundle = getArguments();
		if (bundle != null) {
		    mWebUrl=bundle.getString(INTENT_KEY_URI);
			loadUrl(mWebUrl);
		}
	}
	
	public final String getWebUrl() {
	    return mWebUrl;
	}
	
	public final void loadUrl(final String url) {
		mWebView.loadUrl(url == null ? "about:blank" : url);
	}
	
	@Override
    public void onResume() {
        super.onResume();
        // Resumes the WebView.
        ReflectionUtils.tryInvoke(mWebView, "onResume");
    }
	
	@Override
    public void onPause() {
        super.onPause();
        // Pauses the WebView.
        ReflectionUtils.tryInvoke(mWebView, "onPause");
    }

    @Override
    public void onDestroyView() {
        mIsWebViewAvailable = false;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
        	destroyView();
        }
        super.onDestroy();
    }
	
	@Override
	public boolean onBackPressed() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return false;
	}

	public final WebView getWebView() {
		return mIsWebViewAvailable ? mWebView : null;
	}
	
	public final void setWebViewClient(final WebViewClient client) {
		mWebView.setWebViewClient(client);
	}
	
	public final void setWebChromeClient(final WebChromeClient client) {
		mWebView.setWebChromeClient(client);
	}

	private void destroyView() {
		mWebView.postDelayed(new Runnable() {
			public void run() {
				mWebView.destroy();
				mWebView = null;
			}
		}, mZoomTimeout);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setLayerType(final View view, final int layerType, final Paint paint) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) return;
		view.setLayerType(layerType, paint);
	}
	
	protected class DefaultWebViewClient extends WebViewClient {

		private final Activity mActivity;

		public DefaultWebViewClient(final Activity activity) {
			mActivity = activity;
		}

		@Override
		public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			mActivity.setProgressBarIndeterminateVisibility(true);
		}

		@Override
		public void onPageFinished(final WebView view, final String url) {
			super.onPageFinished(view, url);
			mActivity.setTitle(view.getTitle());
			mActivity.setProgressBarIndeterminateVisibility(false);
		}
		
		@Override
		@TargetApi(Build.VERSION_CODES.FROYO)
		public void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {
			handler.proceed();
		}

		@Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		    view.stopLoading();
		    view.clearView();
		    // 显示错误页面
		    view.loadUrl("file:///android_asset/error.html");  
        }

        @Override
		public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
			view.loadUrl(url);
			return true;
		}
	}
	
	protected class DefaultWebChromeClient extends WebChromeClient {

		private final Activity mActivity;

		public DefaultWebChromeClient(final Activity activity) {
			mActivity = activity;
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			mActivity.setTitle(newProgress + "%");
			if (newProgress == 100) {
				mActivity.setTitle(view.getTitle());
			}
		}
	}
}
