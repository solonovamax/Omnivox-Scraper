package com.solostudios.omnivoxscraper.impl.calendar.events;

import com.solostudios.omnivoxscraper.api.calendar.OmniDay;
import com.solostudios.omnivoxscraper.api.calendar.events.GenericEvent;
import org.jetbrains.annotations.NotNull;


public abstract class AbstractEvent implements GenericEvent {
    protected final OmniDay day;
    protected final String  eventName;
    protected final String  calendarName;
    protected final String  eventDetails;
    
    protected AbstractEvent(OmniDay day, String eventName, String calendarName, String eventDetails) {
        this.day = day;
        this.eventName = eventName;
        this.calendarName = calendarName;
        this.eventDetails = eventDetails;
    }
    
    @Override
    public @NotNull OmniDay getDay() {
        return day;
    }
    
    @Override
    public @NotNull String getEventName() {
        return eventName;
    }
    
    @Override
    public @NotNull String getCalendarName() {
        return calendarName;
    }
    
    @Override
    public @NotNull String getEventDetails() {
        return eventDetails;
    }
}
