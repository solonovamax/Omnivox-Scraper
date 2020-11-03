package com.solostudios.omnivoxscraper.api.calendar.events;

import org.jetbrains.annotations.NotNull;


/**
 * An even representing a CommunityEvent on the calendar.
 *
 * @author solonovamax
 */
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
}
