package com.iresearch.android.http;

import java.net.ConnectException;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;

public interface BetterHttpRequest {

    /**
     * @return the HttpClient request object wrapped by this request
     */
    public HttpUriRequest unwrap();

    /**
     * @return the request URL
     */
    public String getRequestUrl();

    /**
     * Define the set of HTTP status codes which you anticipate to be returned by the server,
     * including error codes you'd like to explicitly handle. Any status code part of this set will
     * not be treated as an error, but returned to you as a normal server response. Any status codes
     * returned by the server that are <i>not</i> part of this set will be raised as an
     * {@link HttpResponseException}. This is very useful when dealing with REST-ful Web services,
     * where it is common to serve error stati that indicate a failure in the application logic
     * (e.g. 404 if a resource doesn't exist). You typically don't want to treat those as connection
     * errors, but gracefully handle them like a normal success code.
     * 
     * @param statusCodes
     *            the set of status codes that you want to manually handle as part of the response
     * @return this request
     */
    public BetterHttpRequest expecting(Integer... statusCodes);

    /**
     * Set maximum number of retries for this particular request.
     * 
     * @param retries
     *            the maximum number of retries should the request fail
     * @return this request
     */
    public BetterHttpRequest retries(int retries);

    /**
     * Set the global timeout for this specific request (connection and socket timeout).
     * 
     * @param timeout
     *            the timeout in milliseconds
     * @return this request
     */
    public BetterHttpRequest withTimeout(int timeout);

    /**
     * Sends the current request. This method uses a special retry-logic (on top of that employed by
     * HttpClient, which is better suited to handle network fail-overs when e.g. switching between
     * Wi-Fi and 3G).
     */
    public BetterHttpResponse send() throws ConnectException;
}
