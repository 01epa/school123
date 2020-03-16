package com.journal.nn.school123.fragment.journal;

import com.journal.nn.school123.pojo.Period;
import com.journal.nn.school123.pojo.Subject;

public class JournalItem {
    public final String number;
    public final String subjectText;
    public final String task;
    public final String marks;
    public final Subject subject;
    public final Period period;

    public JournalItem(String number,
                       String subjectText,
                       String task,
                       String marks,
                       Subject subject,
                       Period period) {
        this.number = number;
        this.subjectText = subjectText;
        this.task = task;
        this.marks = marks;
        this.subject = subject;
        this.period = period;
    }

    public boolean isHeader() {
        return subject == null || period == null;
    }
}
