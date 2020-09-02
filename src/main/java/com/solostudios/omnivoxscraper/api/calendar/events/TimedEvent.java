package com.solostudios.omnivoxscraper.api.calendar.events;

import java.time.Duration;
import java.time.LocalTime;


public interface TimedEvent extends GenericEvent {
    Duration getEventDuration();
    
    LocalTime getEventStartTime();
}
