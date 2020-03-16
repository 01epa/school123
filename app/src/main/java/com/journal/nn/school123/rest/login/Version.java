package com.journal.nn.school123.rest.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.journal.nn.school123.util.LoginUtil;
import com.journal.nn.school123.util.VersionUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version extends StringRequest {
    private static final Pattern pattern = Pattern.compile("<title>.*?\\s*(\\d*\\.?\\d*)\\s*\\(\\d*\\)</title>");

    public Version(@NonNull String userId,
                   @NonNull String username,
                   @NonNull String password,
                   @NonNull String address,
                   @NonNull Context context,
                   @NonNull Response.ErrorListener errorListener,
                   @NonNull Response.Listener<String> successListener,
                   @NonNull RequestQueue requestQueue,
                   @NonNull Gson gson) {
        super(Method.GET,
                address + "/",
                response -> {
                    Matcher matcher = pattern.matcher(response);
                    if (matcher.find()) {
                        String version = matcher.group(1);
                        switch (version) {
                            case VersionUtil.Version_3_1:
                                Login login31 = new Login(address,
                                        new LoginUtil.PlainCookieListener(version,
                                                userId,
                                                username,
                                                password,
                                                address,
                                                context,
                                                errorListener,
                                                successListener,
                                                requestQueue,
                                                gson),
                                        errorListener,
                                        username,
                                        password);
                                requestQueue.add(login31);
                                break;
                            case VersionUtil.Version_4_1:
                            default:
                                Login login41 = new Login(address,
                                        new LoginUtil.Sha1CookieListener(version,
                                                userId,
                                                username,
                                                password,
                                                address,
                                                context,
                                                errorListener,
                                                successListener,
                                                requestQueue,
                                                gson),
                                        errorListener,
                                        username,
                                        LoginUtil.getSha1Hash(password));
                                requestQueue.add(login41);
                                break;
                        }
                    }
                },
                errorListener);
    }

    @Override
    public String getBodyContentType() {
        return "text/html; charset=" + getParamsEncoding();
    }
}
