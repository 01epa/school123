package com.journal.nn.school123.rest.info.subject;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.rest.AbstractGetRequest;
import com.journal.nn.school123.rest.RequestParameters;

import java.util.HashMap;
import java.util.Map;

import static com.journal.nn.school123.util.VersionUtil.getJsonSize;

public class SubjectNames extends AbstractGetRequest {
    public SubjectNames(@NonNull RequestParameters requestParameters) {
        super("/act/GET_SBJ_NAMES_STORE_DATA?_dc=" + System.currentTimeMillis(),
                requestParameters,
                response -> {
                    JsonArray jsonArray = requestParameters.getGson().fromJson(response, JsonArray.class);
                    Map<Integer, String> subjects = new HashMap<>();
                    for (JsonElement jsonElement : jsonArray) {
                        JsonArray subject = jsonElement.getAsJsonArray();
                        if (subject.size() == getJsonSize(requestParameters, SubjectNames.class)) {
                            int subjectId = subject.get(0).getAsInt();
                            String subjectName = subject.get(1).getAsString();
                            subjects.put(subjectId, subjectName);
                        } else {
                            System.out.println("Too many data in subject=" + subject);
                        }
                    }
                    requestParameters.getData().setSubjects(subjects);
                    StudentSubjects studentSubjects = new StudentSubjects(requestParameters);
                    requestParameters.getRequestQueue().add(studentSubjects);
                }
        );
    }
}
