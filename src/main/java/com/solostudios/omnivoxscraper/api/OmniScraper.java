package com.solostudios.omnivoxscraper.api;

import com.solostudios.omnivoxscraper.api.calendar.OmniCalendar;
import com.solostudios.omnivoxscraper.api.calendar.documents.DocumentManager;


/**
 * stuff
 */
public interface OmniScraper {
    String getSubdomain();
    
//    WebClient getWebClient();
    
    OmniCalendar getOmniCalendar();
    
    DocumentManager getOmniDocument();
    
    void shutdown();
}
