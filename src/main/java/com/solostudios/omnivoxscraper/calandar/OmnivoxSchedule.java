package com.solostudios.omnivoxscraper.calandar;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.solostudios.omnivoxscraper.OmniScraper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OmnivoxSchedule {
    private final OmniScraper           scraper;
    private final List<OmnivoxSemester> semesters;
    
    public OmnivoxSchedule(OmniScraper scraper, HtmlPage coursePage) throws IOException {
        this.scraper = scraper;
        this.semesters = new ArrayList<>();
        for (HtmlOption semesterElement : ((HtmlSelect) coursePage.getElementByName("AnSession")).getOptions()) {
            OmnivoxSemester semester = getSemester(semesterElement, coursePage);
            if (semester != null)
                semesters.add(semester);
        }
    }
    
    public List<OmnivoxSemester> getSemesters() {
        return semesters;
    }
    
    private OmnivoxSemester getSemester(HtmlOption scheduleOption, HtmlPage coursePage) throws IOException {
        String semesterAction = ((HtmlForm) coursePage.getFirstByXPath(
                "//*[@id=\"tblContenuSSO\"]/table/tbody/tr/td/table/tbody/tr/td/form")).getActionAttribute();
        
        Pattern refPattern = Pattern.compile("Horaire\\.ovx\\?Ref=(\\d*)&C=VAN&L=ANG");
        Matcher refMatcher = refPattern.matcher(semesterAction);
        if (!refMatcher.matches()) {
            throw new IllegalArgumentException(
                    "OmniScraper was served a bad URL! The URL on the Course Schedule page must match the regex " +
                    "\"Horaire\\.ovx\\?Ref=(\\d*)&C=VAN&L=ANG\"");
        }
        String ref = refMatcher.group(1);
        
        URL                 semesterUrl     = new URL("https://" + scraper.getSubdomain() + "-estd.omnivox.ca/estd/hrre/" + "Horaire.ovx");
        WebRequest          semesterRequest = new WebRequest(semesterUrl, HttpMethod.POST);
        List<NameValuePair> semesterParams  = new ArrayList<>();
        
        semesterParams.add(new NameValuePair("AnSession", scheduleOption.getValueAttribute()));
        semesterParams.add(new NameValuePair("Confirm", "Obtain+my+schedule"));
        semesterRequest.setRequestParameters(semesterParams);
        HtmlPage schedulePage = scraper.getWebClient().getPage(semesterRequest);
        
        scheduleOption.getEnclosingSelect().setSelectedAttribute(scheduleOption, true);
        
        try {
            return new OmnivoxSemester(Integer.parseInt(scheduleOption.getValueAttribute()),
                                       scheduleOption.getTextContent().replaceAll("(.*) \\d\\d\\d\\d", "$1").trim(),
                                       Integer.parseInt(scheduleOption.getTextContent().replaceAll(".* (\\d\\d\\d\\d)", "$1").trim()),
                                       scheduleOption.isDefaultSelected(), schedulePage);
        } catch (CalendarAccessException e) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return toJSONString();
    }
    
    public String toJSONString() {
        return "{" +
               "\"name\":\"OmnivoxSchedule\"" +
               ", \"scraper\"=" + scraper +
               ", \"semesters\"=" + semesters +
               "}";
    }
}
