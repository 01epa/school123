package com.journal.nn.school123.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.journal.nn.school123.pojo.IntentHelper;
import com.journal.nn.school123.pojo.School;
import com.journal.nn.school123.rest.school.SchoolInfo;

import java.util.Collection;

public class SchoolUtil {
    private static final TypeToken<Collection<School>> typeToken = new TypeToken<Collection<School>>() {
    };

    public static void loadSchools(@NonNull Context context,
                                   @NonNull Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        Gson gson = new Gson();
        SchoolInfo schoolInfo = new SchoolInfo(
                response -> {
                    Collection<School> schools = gson.fromJson(response, typeToken.getType());
                    IntentHelper.setSchools(context, schools);
                    UpdateUtil.checkUpdate(context, errorListener);
                },
                errorListener
        );
        requestQueue.add(schoolInfo);
    }
}
