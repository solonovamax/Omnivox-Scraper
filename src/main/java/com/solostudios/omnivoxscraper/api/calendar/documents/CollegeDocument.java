package com.solostudios.omnivoxscraper.api.calendar.documents;

import java.time.LocalDate;


/**
 * A document given to a student by the college.
 * This document is a downloadable document that is never null.
 */
public interface CollegeDocument extends OmniDocument {
    LocalDate getExpirationDate();
    
    boolean isPriority();
}
