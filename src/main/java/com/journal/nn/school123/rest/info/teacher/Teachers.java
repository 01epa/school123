package com.journal.nn.school123.rest.info.teacher;

import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.rest.AbstractPostRequest;
import com.journal.nn.school123.rest.RequestParameters;

import java.util.HashMap;
import java.util.Map;

import static com.journal.nn.school123.util.VersionUtil.getJsonSize;

public class Teachers extends AbstractPostRequest {
    public Teachers(@NonNull RequestParameters requestParameters) {
        super("/act/GET_PERSON_STORE_DATA",
                requestParameters,
                response -> {
                    JsonArray jsonArray = requestParameters.getGson().fromJson(response, JsonArray.class);
                    Map<Integer, String> teachers = new HashMap<>();
                    for (JsonElement jsonElement : jsonArray) {
                        JsonArray teacher = jsonElement.getAsJsonArray();
                        if (teacher.size() == getJsonSize(requestParameters, Teachers.class)) {
                            int teacherId = teacher.get(0).getAsInt();
                            String teacherFullName = teacher.get(1).getAsString();
                            teachers.put(teacherId, teacherFullName);
                        } else {
                            System.out.println("Too many data for teacher=" + teacher);
                        }
                    }
                    requestParameters.getData().setTeachers(teachers);
                    ClassTeachers classTeachers = new ClassTeachers(requestParameters);
                    requestParameters.getRequestQueue().add(classTeachers);
                }
        );
    }

    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("uchId", "1");
        return params;
    }
}
