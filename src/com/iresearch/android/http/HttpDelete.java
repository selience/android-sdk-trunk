package com.iresearch.android.http;

import java.util.HashMap;

class HttpDelete extends BetterHttpRequestBase {

    HttpDelete(BetterHttp ignitedHttp, String url, HashMap<String, String> defaultHeaders) {
        super(ignitedHttp);
        request = new org.apache.http.client.methods.HttpDelete(url);
        for (String header : defaultHeaders.keySet()) {
            request.setHeader(header, defaultHeaders.get(header));
        }
    }

}
