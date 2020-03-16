package com.journal.nn.school123.rest.info.journal;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.pojo.Journal;
import com.journal.nn.school123.rest.RequestParameters;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.journal.nn.school123.util.VersionUtil.getJsonSize;
import static com.journal.nn.school123.util.CurrentPeriodUtil.clearCalendar;

public class JournalInfoListener implements Response.Listener<String> {
    private RequestParameters requestParameters;
    protected Calendar calendar;

    public JournalInfoListener(RequestParameters requestParameters) {
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
            if (journalsData.size() == getJsonSize(requestParameters, JournalInfo.class)) {
                Journal journal = new Journal();
                Calendar calendar = Calendar.getInstance();
                calendar.set(journalsData.get(0).getAsInt(),
                        journalsData.get(1).getAsInt(),
                        journalsData.get(2).getAsInt());
                clearCalendar(calendar);
                Date date = calendar.getTime();
                journal.setDate(date);
                journal.setSubjectId(journalsData.get(8).getAsInt());
                JsonElement taskElement = journalsData.get(9);
                if (taskElement.isJsonNull()) {
                    journal.setTask("");
                } else {
                    journal.setTask(taskElement.getAsString().trim());
                }
                JsonElement detailsElement = journalsData.get(10);
                if (detailsElement.isJsonNull()) {
                    journal.setTaskDetails("");
                } else {
                    journal.setTaskDetails(detailsElement.getAsString().trim());
                }
                journal.setGroupNumber(journalsData.get(12).getAsString());
                Set<Journal> values = journals.computeIfAbsent(date, v -> new HashSet<>());
                values.add(journal);
            } else {
                System.out.println("Too many data for journals=" + journalsData);
            }
        }
        proceedResult(journals);
        requestParameters.getData().journalLoaded(calendar);
        nextStep(response);
    }

    protected void nextStep(String response) {
        PeriodJournalInfo periodJournalInfo = new PeriodJournalInfo(requestParameters, new PeriodJournalInfoListener(requestParameters));
        requestParameters.getRequestQueue().add(periodJournalInfo);
    }

    protected void proceedResult(@NonNull Map<Date, Set<Journal>> journals) {
        requestParameters.getData().setJournals(journals);
    }

    public void setCalendar(@NonNull Calendar calendar) {
        this.calendar = calendar;
    }
}
