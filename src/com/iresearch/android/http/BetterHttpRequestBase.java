package com.iresearch.android.http;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import com.iresearch.android.http.cache.CachedHttpResponse.ResponseData;
import com.iresearch.android.http.cache.HttpResponseCache;

public abstract class BetterHttpRequestBase implements BetterHttpRequest,
        ResponseHandler<BetterHttpResponse> {

    private static final int MAX_RETRIES = 5;

    protected static final String HTTP_CONTENT_TYPE_HEADER = "Content-Type";

    protected Set<Integer> expectedStatusCodes = new HashSet<Integer>();

    protected BetterHttp betterHttp;

    protected AbstractHttpClient httpClient;

    protected HttpUriRequest request;

    protected int maxRetries = MAX_RETRIES;

    private int oldSocketTimeout, oldConnTimeout;
    private boolean timeoutChanged;

    private int executionCount;

    BetterHttpRequestBase(BetterHttp http) {
        this.betterHttp = http;
        this.httpClient = http.getHttpClient();
    }

    @Override
    public HttpUriRequest unwrap() {
        return request;
    }

    @Override
    public String getRequestUrl() {
        return request.getURI().toString();
    }

    @Override
    public BetterHttpRequestBase expecting(Integer... statusCodes) {
        expectedStatusCodes.addAll(Arrays.asList(statusCodes));
        return this;
    }

    @Override
    public BetterHttpRequestBase retries(int retries) {
        if (retries < 0) {
            this.maxRetries = 0;
        } else if (retries > MAX_RETRIES) {
            this.maxRetries = MAX_RETRIES;
        } else {
            this.maxRetries = retries;
        }
        return this;
    }

    @Override
    public BetterHttpRequest withTimeout(int timeout) {
        oldSocketTimeout = httpClient.getParams().getIntParameter(CoreConnectionPNames.SO_TIMEOUT,
                BetterHttp.DEFAULT_SOCKET_TIMEOUT);
        oldConnTimeout = httpClient.getParams().getIntParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT,
                BetterHttp.DEFAULT_WAIT_FOR_CONNECTION_TIMEOUT);

        betterHttp.setSocketTimeout(timeout);
        betterHttp.setConnectionTimeout(timeout);

        timeoutChanged = true;
        return this;
    }

    @Override
    public BetterHttpResponse send() throws ConnectException {

        BetterHttpRequestRetryHandler retryHandler = new BetterHttpRequestRetryHandler(maxRetries);

        // tell HttpClient to user our own retry handler
        httpClient.setHttpRequestRetryHandler(retryHandler);

        HttpContext context = new BasicHttpContext();

        // Grab a coffee now and lean back, I'm not good at explaining stuff. This code realizes
        // a second retry layer on top of HttpClient. Rationale: HttpClient.execute sometimes craps
        // out even *before* the HttpRequestRetryHandler set above is called, e.g. on a
        // "Network unreachable" SocketException, which can happen when failing over from Wi-Fi to
        // 3G or vice versa. Hence, we catch these exceptions, feed it through the same retry
        // decision method *again*, and align the execution count along the way.
        boolean retry = true;
        IOException cause = null;
        while (retry) {
            try {
                return httpClient.execute(request, this, context);
            } catch (IOException e) {
                cause = e;
                retry = retryRequest(retryHandler, cause, context);
            } catch (NullPointerException e) {
                // there's a bug in HttpClient 4.0.x that on some occasions causes
                // DefaultRequestExecutor to throw an NPE, see
                // http://code.google.com/p/android/issues/detail?id=5255
                cause = new IOException("NPE in HttpClient" + e.getMessage());
                retry = retryRequest(retryHandler, cause, context);
            } finally {
                // if timeout was changed with this request using withTimeout(), reset it
                if (timeoutChanged) {
                    betterHttp.setConnectionTimeout(oldConnTimeout);
                    betterHttp.setSocketTimeout(oldSocketTimeout);
                }
            }
        }

        // no retries left, crap out with exception
        ConnectException ex = new ConnectException();
        ex.initCause(cause);
        throw ex;
    }

    private boolean retryRequest(BetterHttpRequestRetryHandler retryHandler, IOException cause,
            HttpContext context) {
        Log.e(BetterHttp.LOG_TAG, "Intercepting exception that wasn't handled by HttpClient");
        executionCount = Math.max(executionCount, retryHandler.getTimesRetried());
        return retryHandler.retryRequest(cause, ++executionCount, context);
    }

    @Override
    public BetterHttpResponse handleResponse(HttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();
        if (expectedStatusCodes != null && !expectedStatusCodes.isEmpty()
                && !expectedStatusCodes.contains(status)) {
            throw new HttpResponseException(status, "Unexpected status code: " + status);
        }

        BetterHttpResponse bhttpr = new BetterHttpResponseImpl(response);
        HttpResponseCache responseCache = betterHttp.getResponseCache();
        if (responseCache != null && bhttpr.getResponseBody() != null) {
            ResponseData responseData = new ResponseData(status, bhttpr.getResponseBodyAsBytes());
            responseCache.put(getRequestUrl(), responseData);
        }
        return bhttpr;
    }
}
