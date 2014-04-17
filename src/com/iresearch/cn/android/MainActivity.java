package com.iresearch.cn.android;

import android.os.Bundle;
import com.iresearch.cn.android.base.BaseActionBarActivity;
import com.iresearch.cn.android.extras.WebViewFragment;

public class MainActivity extends BaseActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Bundle bundle = new Bundle();
		String url = "file:///android_asset/www/info.html";
		url = "http://blog.sina.com.cn/selienceblog";
		bundle.putString(WebViewFragment.INTENT_KEY_URI, url);
		replace(WebPageFragment.class, "WebPageFragment", bundle);
	}

	@Override
	protected int layout() {
		return R.id.content;
	}
}
