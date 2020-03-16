package com.journal.nn.school123.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.journal.nn.school123.pojo.Data;
import com.journal.nn.school123.pojo.Period;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class CurrentPeriodUtil {
    public static void clearCalendar(@NonNull Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static Period getPeriod(Data data,
                                   Calendar calendar) {
        Map<String, Period> periods = data.getPeriods();
        Date currentDate = calendar.getTime();
        Period currentPeriod = null;
        List<Period> periodList = data.getClassPeriods()
                .stream()
                .map(periods::get)
                .filter(period -> inPeriod(currentDate, period.getFrom(), period.getTo()))
                .collect(toList());
        for (Period period : periodList) {
            if (currentPeriod == null || period.getTo().getTime() < currentPeriod.getTo().getTime()) {
                currentPeriod = period;
            }
        }
        return currentPeriod;
    }

    public static Period getCurrentPeriod(Data data) {
        return getPeriod(data, Calendar.getInstance());
    }

    public static boolean inPeriod(@NonNull Date date,
                                   @Nullable Date from,
                                   @Nullable Date to) {
        return from == null
                || to == null
                || (from.getTime() <= date.getTime() && date.getTime() <= to.getTime());
    }
}
