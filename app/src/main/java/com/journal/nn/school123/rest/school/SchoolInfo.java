package com.journal.nn.school123.rest.school;

import android.support.annotation.NonNull;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.nio.charset.StandardCharsets;

import static com.journal.nn.school123.rest.update.Update.ZORAS_ADDRESS;

public class SchoolInfo extends StringRequest {
    private static final String SCHOOLS_PATH = "schools.json";

    public SchoolInfo(@NonNull Response.Listener<String> successListener,
                      @NonNull Response.ErrorListener errorListener) {
        super(Method.GET,
                ZORAS_ADDRESS + SCHOOLS_PATH,
                successListener,
                errorListener);
    }

    @Override
    @SuppressWarnings("DefaultCharset")
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        parsed = new String(response.data, StandardCharsets.UTF_8);
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
}
