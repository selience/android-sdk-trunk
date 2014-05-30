package com.iresearch.cn.android.model.request;

import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.iresearch.cn.android.volley.toolbox.RequestInterface;

public class TestRequest extends RequestInterface<String, Void> {

    @Override
    public Request<String> create() {

        Uri.Builder builder = Uri.parse("").buildUpon();
        builder.appendQueryParameter("api_key", "5e045abd4baba4bbcd866e1864ca9d7b");
        builder.appendQueryParameter("method", "flickr.interestingness.getList");
        builder.appendQueryParameter("format", "json");
        builder.appendQueryParameter("nojsoncallback", "1");
        
        Request<String> request = new StringRequest(
                "http://blog.sina.com.cn/selienceblog",
                useInterfaceListener(),
                useInterfaceErrorListener());

        return request;
    }

}
