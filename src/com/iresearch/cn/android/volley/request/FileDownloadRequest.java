
package com.iresearch.cn.android.volley.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;

public class FileDownloadRequest extends Request<Void> {

    public FileDownloadRequest(int method, String url, ErrorListener listener) {
        super(method, url, listener);
    }

    @Override
    protected void deliverResponse(Void response) {
    }

    @Override
    protected Response<Void> parseNetworkResponse(NetworkResponse response) {
        return null;
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }
}
