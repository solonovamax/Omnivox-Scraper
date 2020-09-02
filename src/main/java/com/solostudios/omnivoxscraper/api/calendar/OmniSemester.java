package com.solostudios.omnivoxscraper.api.calendar;

import com.solostudios.omnivoxscraper.api.calendar.events.OmniClass;
import com.solostudios.omnivoxscraper.api.courses.OmniCourse;

import java.util.List;


public interface OmniSemester {
    List<OmniCourse> getCourseList();
    
    List<OmniDay> getDays();
    
    List<OmniClass>[] getClassList();
    
    int getSemesterId();
    
    String getSeason();
    
    int getYear();
    
    boolean isCurrent();
    
    String getSemesterSeason();
}
