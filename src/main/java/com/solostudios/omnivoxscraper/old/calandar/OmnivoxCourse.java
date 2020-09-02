package com.solostudios.omnivoxscraper.old.calandar;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class OmnivoxCourse {
    private final List<OmnivoxClass> classList;
    private final String             className;
    private final String             courseNumber;
    private final int                section;
    private final String             teacherName;
    private final OmnivoxSemester    semester;
    private final LocalDate          startDate;
    private final Period             courseLength;
    private final Duration           theoryHours;
    private final Duration           labHours;
    private final Duration           workHours;
    private final String             extraInfo;
    
    public OmnivoxCourse(String className, String courseNumber, int section, String teacherName, OmnivoxSemester semester,
                         LocalDate startDate, LocalDate endDate, Duration theoryHours, Duration labHours, Duration workHours,
                         String extraInfo) {
        this(className, courseNumber, section, teacherName, semester, startDate, Period.between(startDate, endDate),
             theoryHours, labHours, workHours, extraInfo);
    }
    
    public OmnivoxCourse(String className, String courseNumber, int section, String teacherName, OmnivoxSemester semester,
                         LocalDate startDate, Period courseLength, Duration theoryHours, Duration labHours, Duration workHours,
                         String extraInfo) {
        this.className = className;
        this.courseNumber = courseNumber;
        this.section = section;
        this.teacherName = teacherName;
        this.semester = semester;
        this.startDate = startDate;
        this.courseLength = courseLength;
        this.theoryHours = theoryHours;
        this.labHours = labHours;
        this.workHours = workHours;
        this.extraInfo = extraInfo;
        
        classList = new ArrayList<>();
    }
    
    public String getClassName() {
        return className;
    }
    
    public String getCourseNumber() {
        return courseNumber;
    }
    
    public int getSection() {
        return section;
    }
    
    public String getTeacherName() {
        return teacherName;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public LocalDate getEndDate() {
        return startDate.plus(courseLength);
    }
    
    public Period getCourseLength() {
        return courseLength;
    }
    
    public Duration getTheoryHours() {
        return theoryHours;
    }
    
    public Duration getLabHours() {
        return labHours;
    }
    
    public Duration getWorkHours() {
        return workHours;
    }
    
    public String getExtraInfo() {
        return extraInfo;
    }
    
    public List<OmnivoxClass> getClassList() {
        return Collections.unmodifiableList(classList);
    }
    
    public OmnivoxSemester getSemester() {
        return semester;
    }
    
    void addClass(OmnivoxClass omnivoxClass) {
        this.classList.add(omnivoxClass);
    }
    
    @Override
    public String toString() {
        return toJSONString();
    }
    
    
    public String toJSONString() {
        return "{" +
               "\"name\":\"OmnivoxCourse\"" +
               ", \"classList\":" + classList +
               ", \"className\":\"" + className + "\"" +
               ", \"courseNumber\":\"" + courseNumber + "\"" +
               ", \"section\":" + section +
               ", \"teacherName\":\"" + teacherName + "\"" +
               ", \"startDate\":\"" + startDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "\"" +
               ", \"courseLength\":\"" + String.format("%02d-%02d", courseLength.getMonths(), getCourseLength().getDays()) + "\"" +
               ", \"theoryHours\":" + theoryHours.toHours() +
               ", \"labHours\":" + labHours.toHours() +
               ", \"workHours\":" + workHours.toHours() +
               ", \"extraInfo\":\"" + extraInfo.replace("\n", "\\n") + "\"" +
               "}";
    }
}
