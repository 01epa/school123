package com.journal.nn.school123.rest.info.marks;

import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.pojo.Mark;
import com.journal.nn.school123.rest.AbstractPostRequest;
import com.journal.nn.school123.rest.RequestParameters;
import com.journal.nn.school123.rest.info.messages.Messages;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.journal.nn.school123.util.VersionUtil.getJsonSize;
import static com.journal.nn.school123.util.CurrentPeriodUtil.clearCalendar;

public class Marks extends AbstractPostRequest {
    public Marks(@NonNull RequestParameters requestParameters) {
        super("/act/GET_STUDENT_JOURNAL_DATA",
                requestParameters,
                response -> {
                    response = response.replace("new Date(", "");
                    response = response.replace(")", "");
                    JsonArray jsonArray = requestParameters.getGson().fromJson(response, JsonArray.class);
                    Map<Integer, List<Mark>> marks = new TreeMap<>(Comparator.naturalOrder());
                    for (JsonElement jsonElement : jsonArray) {
                        JsonArray markData = jsonElement.getAsJsonArray();
                        if (markData.size() == getJsonSize(requestParameters, Marks.class)) {
                            Mark mark = new Mark();
                            mark.setMark(markData.get(12).getAsInt());
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(markData.get(3).getAsInt(),
                                    markData.get(4).getAsInt(),
                                    markData.get(5).getAsInt());
                            clearCalendar(calendar);
                            mark.setDate(calendar.getTime());
                            List<Mark> subjectMarks = marks.computeIfAbsent(markData.get(10).getAsInt(), v -> new ArrayList<>());
                            subjectMarks.add(mark);
                        } else {
                            System.out.println("Too many data for mark=" + markData);
                        }
                    }
                    requestParameters.getData().setMarks(marks);
                    Messages messages = new Messages(requestParameters);
                    requestParameters.getRequestQueue().add(messages);
                }
        );
    }

    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("cls", requestParameters.getData().getClassId());
        params.put("parallelClasses", "");
        params.put("student", requestParameters.getStudentId());
        return params;
    }
}
