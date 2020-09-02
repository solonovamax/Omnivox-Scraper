package com.solostudios.omnivoxscraper.api.calendar.events;

public interface CommunityEvent extends TimedEvent {
    @Override
    default String getCalendarName() {
        return "Community Calendar";
    }
    
    String getEventDetails();
}
