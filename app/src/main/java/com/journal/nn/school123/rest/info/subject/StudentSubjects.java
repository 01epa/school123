package com.journal.nn.school123.rest.info.subject;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.pojo.Data;
import com.journal.nn.school123.pojo.Subject;
import com.journal.nn.school123.rest.AbstractPostRequest;
import com.journal.nn.school123.rest.RequestParameters;
import com.journal.nn.school123.rest.info.teacher.Teachers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.journal.nn.school123.util.VersionUtil.getJsonSize;

public class StudentSubjects extends AbstractPostRequest {
    public StudentSubjects(@NonNull RequestParameters requestParameters) {
        super("/act/GET_STUDENT_SUBJECTS",
                requestParameters,
                response -> {
                    JsonArray jsonArray = requestParameters.getGson().fromJson(response, JsonArray.class);
                    Map<Integer, Subject> studentSubjects = new TreeMap<>(Comparator.naturalOrder());
                    for (JsonElement jsonElement : jsonArray) {
                        JsonArray studentSubject = jsonElement.getAsJsonArray();
                        if (studentSubject.size() == getJsonSize(requestParameters, StudentSubjects.class)) {
                            Subject subject = new Subject();
                            subject.setId(studentSubject.get(0).getAsInt());
                            subject.setName(studentSubject.get(1).getAsString());
                            studentSubjects.put(subject.getId(), subject);
                        } else {
                            System.out.println("Too many data in student subject=" + studentSubject);
                        }
                    }
                    requestParameters.getData().setStudentSubjects(studentSubjects);
                    Teachers teachers = new Teachers(requestParameters);
                    requestParameters.getRequestQueue().add(teachers);
                }
        );
    }

    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        Data data = requestParameters.getData();
        params.put("cls", data.getClassId());
        params.put("student", requestParameters.getStudentId());
        params.put("uchYear", data.getCurrentYear());
        return params;
    }
}
