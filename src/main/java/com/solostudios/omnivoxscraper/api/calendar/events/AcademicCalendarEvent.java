package com.solostudios.omnivoxscraper.api.calendar.events;

import org.jetbrains.annotations.NotNull;


/**
 * A calendar even for the Academic calendar on Omnivox.
 *
 * @author solonovamax
 */
public interface AcademicCalendarEvent extends GenericEvent {
    /**
     * @return {@code "Academic Calendar"}
     *
     * @inheritDoc
     */
    @Override
    default @NotNull String getCalendarName() {
        return "Academic Calendar";
    }
}
