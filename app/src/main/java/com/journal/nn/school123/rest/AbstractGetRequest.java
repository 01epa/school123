package com.journal.nn.school123.rest;

import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;

public abstract class AbstractGetRequest extends AbstractRequest {
    public AbstractGetRequest(@NonNull String path,
                              @NonNull RequestParameters requestParameters,
                              @NonNull Response.Listener<String> successListener) {
        super(Request.Method.GET,
                path,
                requestParameters,
                successListener);
    }
}
