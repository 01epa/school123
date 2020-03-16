package com.journal.nn.school123.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Journal implements Serializable {
    private int subjectId;
    private Date date;
    private String task;
    private String taskDetails;
    private String groupNumber;

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getTaskDetails() {
        return taskDetails;
    }

    public void setTaskDetails(String taskDetails) {
        this.taskDetails = taskDetails;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Journal journal = (Journal) o;
        return Objects.equals(subjectId, journal.subjectId) &&
                Objects.equals(date, journal.date) &&
                Objects.equals(task, journal.task) &&
                Objects.equals(taskDetails, journal.taskDetails) &&
                Objects.equals(groupNumber, journal.groupNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectId,
                date,
                task,
                taskDetails,
                groupNumber);
    }
}
