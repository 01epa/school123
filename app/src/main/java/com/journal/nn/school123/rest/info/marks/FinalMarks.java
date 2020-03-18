package com.journal.nn.school123.rest.info.marks;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.pojo.FinalMark;
import com.journal.nn.school123.rest.AbstractPostRequest;
import com.journal.nn.school123.rest.RequestParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.journal.nn.school123.util.VersionUtil.getJsonSize;

public class FinalMarks extends AbstractPostRequest {
    public FinalMarks(@NonNull RequestParameters requestParameters) {
        super("/act/GET_STUDENT_DIRECTOR_DATA",
                requestParameters,
                response -> {
                    JsonArray jsonArray = requestParameters.getGson().fromJson(response, JsonArray.class);
                    List<FinalMark> finalMarks = new ArrayList<>();
                    for (JsonElement jsonElement : jsonArray) {
                        JsonArray finalMarkData = jsonElement.getAsJsonArray();
                        if (finalMarkData.size() == getJsonSize(requestParameters, FinalMarks.class)) {
                            FinalMark finalMark = new FinalMark();
                            finalMark.setPeriodId(finalMarkData.get(1).getAsString());
                            finalMark.setSubjectId(finalMarkData.get(4).getAsInt());
                            finalMark.setMark(finalMarkData.get(3).getAsString().trim());
                            finalMarks.add(finalMark);
                        } else {
                            System.out.println("Too many data for mark=" + finalMarkData);
                        }
                    }
                    requestParameters.getData().setFinalMarks(finalMarks);
                    Marks marks = new Marks(requestParameters);
                    requestParameters.getRequestQueue().add(marks);
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
