package com.iresearch.android.http;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import com.iresearch.android.http.multipart.FilePart;
import com.iresearch.android.http.multipart.MultipartEntity;
import com.iresearch.android.http.multipart.StringPart;

class HttpPost extends BetterHttpRequestBase {

    HttpPost(BetterHttp betterHttp, String url, HashMap<String, String> defaultHeaders) {
        super(betterHttp);
        this.request = new org.apache.http.client.methods.HttpPost(url);
        for (String header : defaultHeaders.keySet()) {
            request.setHeader(header, defaultHeaders.get(header));
        }
    }

    HttpPost(BetterHttp betterHttp, String url, List<NameValuePair> nameValuePairs,
            HashMap<String, String> defaultHeaders) {
        super(betterHttp);
        this.request = new org.apache.http.client.methods.HttpPost(url);
        
        try {
            HttpEntity payload=new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
            ((HttpEntityEnclosingRequest) request).setEntity(payload);
            
            request.setHeader(HTTP_CONTENT_TYPE_HEADER, payload.getContentType().getValue());
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Unable to encode http parameters.");
        }

        for (String header : defaultHeaders.keySet()) {
            request.setHeader(header, defaultHeaders.get(header));
        }
    }

    HttpPost(BetterHttp betterHttp, String url, Map<String, String> multipartParams, 
            Map<String, String> filesToUpload, HashMap<String, String> defaultHeaders) {
        super(betterHttp);
        this.request = new org.apache.http.client.methods.HttpPost(url);
        
        MultipartEntity multipartEntity = new MultipartEntity();
        for (Map.Entry<String, String> entry : multipartParams.entrySet()) {
            multipartEntity.addPart(new StringPart(entry.getKey(), entry.getValue()));
        }
        
        for (Map.Entry<String, String> entry : filesToUpload.entrySet()) {
            File file = new File(entry.getKey());
            
            if(!file.exists()) {
                throw new RuntimeException(String.format("File not found: %s", file.getAbsolutePath()));
            }
            
            if(file.isDirectory()) {
                throw new RuntimeException(String.format("File is a directory: %s", file.getAbsolutePath()));
            }
            
            multipartEntity.addPart(new FilePart(entry.getKey(), file, null, null));
        }

        ((HttpEntityEnclosingRequest) request).setEntity(multipartEntity);
        
        request.setHeader(HTTP_CONTENT_TYPE_HEADER, multipartEntity.getContentType().getValue());
        for (String header : defaultHeaders.keySet()) {
            request.setHeader(header, defaultHeaders.get(header));
        }
    }
}
