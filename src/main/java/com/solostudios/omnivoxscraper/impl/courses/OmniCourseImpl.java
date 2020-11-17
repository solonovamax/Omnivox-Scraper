package com.solostudios.omnivoxscraper.impl.courses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.solostudios.omnivoxscraper.api.calendar.OmniSemester;
import com.solostudios.omnivoxscraper.api.calendar.events.OmniClass;
import com.solostudios.omnivoxscraper.api.courses.OmniCourse;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@ToString
@EqualsAndHashCode
public class OmniCourseImpl implements OmniCourse {
    
    private static final String  DATE_REGEX   = "from\\s(\\w\\w\\w-\\d\\d?)\\sto\\s(\\w\\w\\w-\\d\\d?)";
    private static final Pattern DATE_PATTERN = Pattern.compile(DATE_REGEX, Pattern.MULTILINE);
    private static final String  NBSP         = "\\u00A0";
    
    private final String       className;
    private final String       courseNumber;
    private final int          sectionId;
    private final String       teacherName;
    private final LocalDate    startDate;
    private final LocalDate    endDate;
    private final Duration     theoryHours;
    private final Duration     labHours;
    private final Duration     workHours;
    private final String       extraInfo;
    @JsonIgnore
    private final OmniSemester semester;
    
    @SneakyThrows({ JsonProcessingException.class, IOException.class })
    public OmniCourseImpl(OmniSemester semester, HtmlTableRow courseRow) {
        this.semester = semester;
        XmlMapper    xmlMapper  = new XmlMapper();
        JsonNode     node       = xmlMapper.readTree(courseRow.asXml().getBytes());
        ObjectMapper jsonMapper = new ObjectMapper();
        String       json       = jsonMapper.writeValueAsString(node);
        
        className = ((HtmlElement) courseRow.getFirstByXPath("./td[4]/font/span")).getTextContent().replaceAll(NBSP, " ").strip();
        logger.debug("Building course {}.", className);
        
        String dateRange = ((HtmlElement) courseRow.getFirstByXPath("./td[1]/font/span")).getTextContent();
        courseNumber = ((HtmlElement) courseRow.getFirstByXPath("./td[2]/font/span")).getTextContent().replaceAll(NBSP, " ").strip();
        String courseSection = ((HtmlElement) courseRow.getFirstByXPath("./td[3]/font/span")).getTextContent();
        sectionId = Integer.parseInt(courseSection.replaceAll(NBSP, " ").strip());
        teacherName = ((HtmlElement) courseRow.getFirstByXPath("./td[5]/font/span")).getTextContent();
        
        HtmlTable hoursTable  = courseRow.getFirstByXPath("./td[6]/table");
        String    theoryHours = ((HtmlElement) hoursTable.getFirstByXPath("./tbody/tr[1]/td[1]/font/span")).getTextContent();
        String    labHours    = ((HtmlElement) hoursTable.getFirstByXPath("./tbody/tr[1]/td[2]/font/span")).getTextContent();
        String    workHours   = ((HtmlElement) hoursTable.getFirstByXPath("./tbody/tr[1]/td[3]/font/span")).getTextContent();
        this.theoryHours = Duration.ofHours(Long.parseLong(theoryHours.replaceAll(NBSP, " ").strip()));
        this.labHours = Duration.ofHours(Long.parseLong(labHours.replaceAll(NBSP, " ").strip()));
        this.workHours = Duration.ofHours(Long.parseLong(workHours.replaceAll(NBSP, " ").strip()));
        
        extraInfo = courseRow.getByXPath("./td[7]/a").isEmpty()
                    ? ""
                    : ((HtmlElement) courseRow.getFirstByXPath("./td[7]/a"))
                            .getAttribute("title")
                            .replaceAll(NBSP, " ")
                            .strip();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLL-dd");
        
        logger.debug("Parsing course dates");
        Matcher matcher = DATE_PATTERN.matcher(dateRange.replaceAll(NBSP, " ").strip());
        if (!matcher.find())
            throw new RuntimeException("no"); //TODO replace with better exception
        
        startDate = MonthDay.parse(matcher.group(1), formatter).atYear(LocalDate.now().getYear());
        endDate = MonthDay.parse(matcher.group(2), formatter).atYear(LocalDate.now().getYear());
    }
    
    @NotNull
    @Override
    public String getClassName() {
        return className;
    }
    
    @NotNull
    @Override
    public String getCourseNumber() {
        return courseNumber;
    }
    
    @Override
    public int getSection() {
        return sectionId;
    }
    
    @NotNull
    @Override
    public String getTeacherName() {
        return teacherName;
    }
    
    @NotNull
    @Override
    public LocalDate getStartDate() {
        return startDate;
    }
    
    @NotNull
    @Override
    public LocalDate getEndDate() {
        return endDate;
    }
    
    @NotNull
    @Override
    public Period getCourseLength() {
        return Period.between(getStartDate(), getEndDate());
    }
    
    @NotNull
    @Override
    public Duration getTheoryHours() {
        return theoryHours;
    }
    
    @NotNull
    @Override
    public Duration getLabHours() {
        return labHours;
    }
    
    @Override
    @NotNull
    public Duration getWorkHours() {
        return workHours;
    }
    
    @NotNull
    @Override
    public String getExtraInfo() {
        return extraInfo;
    }
    
    @NotNull
    @Override
    public List<OmniClass> getClassListAtDay(@NotNull LocalDate date) {
        return null;
    }
    
    @NotNull
    @Override
    public OmniSemester getSemester() {
        return semester;
    }
}
