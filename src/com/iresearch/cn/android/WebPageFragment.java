package com.iresearch.cn.android;

import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.iresearch.cn.android.extras.WebViewFragment;
import com.iresearch.cn.android.utils.ToastUtils;

public class WebPageFragment extends WebViewFragment {

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final WebView webview = getWebView();
		webview.addJavascriptInterface(new JavaScriptInterface(), "android");
	}
	
	class JavaScriptInterface {

		@JavascriptInterface // 4.2 version must annotation
		public String text() { 
			return "测试内容";
		}
		
		@JavascriptInterface // 4.2 version must annotation
		public void test() {
			ToastUtils.show(getActivity(), "当前内容来自本地客户端");
		}
	}
	
	/*
	 *  Android 4.2 JS调用Java代码的时候必须加上@JavascriptInterface才能调用。
	 *  在混淆里面加上以下内容，否则导致JS无法调用：
	 *  
	 *	-keepattributes Annotation
	 *	-keepattributes JavascriptInterface
	*/
}
