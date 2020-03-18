package com.journal.nn.school123.rest.info.teacher;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.pojo.Data;
import com.journal.nn.school123.pojo.Subject;
import com.journal.nn.school123.rest.AbstractPostRequest;
import com.journal.nn.school123.rest.RequestParameters;
import com.journal.nn.school123.rest.info.marks.Reasons;

import java.util.HashMap;
import java.util.Map;

import static com.journal.nn.school123.util.VersionUtil.getJsonSize;

public class ClassTeachers extends AbstractPostRequest {
    public ClassTeachers(@NonNull RequestParameters requestParameters) {
        super("/act/GET_CLASS_TEACHERS_INFO",
                requestParameters,
                response -> {
                    JsonArray jsonArray = requestParameters.getGson().fromJson(response, JsonArray.class);
                    Map<Integer, Subject> studentSubjects = requestParameters.getData().getStudentSubjects();
                    for (JsonElement jsonElement : jsonArray) {
                        JsonArray teacher = jsonElement.getAsJsonArray();
                        if (teacher.size() == getJsonSize(requestParameters, ClassTeachers.class)) {
                            Subject subject = studentSubjects.get(teacher.get(0).getAsInt());
                            if (subject != null) {
                                JsonElement teacherJsonElement = teacher.get(1);
                                String teacherName = "Не известно";
                                if (!teacherJsonElement.isJsonNull()) {
                                    teacherName = teacherJsonElement.getAsString();
                                }
                                subject.setTeacher(teacherName);
                            }
                        } else {
                            System.out.println("Too many data for class teacher=" + teacher);
                        }
                    }
                    Reasons reasons = new Reasons(requestParameters);
                    requestParameters.getRequestQueue().add(reasons);
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
