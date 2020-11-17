package com.solostudios.omnivoxscraper.impl.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.solostudios.omnivoxscraper.api.calendar.OmniSemester;
import com.solostudios.omnivoxscraper.api.calendar.OmniWeekDay;
import com.solostudios.omnivoxscraper.api.calendar.events.OmniClass;
import com.solostudios.omnivoxscraper.api.courses.OmniCourse;
import com.solostudios.omnivoxscraper.impl.courses.OmniCourseImpl;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@ToString
@EqualsAndHashCode
@SuppressWarnings("unused")
public class OmniSemesterImpl implements OmniSemester {
    private final int               semesterId;
    private final String            season;
    private final Year              year;
    @JsonProperty("current")
    private final boolean           isCurrent;
    @JsonProperty("days")
    private final List<OmniWeekDay> dayList;
    @JsonProperty("courses")
    private final List<OmniCourse>  courseList;
//    private final List<List<OmniClass>> classDayList;
    
    @SneakyThrows(IOException.class)
    public OmniSemesterImpl(HtmlPage semesterPage, int semesterId, String season, int year, boolean isCurrent) {
        this.semesterId = semesterId;
        this.season = season;
        this.year = Year.of(year);
        this.isCurrent = isCurrent;
        logger.debug("Building course list");
        courseList = buildCourseList(semesterPage);
        dayList = new ArrayList<>();
        
        logger.debug("Getting schedule page.");
        HtmlPage schedulePage = ((HtmlElement) semesterPage.getFirstByXPath("//*[@id=\"aspnetForm\"]/div[3]/a")).click();
    }
    
    private List<OmniCourse> buildCourseList(HtmlPage semesterPage) {
        List<OmniCourse> courses = new ArrayList<>();
        List<HtmlTableRow> rows = semesterPage.getByXPath(
                "//*[@id=\"tblContenuSSO\"]/table/tbody/tr/td/table/tbody/tr/td/center/table/tbody/tr/td/table[2]/tbody/tr")
                                              .stream()
                                              .map(c -> (HtmlTableRow) c)
                                              .collect(Collectors.toList());
        for (HtmlTableRow courseRow : rows) {
            if (courseRow.getCells().size() < 4 || courseRow.getByXPath("./td/font/b/span").size() > 1)
                continue;
            courses.add(new OmniCourseImpl(this, courseRow));
            logger.debug("Course constructed successfully.");
        }
        
        return courses;
    }
    
    @Override
    public List<OmniCourse> getCourseList() {
        return courseList;
    }
    
    @Override
    public List<OmniWeekDay> getDays() {
        return dayList;
    }
    
    @JsonProperty("classes")
    @Override
    public List<List<OmniClass>> getClassList() {
        return getDays()
                .stream()
                .map(OmniWeekDay::getClassEventList)
                .collect(Collectors.toList());
    }
    
    @Override
    public int getSemesterId() {
        return semesterId;
    }
    
    @Override
    public String getSeason() {
        return season;
    }
    
    @Override
    public int getYear() {
        return year.getValue();
    }
    
    @Override
    public boolean isCurrent() {
        return isCurrent;
    }
}
