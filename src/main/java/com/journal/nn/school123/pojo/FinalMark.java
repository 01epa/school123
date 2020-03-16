package com.journal.nn.school123.pojo;

import java.io.Serializable;
import java.util.Objects;

public class FinalMark implements Serializable {
    private String periodId;
    private int subjectId;
    private String mark;

    public String getPeriodId() {
        return periodId;
    }

    public void setPeriodId(String periodId) {
        this.periodId = periodId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinalMark finalMark = (FinalMark) o;
        return periodId == finalMark.periodId &&
                subjectId == finalMark.subjectId &&
                Objects.equals(mark, finalMark.mark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(periodId, subjectId, mark);
    }
}
