package com.journal.nn.school123.rest.info.journal;

import androidx.annotation.NonNull;

import com.journal.nn.school123.pojo.Data;
import com.journal.nn.school123.rest.AbstractPostRequest;
import com.journal.nn.school123.rest.RequestParameters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.journal.nn.school123.util.CurrentPeriodUtil.clearCalendar;

public class JournalInfo extends AbstractPostRequest {
    public JournalInfo(@NonNull RequestParameters requestParameters,
                       @NonNull JournalInfoListener listener) {
        super("/act/GET_STUDENT_DAIRY",
                requestParameters,
                listener
        );
        listener.setCalendar(getCalendar());
    }

    protected Map<String, String> getParams() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Data data = requestParameters.getData();
        Calendar calendar = getCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Map<String, String> params = new HashMap<>();
        params.put("cls", data.getClassId());
        params.put("pClassesIds", "");
        params.put("begin_dt", dateFormat.format(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        params.put("end_dt", dateFormat.format(calendar.getTime()));
        params.put("student", requestParameters.getStudentId());
        return params;
    }

    protected Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        clearCalendar(calendar);
        return calendar;
    }
}
