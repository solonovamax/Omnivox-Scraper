package com.solostudios.omnivoxscraper.api.calendar.events;

import com.solostudios.omnivoxscraper.api.courses.OmniCourse;

import java.time.Duration;
import java.time.LocalTime;


/**
 * Represents a class in an omnivox schedule.
 * <p>
 * This is different from an {@link OmniCourse}. This is tied to a schedule and represents a time
 */
public interface OmniClass extends TimedEvent {
    Duration getClassDuration();
    
    LocalTime getClassStartTime();
    
    OmniCourse getCourse();
    
    boolean isOnline();
}
