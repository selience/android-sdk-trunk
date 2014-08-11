package com.iresearch.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import com.android.sdk.base.WebViewFragment;
import com.android.sdk.log.DebugLog;

public class WebPageFragment extends WebViewFragment {

    private WebView mWebview = null;
    
	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mWebview = getWebView();
		mWebview.addJavascriptInterface(new JavaScriptInterface(), "android");
		// 注册上下文菜单
		registerForContextMenu(mWebview);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    
	    WebView w = (WebView)v;
	    HitTestResult result = w.getHitTestResult();
	    // 检测图片格式
	    if(result.getType() == HitTestResult.IMAGE_TYPE){
	        menu.addSubMenu(1, 1, 1, "保存图片");
	        //通过result.getExtra()取出URL
	        DebugLog.d("webView", result.getExtra());
	    }
	}
	
	class JavaScriptInterface {

		@JavascriptInterface // 4.2 version must annotation
		public void netSetting() {
		    startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
		}
		
		@JavascriptInterface // 4.2 version must annotation
		public void refresh() {
		    loadUrl(getWebUrl());
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
