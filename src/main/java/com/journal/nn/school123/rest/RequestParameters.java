package com.journal.nn.school123.rest;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.journal.nn.school123.pojo.Data;

public class RequestParameters {
    @NonNull
    private final Gson gson;
    @NonNull
    private final RequestQueue requestQueue;
    @NonNull
    private final Response.Listener<String> successListener;
    @NonNull
    private final Response.ErrorListener errorListener;
    @NonNull
    private final String cookie;
    @NonNull
    private final String studentId;
    @NonNull
    private String address;
    @NonNull
    private Data data;

    public RequestParameters(@NonNull Data data,
                             @NonNull Gson gson,
                             @NonNull RequestQueue requestQueue,
                             @NonNull Response.Listener<String> successListener,
                             @NonNull Response.ErrorListener errorListener,
                             @NonNull String cookie,
                             @NonNull String studentId,
                             @NonNull String address) {
        this.data = data;
        this.gson = gson;
        this.requestQueue = requestQueue;
        this.successListener = successListener;
        this.errorListener = errorListener;
        this.cookie = cookie;
        this.studentId = studentId;
        this.address = address;
    }

    @NonNull
    public Gson getGson() {
        return gson;
    }

    @NonNull
    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    @NonNull
    public Response.Listener<String> getSuccessListener() {
        return successListener;
    }

    @NonNull
    public Response.ErrorListener getErrorListener() {
        return errorListener;
    }

    @NonNull
    public String getCookie() {
        return cookie;
    }

    @NonNull
    public String getStudentId() {
        return studentId;
    }

    @NonNull
    public Data getData() {
        return data;
    }

    @NonNull
    public String getAddress() {
        return address;
    }
}
