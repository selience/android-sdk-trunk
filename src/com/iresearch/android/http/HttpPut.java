package com.iresearch.android.http;

import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;

class HttpPut extends BetterHttpRequestBase {

    HttpPut(BetterHttp ignitedHttp, String url, HashMap<String, String> defaultHeaders) {
        super(ignitedHttp);
        this.request = new org.apache.http.client.methods.HttpPut(url);
        for (String header : defaultHeaders.keySet()) {
            request.setHeader(header, defaultHeaders.get(header));
        }
    }

    HttpPut(BetterHttp ignitedHttp, String url, HttpEntity payload,
            HashMap<String, String> defaultHeaders) {
        super(ignitedHttp);
        this.request = new org.apache.http.client.methods.HttpPut(url);
        ((HttpEntityEnclosingRequest) request).setEntity(payload);

        request.setHeader(HTTP_CONTENT_TYPE_HEADER, payload.getContentType().getValue());
        for (String header : defaultHeaders.keySet()) {
            request.setHeader(header, defaultHeaders.get(header));
        }
    }

}
