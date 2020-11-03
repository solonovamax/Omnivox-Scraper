package com.solostudios.omnivoxscraper.api.calendar.events;

import com.solostudios.omnivoxscraper.api.calendar.OmniDay;
import net.fortuna.ical4j.model.component.VEvent;
import org.jetbrains.annotations.NotNull;


public interface GenericEvent {
    @NotNull
    OmniDay getDay();
    
    @NotNull
    VEvent iCalendarEvent();
    
    @NotNull
    String getEventName();
    
    /**
     * The name of the calendar this event belongs to.
     *
     * @return The name of the calendar this even belongs to on Omnivox.
     */
    @NotNull
    String getCalendarName();
}
