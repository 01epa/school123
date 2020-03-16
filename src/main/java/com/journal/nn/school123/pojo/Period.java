package com.journal.nn.school123.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Period implements Serializable {
    private String id;
    private String name;
    private Date from;
    private Date to;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Period period = (Period) o;
        return Objects.equals(id, period.id) &&
                Objects.equals(name, period.name) &&
                Objects.equals(from, period.from) &&
                Objects.equals(to, period.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                name,
                from,
                to);
    }
}
