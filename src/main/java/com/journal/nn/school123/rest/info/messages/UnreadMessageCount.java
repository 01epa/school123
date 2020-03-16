package com.journal.nn.school123.rest.info.messages;

import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.rest.AbstractPostRequest;
import com.journal.nn.school123.rest.RequestParameters;
import com.journal.nn.school123.rest.info.journal.JournalInfo;
import com.journal.nn.school123.rest.info.journal.JournalInfoListener;

import java.util.HashMap;
import java.util.Map;

import static com.journal.nn.school123.util.VersionUtil.getJsonSize;

public class UnreadMessageCount extends AbstractPostRequest {
    public UnreadMessageCount(@NonNull RequestParameters requestParameters) {
        super("/act/GET_MESSAGE_COUNT",
                requestParameters,
                response -> {
                    JsonArray jsonArray = requestParameters.getGson().fromJson(response, JsonArray.class);
                    for (JsonElement jsonElement : jsonArray) {
                        JsonArray jsonElements = jsonElement.getAsJsonArray();
                        if (jsonElements.size() == getJsonSize(requestParameters, UnreadMessageCount.class)) {
                            requestParameters.getData().setUnreadMessageCount(jsonElements.get(0).getAsInt());
                            break;
                        }
                    }
                    JournalInfo journalInfo = new JournalInfo(requestParameters, new JournalInfoListener(requestParameters));
                    requestParameters.getRequestQueue().add(journalInfo);
                }
        );
    }

    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("uchYear", requestParameters.getData().getCurrentYear());
        return params;
    }
}
