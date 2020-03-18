package com.journal.nn.school123.rest.update;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.nio.charset.StandardCharsets;

import static com.journal.nn.school123.rest.update.Update.ZORAS_ADDRESS;
import static com.journal.nn.school123.rest.update.Update.VERSION_PATH;

public class UpdateInfo extends StringRequest {
    public UpdateInfo(@NonNull Response.Listener<String> successListener,
                      @NonNull Response.ErrorListener errorListener,
                      @Nullable String version) {
        super(Method.GET,
                ZORAS_ADDRESS + version + "/" + VERSION_PATH,
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
