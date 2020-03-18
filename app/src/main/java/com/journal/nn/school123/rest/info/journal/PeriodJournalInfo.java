package com.journal.nn.school123.rest.info.journal;

import androidx.annotation.NonNull;

import com.journal.nn.school123.pojo.Data;
import com.journal.nn.school123.pojo.Period;
import com.journal.nn.school123.rest.AbstractPostRequest;
import com.journal.nn.school123.rest.RequestParameters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.journal.nn.school123.fragment.marks.MarksRecyclerViewAdapter.YEAR_PERIOD;
import static com.journal.nn.school123.util.CurrentPeriodUtil.getCurrentPeriod;

public class PeriodJournalInfo extends AbstractPostRequest {
    public PeriodJournalInfo(@NonNull RequestParameters requestParameters,
                             @NonNull PeriodJournalInfoListener listener) {
        super("/act/GET_STUDENT_LESSONS",
                requestParameters,
                listener
        );
        listener.setPeriod(getPeriod());
    }

    protected Map<String, String> getParams() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Data data = requestParameters.getData();
        Map<String, String> params = new HashMap<>();
        params.put("cls", data.getClassId());
        params.put("parallelClasses", "");
        Period currentPeriod = getPeriod();
        if (!YEAR_PERIOD.equals(currentPeriod.getId())) {
            params.put("period_begin", dateFormat.format(currentPeriod.getFrom()));
            params.put("period_end", dateFormat.format(currentPeriod.getTo()));
        }
        params.put("student", requestParameters.getStudentId());
        params.put("uchYear", data.getCurrentYear());
        return params;
    }

    protected Period getPeriod() {
        return getCurrentPeriod(requestParameters.getData());
    }
}
