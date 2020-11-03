package com.solostudios.omnivoxscraper.api.calendar.events;

import org.jetbrains.annotations.NotNull;


public interface CommunityEvent extends TimedEvent {
    /**
     * @return {@code "Community Calendar"}
     *
     * @inheritDoc
     */
    @NotNull
    @Override
    default String getCalendarName() {
        return "Community Calendar";
    }
    
    /**
     * Gets the details of the event.
     *
     * @return
     */
    String getEventDetails();
}
