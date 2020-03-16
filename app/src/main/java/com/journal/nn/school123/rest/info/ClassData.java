package com.journal.nn.school123.rest.info;

import android.support.annotation.NonNull;

import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.pojo.Data;
import com.journal.nn.school123.rest.AbstractPostRequest;
import com.journal.nn.school123.rest.RequestParameters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.journal.nn.school123.util.VersionUtil.getJsonSize;

public class ClassData extends AbstractPostRequest {
    public ClassData(@NonNull RequestParameters requestParameters) {
        super("/act/GET_CLASS_STORE_DATA",
                requestParameters,
                response -> {
                    JsonArray jsonArray = requestParameters.getGson().fromJson(response, JsonArray.class);
                    for (JsonElement jsonElement : jsonArray) {
                        JsonArray jsonElements = jsonElement.getAsJsonArray();
                        if (jsonElements.size() == getJsonSize(requestParameters, ClassData.class)) {
                            Data data = requestParameters.getData();
                            data.setClassId(jsonElements.get(0).getAsString().trim());
                            JsonElement classTeacherJsonElement = jsonElements.get(5);
                            String classTeacher = "Не известно";
                            if (!classTeacherJsonElement.isJsonNull()) {
                                classTeacher = classTeacherJsonElement.getAsString().trim();
                            }
                            data.setClassTeacher(classTeacher);
                            data.setCurrentClass(jsonElements.get(7).getAsString().trim());

                            UserData userData = new UserData(requestParameters);
                            requestParameters.getRequestQueue().add(userData);
                            return;
                        }
                    }
                    requestParameters.getErrorListener().onErrorResponse(new VolleyError("Информация о классе не задана"));
                }
        );
    }

    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date date = new Date();
        params.put("currentDate", dateFormat.format(date));
        params.put("demo", "false");
        params.put("subsClassIds", "");
        params.put("uchId", "1");
        params.put("uchYear", requestParameters.getData().getCurrentYear());
        return params;
    }
}
