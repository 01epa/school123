package com.journal.nn.school123.rest.info.period;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.pojo.Period;
import com.journal.nn.school123.rest.AbstractGetRequest;
import com.journal.nn.school123.rest.RequestParameters;

import java.util.HashMap;
import java.util.Map;

import static com.journal.nn.school123.util.VersionUtil.getJsonSize;

public class PeriodInfo extends AbstractGetRequest {
    public PeriodInfo(@NonNull RequestParameters requestParameters) {
        super("/act/GET_PERIODS?_dc=" + System.currentTimeMillis(),
                requestParameters,
                response -> {
                    JsonArray jsonArray = requestParameters.getGson().fromJson(response, JsonArray.class);
                    Map<String, Period> periods = new HashMap<>();
                    for (JsonElement jsonElement : jsonArray) {
                        JsonArray periodData = jsonElement.getAsJsonArray();
                        if (periodData.size() == getJsonSize(requestParameters, PeriodInfo.class)) {
                            Period period = new Period();
                            period.setId(periodData.get(0).getAsString());
                            period.setName(periodData.get(1).getAsString());
                            periods.put(period.getId(), period);
                        } else {
                            System.out.println("Too many data in period=" + periodData);
                        }
                    }
                    requestParameters.getData().setPeriods(periods);
                    PeriodTime periodTime = new PeriodTime(requestParameters);
                    requestParameters.getRequestQueue().add(periodTime);
                }
        );
    }
}
