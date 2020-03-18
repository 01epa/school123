package com.journal.nn.school123.rest.update;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class Update extends StringRequest {
    public static final String ZORAS_ADDRESS = "http://zoras.ru/school/";
    public static final String INDEX_ADDRESS = "http://zoras.ru/school/index.html";
    public static final String VERSION_PATH = "version.json";

    public Update(@NonNull Response.Listener<String> successListener,
                  @NonNull Response.ErrorListener errorListener) {
        super(Request.Method.GET,
                ZORAS_ADDRESS + VERSION_PATH,
                successListener,
                errorListener);
    }
}
