package com.journal.nn.school123.pojo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.journal.nn.school123.util.CurrentPeriodUtil.YEAR_PERIOD;
import static com.journal.nn.school123.util.CurrentPeriodUtil.clearCalendar;

public class Data implements Serializable {
    private final String id;
    private final String version;
    private String secondName;
    private String firstName;
    private String middleName;
    private String schoolDescription;
    private String currentYear;
    private int unreadMessageCount;
    private Map<Integer, String> subjects;
    private String currentClass;
    private String classTeacher;
    private Map<Integer, String> teachers;
    private List<Message> messages;
    private Map<String, Period> periods;
    private String classId;
    private Map<Integer, Subject> studentSubjects;
    private List<String> classPeriods;
    private Map<Date, Set<Journal>> journals;
    private Map<Integer, List<Mark>> marks;
    private String groupNumber;
    private Map<Date, Set<Journal>> periodJournals;
    private Map<Integer, Reason> reasons;
    private List<FinalMark> finalMarks;
    private Set<String> loadedPeriodIds = new HashSet<>();
    private Set<Long> loadedJournalWeeks = new HashSet<>();
    private Date saveDate = new Date();

    public Data(String id,
                String version) {
        this.id = id;
        this.version = version;
        loadedPeriodIds.add(YEAR_PERIOD);
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSchoolDescription() {
        return schoolDescription;
    }

    public void setSchoolDescription(String schoolDescription) {
        this.schoolDescription = schoolDescription;
    }

    public String getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(String currentYear) {
        this.currentYear = currentYear;
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public Map<Integer, String> getSubjects() {
        return subjects;
    }

    public void setSubjects(Map<Integer, String> subjects) {
        this.subjects = subjects;
    }

    public String getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(String currentClass) {
        this.currentClass = currentClass;
    }

    public String getClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(String classTeacher) {
        this.classTeacher = classTeacher;
    }

    public Map<Integer, String> getTeachers() {
        return teachers;
    }

    public void setTeachers(Map<Integer, String> teachers) {
        this.teachers = teachers;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Map<String, Period> getPeriods() {
        return periods;
    }

    public void setPeriods(Map<String, Period> periods) {
        this.periods = periods;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public Map<Integer, Subject> getStudentSubjects() {
        return studentSubjects;
    }

    public void setStudentSubjects(Map<Integer, Subject> studentSubjects) {
        this.studentSubjects = studentSubjects;
    }

    public List<String> getClassPeriods() {
        return classPeriods;
    }

    public void setClassPeriods(List<String> classPeriods) {
        this.classPeriods = classPeriods;
    }

    public Map<Date, Set<Journal>> getJournals() {
        return journals;
    }

    public void setJournals(Map<Date, Set<Journal>> journals) {
        this.journals = journals;
    }

    public void addJournals(Map<Date, Set<Journal>> journals) {
        this.journals.putAll(journals);
    }

    public Map<Integer, List<Mark>> getMarks() {
        return marks;
    }

    public void setMarks(Map<Integer, List<Mark>> marks) {
        this.marks = marks;
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
        Data data = (Data) o;
        return unreadMessageCount == data.unreadMessageCount &&
                Objects.equals(id, data.id) &&
                Objects.equals(version, data.version) &&
                Objects.equals(secondName, data.secondName) &&
                Objects.equals(firstName, data.firstName) &&
                Objects.equals(middleName, data.middleName) &&
                Objects.equals(schoolDescription, data.schoolDescription) &&
                Objects.equals(currentYear, data.currentYear) &&
                Objects.equals(subjects, data.subjects) &&
                Objects.equals(currentClass, data.currentClass) &&
                Objects.equals(classTeacher, data.classTeacher) &&
                Objects.equals(teachers, data.teachers) &&
                Objects.equals(messages, data.messages) &&
                Objects.equals(periods, data.periods) &&
                Objects.equals(classId, data.classId) &&
                Objects.equals(studentSubjects, data.studentSubjects) &&
                Objects.equals(classPeriods, data.classPeriods) &&
                Objects.equals(journals, data.journals) &&
                Objects.equals(marks, data.marks) &&
                Objects.equals(groupNumber, data.groupNumber) &&
                Objects.equals(reasons, data.reasons) &&
                Objects.equals(finalMarks, data.finalMarks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                version,
                secondName,
                firstName,
                middleName,
                schoolDescription,
                currentYear,
                unreadMessageCount,
                subjects,
                currentClass,
                classTeacher,
                teachers,
                messages,
                periods,
                classId,
                studentSubjects,
                classPeriods,
                journals,
                marks,
                groupNumber,
                reasons,
                finalMarks);
    }

    public void update(Data oldData) {
        if (oldData.marks != null) {
            marks.forEach((subjectId, marks) -> {
                List<Mark> oldMarks = oldData.marks.get(subjectId);
                if (oldMarks != null) {
                    for (Mark mark : marks) {
                        Mark oldMark = oldMarks.stream()
                                .filter(m -> m.equals(mark))
                                .findAny()
                                .orElse(null);
                        if (oldMark == null) {
                            mark.setJustReceived(true);
                        } else {
                            mark.setJustReceived(oldMark.isJustReceived());
                        }
                    }
                }
            });
        }
        if (oldData.periodJournals != null) {
            oldData.periodJournals.forEach((date, journal) -> {
                if (!periodJournals.containsKey(date)) {
                    periodJournals.put(date, journal);
                }
            });
        }
        if (oldData.loadedPeriodIds != null) {
            loadedPeriodIds.addAll(oldData.loadedPeriodIds);
        }
        if (oldData.journals != null) {
            oldData.journals.forEach((date, journal) -> {
                if (!journals.containsKey(date)) {
                    journals.put(date, journal);
                }
            });
        }
        if (oldData.loadedJournalWeeks != null) {
            loadedJournalWeeks.addAll(oldData.loadedJournalWeeks);
        }
        saveDate = new Date();
    }

    void clear() {
        if (marks != null) {
            marks.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(mark -> mark.setJustReceived(false));
        }
    }

    public Map<Date, Set<Journal>> getPeriodJournals() {
        return periodJournals;
    }

    public void setPeriodJournals(Map<Date, Set<Journal>> periodJournals) {
        this.periodJournals = periodJournals;
    }

    public void addPeriodJournals(Map<Date, Set<Journal>> periodJournals) {
        this.periodJournals.putAll(periodJournals);
    }

    public Map<Integer, Reason> getReasons() {
        return reasons;
    }

    public void setReasons(Map<Integer, Reason> reasons) {
        this.reasons = reasons;
    }

    public void setFinalMarks(List<FinalMark> finalMarks) {
        this.finalMarks = finalMarks;
    }

    public List<FinalMark> getFinalMarks() {
        return finalMarks;
    }

    public boolean isPeriodLoaded(String periodId) {
        return loadedPeriodIds.contains(periodId);
    }

    public void periodLoaded(Period period) {
        loadedPeriodIds.add(period.getId());
    }

    public boolean isJournalLoaded(Long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        clearCalendar(calendar);
        return loadedJournalWeeks.contains(calendar.getTimeInMillis());
    }

    public void journalLoaded(Calendar calendar) {
        clearCalendar(calendar);
        loadedJournalWeeks.add(calendar.getTimeInMillis());
    }

    public Date getSaveDate() {
        return saveDate;
    }
}
