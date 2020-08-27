package com.solostudios.omnivoxscraper.calandar;

import java.time.Duration;
import java.time.LocalTime;


public class OmnivoxClass {
    private final Duration      classDuration;
    private final LocalTime     classStartTime;
    private final OmnivoxCourse course;
    private final OmnivoxDay    day;
    private final boolean       isOnline;
    
    public OmnivoxClass(Duration classDuration, LocalTime classStartTime, OmnivoxCourse course,
                        OmnivoxDay day, boolean isOnline) {
        this.classDuration = classDuration;
        this.classStartTime = classStartTime;
        this.course = course;
        this.day = day;
        this.isOnline = isOnline;
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
