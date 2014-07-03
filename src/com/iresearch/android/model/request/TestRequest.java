package com.iresearch.android.model.request;

import android.annotation.SuppressLint;
import android.net.Uri;
import com.android.volley.Request;
import com.android.volley.core.RequestInterface;
import com.android.volley.request.StringRequest;
import java.util.concurrent.TimeUnit;

public class TestRequest extends RequestInterface<String, Void> {

    @Override
    @SuppressLint("NewApi")
    public Request<String> create() {

        Uri.Builder builder = Uri.parse("").buildUpon();
        builder.appendQueryParameter("api_key", "5e045abd4baba4bbcd866e1864ca9d7b");
        builder.appendQueryParameter("method", "flickr.interestingness.getList");
        builder.appendQueryParameter("format", "json");
        builder.appendQueryParameter("nojsoncallback", "1");
        
        Request<String> request = new StringRequest(
                "http://www.baidu.com",
                useInterfaceListener()).
                setCacheExpireTime(TimeUnit.MINUTES, 5);

        return request;
    }

}
