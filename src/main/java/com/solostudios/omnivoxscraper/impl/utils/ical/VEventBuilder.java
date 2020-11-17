package com.solostudios.omnivoxscraper.impl.utils.ical;

import lombok.SneakyThrows;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.FixedUidGenerator;
import net.fortuna.ical4j.util.HostInfo;
import net.fortuna.ical4j.util.SimpleHostInfo;
import net.fortuna.ical4j.util.UidGenerator;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.GregorianCalendar;


public class VEventBuilder {
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;
    private String    summary = "";
    
    public VEventBuilder(@NotNull LocalDate startDate, @NotNull LocalTime startTime) {
        this.startDate = startDate;
        this.startTime = startTime;
    }
    
    @NotNull
    public VEventBuilder setSummary(String summary) {
        this.summary = summary;
        return this;
    }
    
    @NotNull
    public VEventBuilder setStartDate(@NotNull LocalDate date) {
        this.startDate = date;
        return this;
    }
    
    @NotNull
    public VEventBuilder setStartTime(@NotNull LocalTime startTime) {
        this.startTime = startTime;
        return this;
    }
    
    @NotNull
    public VEventBuilder setEndTime(@NotNull LocalTime startTime) {
        this.endTime = startTime;
        return this;
    }
    
    @NotNull
    public VEventBuilder setEndDate(@NotNull LocalDate date) {
        this.endDate = date;
        return this;
    }
    
    @SuppressWarnings("DuplicatedCode")
    @NotNull
    @SneakyThrows(UnknownHostException.class)
    public VEvent build() {
        if (startDate == null && startTime == null)
            throw new IllegalArgumentException("You must provide a start date and time.");

//        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
//        TimeZone         timezone = registry.getTimeZone("America/Toronto"); //un-hardcode this later
//        VTimeZone        tz       = timezone.getVTimeZone();
        VEvent event;
        
        java.util.Calendar startCalendar = new GregorianCalendar();
//            startCalendar.setTimeZone(timezone);
        startCalendar.set(java.util.Calendar.MONTH, this.startDate.getMonthValue());
        startCalendar.set(java.util.Calendar.DAY_OF_MONTH, this.startDate.getDayOfMonth());
        startCalendar.set(java.util.Calendar.YEAR, this.startDate.getYear());
        startCalendar.set(java.util.Calendar.HOUR_OF_DAY, this.startTime.getHour());
        startCalendar.set(java.util.Calendar.MINUTE, this.startTime.getMinute());
        startCalendar.set(java.util.Calendar.SECOND, this.startTime.getSecond());
        
        DateTime startDateTime = new DateTime(startCalendar.getTime());
        
        if (endDate == null || endTime == null) {
            event = new VEvent(startDateTime, summary);
        } else {
            // Start Date is on: April 1, 2008, 9:00 am
            java.util.Calendar endCalendar = new GregorianCalendar();
//            endCalendar.setTimeZone(timezone);
            endCalendar.set(java.util.Calendar.MONTH, this.endDate.getMonthValue());
            endCalendar.set(java.util.Calendar.DAY_OF_MONTH, this.endDate.getDayOfMonth());
            endCalendar.set(java.util.Calendar.YEAR, this.endDate.getYear());
            endCalendar.set(java.util.Calendar.HOUR_OF_DAY, this.endTime.getHour());
            endCalendar.set(java.util.Calendar.MINUTE, this.endTime.getMinute());
            endCalendar.set(java.util.Calendar.SECOND, this.endTime.getSecond());
            
            DateTime endDateTime = new DateTime(endCalendar.getTime());
            event = new VEvent(startDateTime, endDateTime, summary);
        }
        HostInfo     hostInfo = new SimpleHostInfo(InetAddress.getLocalHost().getHostName());
        UidGenerator ug       = new FixedUidGenerator(hostInfo, Long.toString(ProcessHandle.current().pid()));
        Uid          uid      = ug.generateUid();
        event.getProperties().add(uid);
        return event;
    }
}
