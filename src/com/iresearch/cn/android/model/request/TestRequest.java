package com.iresearch.cn.android.model.request;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.iresearch.cn.android.volley.toolbox.RequestInterface;

public class TestRequest extends RequestInterface<String, Void> {

    @Override
    public Request<String> create() {

        Request<String> request = new StringRequest(
                "http://blog.sina.com.cn/selienceblog",
                useInterfaceListener(),
                useInterfaceErrorListener());

        return request;
    }

}
