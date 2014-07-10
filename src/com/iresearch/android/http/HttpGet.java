package com.iresearch.android.http;

import java.util.HashMap;

class HttpGet extends BetterHttpRequestBase {

    HttpGet(BetterHttp ignitedHttp, String url, HashMap<String, String> defaultHeaders) {
        super(ignitedHttp);
        request = new org.apache.http.client.methods.HttpGet(url);
        for (String header : defaultHeaders.keySet()) {
            request.setHeader(header, defaultHeaders.get(header));
        }
    }

}
