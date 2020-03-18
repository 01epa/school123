package com.journal.nn.school123.rest.info.journal;

import androidx.annotation.NonNull;

import com.android.volley.Response;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.pojo.Journal;
import com.journal.nn.school123.pojo.Period;
import com.journal.nn.school123.rest.RequestParameters;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.journal.nn.school123.util.VersionUtil.getJsonSize;
import static com.journal.nn.school123.util.CurrentPeriodUtil.clearCalendar;

public class PeriodJournalInfoListener implements Response.Listener<String> {
    private RequestParameters requestParameters;
    private Period period;

    public PeriodJournalInfoListener(RequestParameters requestParameters) {
        this.requestParameters = requestParameters;
    }

    @Override
    public void onResponse(String response) {
        response = response.replace("new Date(", "");
        response = response.replace(")", "");
        JsonArray jsonArray = requestParameters.getGson().fromJson(response, JsonArray.class);
        Map<Date, Set<Journal>> journals = new HashMap<>();
        for (JsonElement jsonElement : jsonArray) {
            JsonArray journalsData = jsonElement.getAsJsonArray();
            if (journalsData.size() == getJsonSize(requestParameters, PeriodJournalInfo.class)) {
                Journal journal = new Journal();
                Calendar calendar = Calendar.getInstance();
                calendar.set(journalsData.get(7).getAsInt(),
                        journalsData.get(8).getAsInt(),
                        journalsData.get(9).getAsInt());
                clearCalendar(calendar);
                Date date = calendar.getTime();
                journal.setDate(date);
                journal.setSubjectId(journalsData.get(20).getAsInt());
                JsonElement taskElement = journalsData.get(4);
                if (taskElement.isJsonNull()) {
                    journal.setTask("");
                } else {
                    journal.setTask(taskElement.getAsString().trim());
                }
                JsonElement detailsElement = journalsData.get(14);
                if (detailsElement.isJsonNull()) {
                    journal.setTaskDetails("");
                } else {
                    journal.setTaskDetails(detailsElement.getAsString().trim());
                }
                journal.setGroupNumber(journalsData.get(16).getAsString());
                Set<Journal> values = journals.computeIfAbsent(date, v -> new HashSet<>());
                values.add(journal);
            } else {
                System.out.println("Too many data for period journals=" + journalsData);
            }
        }
        proceedResult(journals);
        requestParameters.getData().periodLoaded(period);
        requestParameters.getSuccessListener().onResponse(response);
    }

    protected void proceedResult(@NonNull Map<Date, Set<Journal>> journals) {
        requestParameters.getData().setPeriodJournals(journals);
    }

    public void setPeriod(@NonNull Period period) {
        this.period = period;
    }
}
