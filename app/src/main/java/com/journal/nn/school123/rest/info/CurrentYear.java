package com.journal.nn.school123.rest.info;

import android.support.annotation.NonNull;

import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.rest.AbstractPostRequest;
import com.journal.nn.school123.rest.RequestParameters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.journal.nn.school123.util.VersionUtil.getJsonSize;

public class CurrentYear extends AbstractPostRequest {
    public CurrentYear(@NonNull RequestParameters requestParameters) {
        super("/act/get_uch_year",
                requestParameters,
                response -> {
                    JsonArray jsonArray = requestParameters.getGson().fromJson(response, JsonArray.class);
                    for (JsonElement jsonElement : jsonArray) {
                        JsonArray jsonElements = jsonElement.getAsJsonArray();
                        if (jsonElements.size() == getJsonSize(requestParameters, CurrentYear.class)) {
                            requestParameters.getData().setCurrentYear(jsonElements.get(0).getAsString());
                            ClassData classData = new ClassData(requestParameters);
                            requestParameters.getRequestQueue().add(classData);
                            return;
                        }
                    }
                    requestParameters.getErrorListener().onErrorResponse(new VolleyError("Текущий год не задан"));
                }
        );
    }

    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date date = new Date();
        params.put("currentDate", dateFormat.format(date));
        return params;
    }
}
