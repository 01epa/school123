package com.journal.nn.school123.pojo;

import java.io.Serializable;
import java.util.Objects;

public class Reason implements Serializable {
    private int id;
    private String shortReason;
    private String reason;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortReason() {
        return shortReason;
    }

    public void setShortReason(String shortReason) {
        this.shortReason = shortReason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reason reason1 = (Reason) o;
        return id == reason1.id &&
                Objects.equals(shortReason, reason1.shortReason) &&
                Objects.equals(reason, reason1.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shortReason, reason);
    }
}
