package com.iresearch.android.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;

public interface BetterHttpResponse {

    public HttpResponse unwrap();

    public InputStream getResponseBody() throws IOException;

    public byte[] getResponseBodyAsBytes() throws IOException;

    public String getResponseBodyAsString() throws IOException;

    public int getStatusCode();

    public String getHeader(String header);
}
