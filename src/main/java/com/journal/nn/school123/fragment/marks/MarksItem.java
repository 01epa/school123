package com.journal.nn.school123.fragment.marks;

import com.journal.nn.school123.pojo.Period;
import com.journal.nn.school123.pojo.Subject;

public class MarksItem {
    public final String subjectName;
    public final String marks1String;
    public final String marks2String;
    public final String marks3String;
    public final String marks4String;
    public final String mark;
    public final Subject subject;
    public final Period period;
    public final boolean total;

    public MarksItem(String subjectName,
                     String marks1String,
                     String marks2String,
                     String marks3String,
                     String marks4String,
                     String mark,
                     Subject subject,
                     Period period,
                     boolean total) {
        this.subjectName = subjectName;
        this.marks1String = marks1String;
        this.marks2String = marks2String;
        this.marks3String = marks3String;
        this.marks4String = marks4String;
        this.mark = mark;
        this.subject = subject;
        this.period = period;
        this.total = total;
    }

    public boolean isHeader() {
        return subject == null;
    }

    public boolean isClickable() {
        return period != null && subject != null;
    }
}
