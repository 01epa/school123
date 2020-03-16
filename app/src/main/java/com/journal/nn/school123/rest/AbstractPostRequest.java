package com.journal.nn.school123.rest;

import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;

public abstract class AbstractPostRequest extends AbstractRequest {
    public AbstractPostRequest(@NonNull String path,
                               @NonNull RequestParameters requestParameters,
                               @NonNull Response.Listener<String> successListener) {
        super(Request.Method.POST,
                path,
                requestParameters,
                successListener);
    }
}
