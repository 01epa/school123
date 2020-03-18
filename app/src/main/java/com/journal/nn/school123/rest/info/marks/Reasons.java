package com.journal.nn.school123.rest.info.marks;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.pojo.Reason;
import com.journal.nn.school123.rest.AbstractGetRequest;
import com.journal.nn.school123.rest.RequestParameters;

import java.util.HashMap;
import java.util.Map;

import static com.journal.nn.school123.util.VersionUtil.getJsonSize;

public class Reasons extends AbstractGetRequest {
    public Reasons(@NonNull RequestParameters requestParameters) {
        super("/act/get_text_marks",
                requestParameters,
                response -> {
                    JsonArray jsonArray = requestParameters.getGson().fromJson(response, JsonArray.class);
                    Map<Integer, Reason> reasons = new HashMap<>();
                    for (JsonElement jsonElement : jsonArray) {
                        JsonArray reasonData = jsonElement.getAsJsonArray();
                        if (reasonData.size() == getJsonSize(requestParameters, Reasons.class)) {
                            Reason reason = new Reason();
                            reason.setId(reasonData.get(0).getAsInt());
                            reason.setShortReason(reasonData.get(1).getAsString());
                            reason.setReason(reasonData.get(2).getAsString());
                            reasons.put(reason.getId(), reason);
                        } else {
                            System.out.println("Too many data for reason=" + reasonData);
                        }
                    }
                    requestParameters.getData().setReasons(reasons);
                    FinalMarks finalMarks = new FinalMarks(requestParameters);
                    requestParameters.getRequestQueue().add(finalMarks);
                }
        );
    }
}
