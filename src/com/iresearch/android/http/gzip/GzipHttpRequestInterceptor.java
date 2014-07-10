package com.iresearch.android.http.gzip;

import com.iresearch.android.http.BetterHttp;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

/**
 * Simple {@link HttpRequestInterceptor} that adds GZIP accept encoding header.
 */
public class GzipHttpRequestInterceptor implements HttpRequestInterceptor {

    @Override
    public void process(final HttpRequest request, final HttpContext context) {
        // Add header to accept gzip content
        if (!request.containsHeader(BetterHttp.HEADER_ACCEPT_ENCODING)) {
            request.addHeader(BetterHttp.HEADER_ACCEPT_ENCODING, BetterHttp.ENCODING_GZIP);
        }
    }

}
