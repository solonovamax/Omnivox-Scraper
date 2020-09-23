package com.solostudios.omnivoxscraper.api;

import com.gargoylesoftware.htmlunit.WebClient;
import com.solostudios.omnivoxscraper.api.calendar.OmniCalendar;
import com.solostudios.omnivoxscraper.api.calendar.documents.DocumentManager;


public interface OmniScraper {
    String getSubdomain();
    
    WebClient getWebClient();
    
    OmniCalendar getOmniCalendar();
    
    DocumentManager getOmniDocument();
    
    void shutdown();
}
