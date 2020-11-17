package com.solostudios.omnivoxscraper.impl.calendar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.solostudios.omnivoxscraper.api.calendar.OmniCalendar;
import com.solostudios.omnivoxscraper.api.calendar.OmniDay;
import com.solostudios.omnivoxscraper.api.calendar.OmniSemester;
import com.solostudios.omnivoxscraper.api.calendar.events.OmniClass;
import com.solostudios.omnivoxscraper.impl.OmniScraperImpl;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.model.component.VEvent;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author solonovamax
 */
@Slf4j
@ToString
@EqualsAndHashCode
@SuppressWarnings("unused")
public class OmniCalendarImpl implements OmniCalendar {
    @ToString.Exclude
    private final OmniScraperImpl        scraper;
    private final List<OmniSemesterImpl> semesters;
    
    public OmniCalendarImpl(OmniScraperImpl scraper) {
        this.scraper = scraper;
        this.semesters = new ArrayList<>();
    }
    
    @SneakyThrows(JsonProcessingException.class)
    public void loadPage(HtmlPage loadCalendarPage) {
        try {
            List<HtmlOption> semesterOptions = ((HtmlSelect) loadCalendarPage.getElementByName("AnSession")).getOptions();
            logger.info("Detected {} semesters, constructing now.", semesterOptions.size());
            for (HtmlOption semesterElement : semesterOptions) {
                OmniSemesterImpl semester = getSemester(semesterElement, loadCalendarPage);
                semesters.add(semester);
            }
            
            logger.debug("Computed semesters: {}", new ObjectMapper().registerModule(new JavaTimeModule())
//                                                                     .writerWithDefaultPrettyPrinter()
                                                                     .writeValueAsString(semesters));
        } catch (ElementNotFoundException e) {
            logger.error("Could not find the AnSession element. " +
                         "This most likely means that you have some unread documents from the college. " +
                         "Please read them before continuing. " +
                         "If this is not the case, then I fucked up something, so please open a support ticket.", e);
            System.exit(1);
        }
    }
    
    @SneakyThrows(IOException.class)
    private OmniSemesterImpl getSemester(HtmlOption semesterElement, HtmlPage loadCalendarPage) {
//        String semesterAction = ((HtmlForm) coursePage.getFirstByXPath(
//                "//*[@id=\"tblContenuSSO\"]/table/tbody/tr/td/table/tbody/tr/td/form")).getActionAttribute();

//        Pattern refPattern = Pattern.compile("Horaire\\.ovx\\?Ref=(\\d*)&C=VAN&L=(?:ANG|FRA)");
//        Matcher refMatcher = refPattern.matcher(semesterAction);
//        if (!refMatcher.matches()) {
//            throw new IllegalArgumentException(
//                    "OmniScraper was served a bad URL! The URL on the Course Schedule page must match the regex " +
//                    "\"Horaire\\.ovx\\?Ref=(\\d*)&C=VAN&L=ANG\"");
//        }
//        String ref = refMatcher.group(1);
        
        URL                 semesterUrl     = new URL("https://" + scraper.getSubdomain() + "-estd.omnivox.ca/estd/hrre/" + "Horaire.ovx");
        WebRequest          semesterRequest = new WebRequest(semesterUrl, HttpMethod.POST);
        List<NameValuePair> semesterParams  = new ArrayList<>();
        
        semesterParams.add(new NameValuePair("AnSession", semesterElement.getValueAttribute()));
        semesterParams.add(new NameValuePair("Confirm", "Obtain+my+schedule"));
        semesterRequest.setRequestParameters(semesterParams);
        HtmlPage schedulePage = scraper.getWebClient().getPage(semesterRequest);
        
        semesterElement.getEnclosingSelect().setSelectedAttribute(semesterElement, true);
        
        String season     = semesterElement.getTextContent().replaceAll("(.*) \\d\\d\\d\\d", "$1").trim();
        int    semesterId = Integer.parseInt(semesterElement.getValueAttribute());
        int    year       = Integer.parseInt(semesterElement.getTextContent().replaceAll(".* (\\d\\d\\d\\d)", "$1").trim());
        
        logger.debug("Constructing semester with ID {}.", semesterId);
        return new OmniSemesterImpl(schedulePage, semesterId, season, year, semesterElement.isDefaultSelected());
    }
    
    @Override
    public List<OmniSemester> getAvailableSemesters() {
        return semesters.stream()
                        .filter(s -> s.getClassList().isEmpty())
                        .map(s -> (OmniSemester) s)
                        .collect(Collectors.toList());
    }
    
    @Override
    public Optional<OmniSemester> getCurrentSemester() {
        return semesters.stream() //there is only one where isCurrent is true.
                        .filter(OmniSemester::isCurrent)
                        .map(s -> (OmniSemester) s)
                        .findAny();
    }
    
    @Override
    public List<OmniClass> getClassesToday() {
        return null;
    }
    
    @Override
    public OmniDay getDay(LocalDate date) {
        return null;
    }
    
    @Override
    public List<VEvent> iCalendarEvents() {
        return null;
    }
}
