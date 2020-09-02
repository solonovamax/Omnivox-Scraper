package com.solostudios.omnivoxscraper.api.calendar.events;

public interface AcademicCalendarEvent extends GenericEvent {
    @Override
    default String getCalendarName() {
        return "Academic Calendar";
    }
}
