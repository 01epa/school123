package com.journal.nn.school123.rest.info.period;

import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.rest.AbstractPostRequest;
import com.journal.nn.school123.rest.RequestParameters;
import com.journal.nn.school123.rest.info.subject.SubjectNames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.journal.nn.school123.fragment.marks.MarksRecyclerViewAdapter.YEAR_PERIOD;
import static com.journal.nn.school123.util.VersionUtil.getJsonSize;

public class ClassPeriod extends AbstractPostRequest {
    public ClassPeriod(@NonNull RequestParameters requestParameters) {
        super("/act/GET_CLASS_PER_SP",
                requestParameters,
                response -> {
                    JsonArray jsonArray = requestParameters.getGson().fromJson(response, JsonArray.class);
                    List<String> classPeriods = new ArrayList<>();
                    if (jsonArray.size() == 0) {
                        classPeriods.add(YEAR_PERIOD);
                    } else {
                        for (JsonElement jsonElement : jsonArray) {
                            JsonArray periodData = jsonElement.getAsJsonArray();
                            if (periodData.size() == getJsonSize(requestParameters, ClassPeriod.class)) {
                                classPeriods.add(periodData.get(0).getAsString());
                            } else {
                                System.out.println("Too many data in class period=" + periodData);
                            }
                        }
                    }
                    requestParameters.getData().setClassPeriods(classPeriods);
                    SubjectNames subjectNames = new SubjectNames(requestParameters);
                    requestParameters.getRequestQueue().add(subjectNames);
                }
        );
    }

    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("cls", requestParameters.getData().getClassId());
        return params;
    }
}
