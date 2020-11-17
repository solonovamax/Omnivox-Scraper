package com.solostudios.omnivoxscraper.api.calendar;

import com.solostudios.omnivoxscraper.api.calendar.events.OmniClass;

import java.time.DayOfWeek;
import java.util.List;


public interface OmniWeekDay {
    
    DayOfWeek getDay();
    
    OmniSemester getSemester();
    
    List<OmniClass> getClassEventList();
}
