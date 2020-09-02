package com.solostudios.omnivoxscraper.old.calandar;

import java.time.Duration;
import java.time.LocalTime;


public class OmnivoxClass {
    private final Duration      classDuration;
    private final LocalTime     classStartTime;
    private final OmnivoxCourse course;
    private final OmnivoxDay    day;
    private final boolean       isOnline;
    
    public OmnivoxClass(Duration classDuration, LocalTime classStartTime, com.solostudios.omnivoxscraper.old.calandar.OmnivoxCourse course,
                        com.solostudios.omnivoxscraper.old.calandar.OmnivoxDay day, boolean isOnline) {
        this.classDuration = classDuration;
        this.classStartTime = classStartTime;
        this.course = course;
        this.day = day;
        this.isOnline = isOnline;
    }
    
    public Duration getClassDuration() {
        return classDuration;
    }
    
    public LocalTime getClassStartTime() {
        return classStartTime;
    }
    
    public com.solostudios.omnivoxscraper.old.calandar.OmnivoxCourse getCourse() {
        return course;
    }
    
    public com.solostudios.omnivoxscraper.old.calandar.OmnivoxDay getDay() {
        return day;
    }
    
    public boolean isOnline() {
        return isOnline;
    }
    
    @Override
    public String toString() {
        return toJSONString();
    }
    
    
    public String toJSONString() {
        return "{" +
               "\"name\":\"OmnivoxClass\"" +
               ", \"classDuration\":" + classDuration.toDays() +
               ", \"classStartTime\":\"" + classStartTime + "\"" +
               ", \"course\":" + course +
               ", \"day\":\"" + day.getDay() + "\"" +
               ", \"isOnline\":" + isOnline +
               "}";
    }
}
