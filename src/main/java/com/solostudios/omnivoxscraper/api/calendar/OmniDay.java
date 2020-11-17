package com.solostudios.omnivoxscraper.api.calendar;


import com.solostudios.omnivoxscraper.api.calendar.events.*;
import net.fortuna.ical4j.model.component.VEvent;

import java.time.LocalDate;
import java.util.List;


public interface OmniDay {
    OmniSemester getSemester();
    
    LocalDate getDate();
    
    List<GenericEvent> getEventList();
    
    List<OmniClass> getClassEventList();
    
    List<LeaEvent> getLeaEventList();
    
    List<CommunityEvent> getCommunityEventList();
    
    List<AcademicCalendarEvent> getAcademicEventList();
    
    List<VEvent> getiCalEventList();

//    List<VEvent> getiCalClassEventList();
//
//    List<VEvent> getiCalLeaEventList();
//
//    List<VEvent> getiCalCommunityEventList();
//
//    List<VEvent> getiCalAcademicEventList();
}
