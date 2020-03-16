package com.journal.nn.school123.rest.info.messages;

import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.pojo.Message;
import com.journal.nn.school123.rest.AbstractPostRequest;
import com.journal.nn.school123.rest.RequestParameters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.journal.nn.school123.util.VersionUtil.getJsonSize;
import static com.journal.nn.school123.util.CurrentPeriodUtil.clearCalendar;

public class Messages extends AbstractPostRequest {
    public Messages(@NonNull RequestParameters requestParameters) {
        super("/act/GET_STUDENT_MESSAGES",
                requestParameters,
                response -> {
                    response = response.replace("new Date(", "");
                    response = response.replace(")", "");
                    JsonArray jsonArray = requestParameters.getGson().fromJson(response, JsonArray.class);
                    List<Message> messages = new ArrayList<>();
                    for (JsonElement jsonElement : jsonArray) {
                        JsonArray jsonElements = jsonElement.getAsJsonArray();
                        if (jsonElements.size() == getJsonSize(requestParameters, Messages.class)) {
                            Message message = new Message();
                            message.setMessage(jsonElements.get(3).getAsString());
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(jsonElements.get(4).getAsInt(),
                                    jsonElements.get(5).getAsInt(),
                                    jsonElements.get(6).getAsInt());
                            clearCalendar(calendar);
                            message.setDate(calendar.getTime());
                            message.setSubjectId(jsonElements.get(12).getAsInt());
                            message.setTeacherId(jsonElements.get(1).getAsInt());
                            messages.add(message);
                        }
                    }
                    requestParameters.getData().setMessages(messages);
                    UnreadMessageCount unreadMessageCount = new UnreadMessageCount(requestParameters);
                    requestParameters.getRequestQueue().add(unreadMessageCount);
                }
        );
    }

    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("uchYear", requestParameters.getData().getCurrentYear());
        params.put("isGuru", "false");
        params.put("student", requestParameters.getStudentId());
        return params;
    }
}
