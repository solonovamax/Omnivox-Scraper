package com.solostudios.omnivoxscraper.impl.utils.ical;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


public class ICalUtil {
    
    /**
     * No fun allowed.
     */
    private ICalUtil() {
    }
    
    /**
     * Gets a new event builder for building custom events.
     *
     * @param startDateTime The time and date at which the event starts.
     *
     * @return The new event builder.
     */
    public static VEventBuilder getEventBuilder(LocalDateTime startDateTime) {
        return new VEventBuilder(startDateTime.toLocalDate(), startDateTime.toLocalTime());
    }
    
    /**
     * Gets a new event builder for building custom events.
     *
     * @param startDateTime The time and date at which the event starts.
     *
     * @return The new event builder.
     */
    public static VEventBuilder getEventBuilder(LocalDateTime startDateTime, Duration duration) {
        return new VEventBuilder(startDateTime.toLocalDate(), startDateTime.toLocalTime()).setEndDate(startDateTime.plus(duration)
                                                                                                                   .toLocalDate())
                                                                                          .setEndTime(startDateTime.plus(duration)
                                                                                                                   .toLocalTime());
    }
    
    /**
     * Gets a new event builder for building custom events.
     *
     * @param startDateTime The time and date at which the event starts.
     * @param endDateTime   The time and date and which the event ends.
     *
     * @return The new event builder.
     */
    public static VEventBuilder getEventBuilder(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return new VEventBuilder(startDateTime.toLocalDate(), startDateTime.toLocalTime()).setEndDate(endDateTime.toLocalDate())
                                                                                          .setEndTime(endDateTime.toLocalTime());
    }
    
    /**
     * Gets a new event builder for building custom events.
     *
     * @param startDate The date at which the event starts.
     * @param startTime The time at which the event starts.
     *
     * @return The new event builder.
     */
    public static VEventBuilder getEventBuilder(LocalDate startDate, LocalTime startTime) {
        return new VEventBuilder(startDate, startTime);
    }
    
    /**
     * Gets a new event builder for building custom events.
     *
     * @param startDate The date at which the event starts.
     * @param startTime The time at which the event starts.
     * @param endDate   The date at which the event ends.
     * @param endTime   The time at which the event ends.
     *
     * @return The new event builder.
     */
    public static VEventBuilder getEventBuilder(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        return new VEventBuilder(startDate, startTime).setEndDate(endDate)
                                                      .setEndTime(endTime);
    }
    
    
}
