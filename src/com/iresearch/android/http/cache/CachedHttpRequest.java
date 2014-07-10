package com.iresearch.android.http.cache;

import java.net.ConnectException;
import com.iresearch.android.http.BetterHttpRequest;
import com.iresearch.android.http.BetterHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

public class CachedHttpRequest implements BetterHttpRequest {

    private String url;

    private HttpResponseCache responseCache;

    public CachedHttpRequest(HttpResponseCache responseCache, String url) {
        this.responseCache = responseCache;
        this.url = url;
    }

    @Override
    public String getRequestUrl() {
        return url;
    }

    @Override
    public BetterHttpRequest expecting(Integer... statusCodes) {
        return this;
    }

    @Override
    public BetterHttpRequest retries(int retries) {
        return this;
    }

    @Override
    public BetterHttpResponse send() throws ConnectException {
        return new CachedHttpResponse(responseCache.get(url));
    }

    @Override
    public HttpUriRequest unwrap() {
        return null;
    }

    @Override
    public BetterHttpRequest withTimeout(int timeout) {
        return this;
    }
}
