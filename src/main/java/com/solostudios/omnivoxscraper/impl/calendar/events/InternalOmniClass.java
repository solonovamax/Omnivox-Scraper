package com.solostudios.omnivoxscraper.impl.calendar.events;

import com.solostudios.omnivoxscraper.api.courses.OmniCourse;

import java.time.DayOfWeek;
import java.time.LocalTime;


public class InternalOmniClass {
    private final LocalTime  startTime;
    private final LocalTime  endTime;
    private final DayOfWeek  date;
    private final OmniCourse course;
    
    public InternalOmniClass(LocalTime startTime, LocalTime endTime, DayOfWeek date, OmniCourse course) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.course = course;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public DayOfWeek getDate() {
        return date;
    }
}
