package com.solostudios.omnivoxscraper.api.calendar;

import com.solostudios.omnivoxscraper.api.calendar.events.OmniClass;
import net.fortuna.ical4j.model.component.VEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@SuppressWarnings("unused")
public interface OmniCalendar {
    List<OmniSemester> getAvailableSemesters();
    
    Optional<OmniSemester> getCurrentSemester();
    
    List<OmniClass> getClassesToday();
    
    default OmniDay getToday() {
        return getDay(LocalDate.now());
    }
    
    OmniDay getDay(LocalDate date);
    
    default OmniDay getTomorrow() {
        return getDay(LocalDate.now().plusDays(1));
    }
    
    List<VEvent> iCalendarEvents();
}
