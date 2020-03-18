package com.journal.nn.school123.util;

import androidx.annotation.NonNull;

import com.journal.nn.school123.rest.RequestParameters;
import com.journal.nn.school123.rest.info.ClassData;
import com.journal.nn.school123.rest.info.CurrentYear;
import com.journal.nn.school123.rest.info.UserData;
import com.journal.nn.school123.rest.info.journal.JournalInfo;
import com.journal.nn.school123.rest.info.journal.PeriodJournalInfo;
import com.journal.nn.school123.rest.info.marks.FinalMarks;
import com.journal.nn.school123.rest.info.marks.Marks;
import com.journal.nn.school123.rest.info.marks.Reasons;
import com.journal.nn.school123.rest.info.messages.Messages;
import com.journal.nn.school123.rest.info.messages.UnreadMessageCount;
import com.journal.nn.school123.rest.info.period.ClassPeriod;
import com.journal.nn.school123.rest.info.period.PeriodInfo;
import com.journal.nn.school123.rest.info.period.PeriodTime;
import com.journal.nn.school123.rest.info.subject.StudentSubjects;
import com.journal.nn.school123.rest.info.subject.SubjectNames;
import com.journal.nn.school123.rest.info.teacher.ClassTeachers;
import com.journal.nn.school123.rest.info.teacher.Teachers;

import java.util.HashMap;
import java.util.Map;

public class VersionUtil {
    private final static Map<String, Integer> responseSizeDefault = new HashMap<>();
    private final static Map<String, Integer> responseSize_4_1 = new HashMap<>();
    public final static String Version_3_1 = "3.1";
    public final static String Version_4_1 = "4.1";

    static {
        VersionUtil.responseSizeDefault.put(CurrentYear.class.getName(), 1);
        VersionUtil.responseSizeDefault.put(ClassData.class.getName(), 8);
        VersionUtil.responseSizeDefault.put(UserData.class.getName(), 6);
        VersionUtil.responseSizeDefault.put(PeriodInfo.class.getName(), 2);
        VersionUtil.responseSizeDefault.put(PeriodTime.class.getName(), 15);
        VersionUtil.responseSizeDefault.put(ClassPeriod.class.getName(), 1);
        VersionUtil.responseSizeDefault.put(SubjectNames.class.getName(), 2);
        VersionUtil.responseSizeDefault.put(StudentSubjects.class.getName(), 5);
        VersionUtil.responseSizeDefault.put(Teachers.class.getName(), 3);
        VersionUtil.responseSizeDefault.put(ClassTeachers.class.getName(), 5);
        VersionUtil.responseSizeDefault.put(Reasons.class.getName(), 3);
        VersionUtil.responseSizeDefault.put(FinalMarks.class.getName(), 6);
        VersionUtil.responseSizeDefault.put(Marks.class.getName(), 14);
        VersionUtil.responseSizeDefault.put(Messages.class.getName(), 13);
        VersionUtil.responseSizeDefault.put(UnreadMessageCount.class.getName(), 1);
        VersionUtil.responseSizeDefault.put(JournalInfo.class.getName(), 17);
        VersionUtil.responseSizeDefault.put(PeriodJournalInfo.class.getName(), 22);

        VersionUtil.responseSize_4_1.put(Marks.class.getName(), 15);
        VersionUtil.responseSize_4_1.put(JournalInfo.class.getName(), 18);
    }

    public static int getJsonSize(@NonNull RequestParameters requestParameters,
                                  @NonNull Class clazz) {
        String version = requestParameters.getData().getVersion();
        String clazzName = clazz.getName();
        switch (version) {
            case Version_3_1:
                return getJsonSize_Default(clazzName);
            case Version_4_1:
            default:
                return getJsonSize_4_1(clazzName);
        }
    }

    private static int getJsonSize_Default(String clazzName) {
        Integer size = responseSizeDefault.get(clazzName);
        return size == null ? 0 : size;
    }

    private static int getJsonSize_4_1(String clazzName) {
        if (responseSize_4_1.containsKey(clazzName)) {
            Integer size = responseSize_4_1.get(clazzName);
            return size == null ? 0 : size;
        } else {
            return getJsonSize_Default(clazzName);
        }
    }
}
