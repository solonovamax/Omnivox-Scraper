package com.solostudios.omnivoxscraper.api.calendar.events;

import com.solostudios.omnivoxscraper.api.calendar.OmniDay;
import net.fortuna.ical4j.model.component.VEvent;
import org.jetbrains.annotations.NotNull;


/**
 * @author solonovamax
 */
public interface GenericEvent {
    /**
     * The object representing the day this even is on.
     *
     * @return
     */
    @NotNull
    OmniDay getDay();
    
    /**
     * The iCalendar event for this event.
     *
     * @return A never nul ical even representing this object.
     *         It will have to correct time, days
     */
    @NotNull
    VEvent iCalendarEvent();
    
    /**
     * The name of this event.
     * <p>
     * This is never null and never an empty string.
     *
     * @return The name of this event.
     */
    @NotNull
    String getEventName();
    
    /**
     * The name of the calendar this event belongs to.
     *
     * @return The name of the calendar this even belongs to on Omnivox.
     */
    @NotNull
    String getCalendarName();
    
    /**
     * Gets the details of the event.
     * <p>
     * This may be an empty string if there are no details, but never {@code null}.
     *
     * @return The details of this event.
     */
    @NotNull
    String getEventDetails();
}
