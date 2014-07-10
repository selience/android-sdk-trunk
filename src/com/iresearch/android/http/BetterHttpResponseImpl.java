package com.iresearch.android.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

public class BetterHttpResponseImpl implements BetterHttpResponse {

    private HttpResponse response;
    private HttpEntity entity;

    public BetterHttpResponseImpl(HttpResponse response) throws IOException {
        this.response = response;
        HttpEntity temp = response.getEntity();
        if (temp != null) {
            entity = new BufferedHttpEntity(temp);
        }
    }

    @Override
    public HttpResponse unwrap() {
        return response;
    }

    @Override
    public InputStream getResponseBody() throws IOException {
        if (entity == null) {
            return null;
        }
        return entity.getContent();
    }

    @Override
    public byte[] getResponseBodyAsBytes() throws IOException {
        if (entity == null) {
            return null;
        }
        return EntityUtils.toByteArray(entity);
    }

    @Override
    public String getResponseBodyAsString() throws IOException {
        if (entity == null) {
            return null;
        }
        return EntityUtils.toString(entity);
    }

    @Override
    public int getStatusCode() {
        return this.response.getStatusLine().getStatusCode();
    }

    @Override
    public String getHeader(String header) {
        if (!response.containsHeader(header)) {
            return null;
        }
        return response.getFirstHeader(header).getValue();
    }
}
