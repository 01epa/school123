package com.journal.nn.school123.rest;

import androidx.annotation.NonNull;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRequest extends StringRequest {
    public static final String TYPE = "application/x-www-form-urlencoded; charset=UTF-8";
    @NonNull
    protected RequestParameters requestParameters;

    public AbstractRequest(int method,
                           @NonNull String path,
                           @NonNull RequestParameters requestParameters,
                           @NonNull Response.Listener<String> successListener) {
        super(method,
                requestParameters.getAddress() + path,
                successListener,
                requestParameters.getErrorListener());
        this.requestParameters = requestParameters;
    }

    @Override
    public String getBodyContentType() {
        return TYPE;
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", requestParameters.getCookie());
        return headers;
    }
}
