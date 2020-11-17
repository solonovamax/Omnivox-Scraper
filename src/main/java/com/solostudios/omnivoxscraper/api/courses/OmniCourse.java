package com.solostudios.omnivoxscraper.api.courses;

import com.solostudios.omnivoxscraper.api.calendar.OmniSemester;
import com.solostudios.omnivoxscraper.api.calendar.events.OmniClass;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
    String getClassName();
    
    @NotNull
    String getCourseNumber();
    
    int getSection();
    
    @NotNull
    String getTeacherName();
    
    @NotNull
    LocalDate getStartDate();
    
    @NotNull
    LocalDate getEndDate();
    
    @NotNull
    Period getCourseLength();
    
    @NotNull
    Duration getTheoryHours();
    
    @NotNull
    Duration getLabHours();
    
    @NotNull
    Duration getWorkHours();
    
    @NotNull
    String getExtraInfo();
    
    //    List<OmniClass> getClassList();
    @NotNull
    List<OmniClass> getClassListAtDay(@NotNull LocalDate date);
    
    @NotNull
    OmniSemester getSemester();
}
