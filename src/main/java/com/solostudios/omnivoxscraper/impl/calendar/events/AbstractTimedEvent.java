package com.solostudios.omnivoxscraper.impl.calendar.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solostudios.omnivoxscraper.api.calendar.OmniDay;
import com.solostudios.omnivoxscraper.api.calendar.events.TimedEvent;
import com.solostudios.omnivoxscraper.impl.utils.ical.ICalUtil;
import net.fortuna.ical4j.model.component.VEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalTime;


public abstract class AbstractTimedEvent extends AbstractEvent implements TimedEvent {
    protected final LocalTime startTime;
    protected final LocalTime endTime;
    
    protected AbstractTimedEvent(OmniDay day, String eventName, String calendarName, String eventDetails, LocalTime startTime,
                                 LocalTime endTime) {
        super(day, eventName, calendarName, eventDetails);
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    @JsonIgnore
    @Override
    public @Nullable Duration getEventDuration() {
        return Duration.between(startTime, endTime);
    }
    
    @Override
    public LocalTime getEventStartTime() {
        return startTime;
    }
    
    @Override
    public LocalTime getEventEndTime() {
        return endTime;
    }
    
    @Override
    public @NotNull VEvent iCalendarEvent() {
        return ICalUtil.getEventBuilder(getDay().getDate(), startTime)
                       .setEndDate(getDay().getDate())
                       .build();
    }
}
