package com.solostudios.omnivoxscraper.api.calendar.documents;

import com.solostudios.omnivoxscraper.api.courses.OmniCourse;


/**
 * A document given by a specific teacher to a specific class.
 * This document is a downloadable document that is never null.
 */
public interface CourseDocument extends OmniDocument {
    /**
     * Returns the course that this document belongs to.
     *
     * @return The course this document belongs to.
     */
    OmniCourse getCourse();
    
    /**
     * Returns the string value that represents the section this document is listed under in the documents table.
     *
     * @return The string title of the document section.
     *
     * @implNote This information can be found on the {@code https://www-[first 3 letters of url]-ovx.omnivox.ca/cvir/ddle/ListeDocuments
     * .aspx?*} website at the {@code //*[@id="categorie[number]"]/thead/tr/td[2]/a/text()} XPath. (This URL could be incorrect. Make sure
     * to compare it with other schools.)
     */
    String getDocumentSection();
}
