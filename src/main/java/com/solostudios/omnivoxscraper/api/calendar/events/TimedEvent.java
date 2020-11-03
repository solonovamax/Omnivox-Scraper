package com.solostudios.omnivoxscraper.api.calendar.events;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalTime;


public interface TimedEvent extends GenericEvent {
    @Nullable
    Duration getEventDuration();
    
    LocalTime getEventStartTime();
}
