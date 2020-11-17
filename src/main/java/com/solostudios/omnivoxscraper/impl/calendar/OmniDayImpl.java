package com.solostudios.omnivoxscraper.impl.calendar;

import com.solostudios.omnivoxscraper.api.calendar.OmniDay;
import com.solostudios.omnivoxscraper.api.calendar.OmniSemester;
import com.solostudios.omnivoxscraper.api.calendar.events.*;
import net.fortuna.ical4j.model.component.VEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@SuppressWarnings("unused")
public class OmniDayImpl implements OmniDay {
    private final OmniSemester                semester;
    private final LocalDate                   date;
    private final List<OmniClass>             classList;
    private final List<LeaEvent>              leaEventList;
    private final List<CommunityEvent>        communityEventList;
    private final List<AcademicCalendarEvent> academicEventList;
    
    public OmniDayImpl(OmniSemester semester, LocalDate date) {
        this.semester = semester;
        this.date = date;
        
        classList = new ArrayList<>();
        leaEventList = new ArrayList<>();
        communityEventList = new ArrayList<>();
        academicEventList = new ArrayList<>();
    }
    
    @Override
    public OmniSemester getSemester() {
        return semester;
    }
    
    @Override
    public LocalDate getDate() {
        return date;
    }
    
    @Override
    public List<GenericEvent> getEventList() {
        List<GenericEvent> genericEventList = new ArrayList<>();
        genericEventList.addAll(classList);
        genericEventList.addAll(leaEventList);
        genericEventList.addAll(communityEventList);
        genericEventList.addAll(academicEventList);
        return genericEventList;
    }
    
    @Override
    public List<OmniClass> getClassEventList() {
        return classList;
    }
    
    @Override
    public List<LeaEvent> getLeaEventList() {
        return leaEventList;
    }
    
    @Override
    public List<CommunityEvent> getCommunityEventList() {
        return communityEventList;
    }
    
    @Override
    public List<AcademicCalendarEvent> getAcademicEventList() {
        return academicEventList;
    }
    
    @Override
    public List<VEvent> getiCalEventList() {
        return getEventList().stream()
                             .map(GenericEvent::iCalendarEvent)
                             .collect(Collectors.toList());
    }
    
    public OmniDayImpl addOmniClass(OmniClass omniClass) {
        classList.add(omniClass);
        return this;
    }
    
    public OmniDayImpl addOmniClasses(OmniClass... omniClass) {
        classList.addAll(Arrays.asList(omniClass));
        return this;
    }
    
    public OmniDayImpl addLeaEvent(LeaEvent event) {
        leaEventList.add(event);
        return this;
    }
    
    public OmniDayImpl addLeaEvents(LeaEvent... event) {
        leaEventList.addAll(Arrays.asList(event));
        return this;
    }
    
    public OmniDayImpl addCommunityEvent(CommunityEvent event) {
        communityEventList.add(event);
        return this;
    }
    
    public OmniDayImpl addCommunityEvents(CommunityEvent... event) {
        communityEventList.addAll(Arrays.asList(event));
        return this;
    }
    
    public OmniDayImpl addAcademicEvent(AcademicCalendarEvent event) {
        academicEventList.add(event);
        return this;
    }
    
    public OmniDayImpl addAcademicEvents(AcademicCalendarEvent... event) {
        academicEventList.addAll(Arrays.asList(event));
        return this;
    }
    
/*    @Override
    public List<VEvent> getiCalClassEventList() {
        return null;
    }
    
    @Override
    public List<VEvent> getiCalLeaEventList() {
        return null;
    }
    
    @Override
    public List<VEvent> getiCalCommunityEventList() {
        return null;
    }
    
    @Override
    public List<VEvent> getiCalAcademicEventList() {
        return null;
    }*/
}
