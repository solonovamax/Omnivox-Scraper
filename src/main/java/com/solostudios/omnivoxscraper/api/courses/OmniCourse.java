package com.solostudios.omnivoxscraper.api.courses;

import com.solostudios.omnivoxscraper.api.calendar.OmniSemester;
import com.solostudios.omnivoxscraper.api.calendar.events.OmniClass;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;


/**
 * Represents a course in Omnivox.
 * <p>
 * This is different from an {@link OmniClass}. This is not tied to a schedule.
 */
public interface OmniCourse {
    String getClassName();
    
    String getCourseNumber();
    
    int getSection();
    
    String getTeacherName();
    
    LocalDate getStartDate();
    
    LocalDate getEndDate();
    
    Period getCourseLength();
    
    Duration getTheoryHours();
    
    Duration getLabHours();
    
    Duration getWorkHours();
    
    String getExtraInfo();
    
    List<OmniClass> getClassList();
    
    OmniSemester getSemester();
}
