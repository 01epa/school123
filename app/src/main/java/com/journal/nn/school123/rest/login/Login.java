package com.journal.nn.school123.rest.login;

import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.journal.nn.school123.rest.AbstractRequest.TYPE;

public class Login extends StringRequest {
    @NonNull
    private final String username;
    @NonNull
    private final String password;

    public Login(@NonNull String address,
                 @NonNull Response.Listener<String> successListener,
                 @NonNull Response.ErrorListener errorListener,
                 @NonNull String username,
                 @NonNull String password) {
        super(Request.Method.POST,
                address + "/login",
                successListener,
                errorListener);
        this.username = username;
        this.password = password;
    }

    @Override
    public String getBodyContentType() {
        return TYPE;
    }

    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("l", username);
        params.put("p", password);
        return params;
    }
}
