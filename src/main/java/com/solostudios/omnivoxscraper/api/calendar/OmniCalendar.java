package com.solostudios.omnivoxscraper.api.calendar;

import com.solostudios.omnivoxscraper.api.calendar.events.OmniClass;
import net.fortuna.ical4j.model.component.VEvent;

import java.util.List;


public interface OmniCalendar {
    List<OmniSemester> getAvailableSemesters();
    
    OmniSemester getCurrentSemester();
    
    List<OmniClass> getClassesToday();
    
    List<VEvent> iCalendarEvents();
}
