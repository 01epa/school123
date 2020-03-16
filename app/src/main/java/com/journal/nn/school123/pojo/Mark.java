package com.journal.nn.school123.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Mark implements Serializable {
    private int mark;
    private Date date;
    private boolean justReceived;

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isJustReceived() {
        return justReceived;
    }

    void setJustReceived(boolean justReceived) {
        this.justReceived = justReceived;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mark mark1 = (Mark) o;
        return mark == mark1.mark &&
                Objects.equals(date, mark1.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mark, date);
    }
}
