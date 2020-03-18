package com.journal.nn.school123.rest.info;

import androidx.annotation.NonNull;

import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.pojo.Data;
import com.journal.nn.school123.rest.AbstractGetRequest;
import com.journal.nn.school123.rest.RequestParameters;
import com.journal.nn.school123.rest.info.period.PeriodInfo;

import static com.journal.nn.school123.util.VersionUtil.getJsonSize;

public class UserData extends AbstractGetRequest {
    public UserData(@NonNull RequestParameters requestParameters) {
        super("/act/get_user_data",
                requestParameters,
                response -> {
                    JsonArray jsonArray = requestParameters.getGson().fromJson(response, JsonArray.class);
                    for (JsonElement jsonElement : jsonArray) {
                        JsonArray jsonElements = jsonElement.getAsJsonArray();
                        if (jsonElements.size() == getJsonSize(requestParameters, UserData.class)) {
                            Data data = requestParameters.getData();
                            data.setSecondName(jsonElements.get(0).getAsString());
                            data.setFirstName(jsonElements.get(1).getAsString());
                            data.setMiddleName(jsonElements.get(2).getAsString());
                            data.setSchoolDescription(jsonElements.get(3).getAsString());
                            data.setGroupNumber(jsonElements.get(4).getAsString());

                            PeriodInfo periodInfo = new PeriodInfo(requestParameters);
                            requestParameters.getRequestQueue().add(periodInfo);
                            return;
                        }
                    }
                    requestParameters.getErrorListener().onErrorResponse(new VolleyError("Информация обученике не задана"));
                }
        );
    }
}
