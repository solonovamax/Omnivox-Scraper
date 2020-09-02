package com.solostudios.omnivoxscraper.api.calendar.events;

import com.solostudios.omnivoxscraper.api.calendar.OmniDay;
import net.fortuna.ical4j.model.component.VEvent;


public interface GenericEvent {
    OmniDay getDay();
    
    VEvent iCalendarEvent();
    
    String getEventName();
    
    String getCalendarName();
}
