package com.solostudios.omnivoxscraper.impl.calendar.events;

import com.solostudios.omnivoxscraper.api.calendar.OmniDay;
import com.solostudios.omnivoxscraper.api.calendar.events.OmniClass;
import com.solostudios.omnivoxscraper.api.courses.OmniCourse;

import java.time.LocalTime;


public class OmniClassImpl extends AbstractTimedEvent implements OmniClass {
    private final OmniCourse course;
    private final boolean    isOnline;
    
    protected OmniClassImpl(OmniDay day, String className, LocalTime startTime, LocalTime endTime, OmniCourse course, boolean isOnline) {
        super(day, className, "Courses", "", startTime, endTime);
        this.course = course;
        this.isOnline = isOnline;
    }
    
    @Override
    public OmniCourse getCourse() {
        return course;
    }
    
    @Override
    public boolean isOnline() {
        return isOnline;
    }
}
