package com.solostudios.omnivoxscraper.impl.calendar;

import com.solostudios.omnivoxscraper.api.calendar.OmniSemester;
import com.solostudios.omnivoxscraper.api.calendar.OmniWeekDay;
import com.solostudios.omnivoxscraper.api.calendar.events.OmniClass;

import java.time.DayOfWeek;
import java.util.List;


public class OmniWeekDayImpl implements OmniWeekDay {
    private final DayOfWeek       day;
    private final OmniSemester    semester;
    private final List<OmniClass> omniClasses;
    
    public OmniWeekDayImpl(DayOfWeek day, OmniSemester semester, List<OmniClass> omniClasses) {
        this.day = day;
        this.semester = semester;
        this.omniClasses = omniClasses;
    }
    
    @Override
    public DayOfWeek getDay() {
        return day;
    }
    
    @Override
    public OmniSemester getSemester() {
        return semester;
    }
    
    @Override
    public List<OmniClass> getClassEventList() {
        return omniClasses;
    }
}
