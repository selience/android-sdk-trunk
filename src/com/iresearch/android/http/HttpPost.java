package com.iresearch.android.http;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.android.volley.multipart.FilePart;
import com.android.volley.multipart.MultipartEntity;
import com.android.volley.multipart.StringPart;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;

class HttpPost extends BetterHttpRequestBase {

    HttpPost(BetterHttp betterHttp, String url, HashMap<String, String> defaultHeaders) {
        super(betterHttp);
        this.request = new org.apache.http.client.methods.HttpPost(url);
        for (String header : defaultHeaders.keySet()) {
            request.setHeader(header, defaultHeaders.get(header));
        }
    }

    HttpPost(BetterHttp betterHttp, String url, HttpEntity payload,
            HashMap<String, String> defaultHeaders) {
        super(betterHttp);
        this.request = new org.apache.http.client.methods.HttpPost(url);
        ((HttpEntityEnclosingRequest) request).setEntity(payload);

        request.setHeader(HTTP_CONTENT_TYPE_HEADER, payload.getContentType().getValue());
        for (String header : defaultHeaders.keySet()) {
            request.setHeader(header, defaultHeaders.get(header));
        }
    }

    HttpPost(BetterHttp betterHttp, String url, Map<String, String> multipartParams, 
            Map<String, String> filesToUpload) throws IOException {
        super(betterHttp);
        this.request = new org.apache.http.client.methods.HttpPost(url);
        
        MultipartEntity multipartEntity = new MultipartEntity();
        for (Map.Entry<String, String> entry : multipartParams.entrySet()) {
            multipartEntity.addPart(new StringPart(entry.getKey(), entry.getValue()));
        }
        
        for (Map.Entry<String, String> entry : filesToUpload.entrySet()) {
            File file = new File(entry.getKey());
            
            if(!file.exists()) {
                throw new IOException(String.format("File not found: %s", file.getAbsolutePath()));
            }
            
            if(file.isDirectory()) {
                throw new IOException(String.format("File is a directory: %s", file.getAbsolutePath()));
            }
            
            multipartEntity.addPart(new FilePart(entry.getKey(), file, null, null));
        }
    }
}
