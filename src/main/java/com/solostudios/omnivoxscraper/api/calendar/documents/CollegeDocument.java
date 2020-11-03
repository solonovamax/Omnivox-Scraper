package com.solostudios.omnivoxscraper.api.calendar.documents;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;


/**
 * A document given to a student by the college.
 * This document is a downloadable document that is never null.
 *
 * @author solonovamax
 */
public interface CollegeDocument extends OmniDocument {
    /**
     * Get the date at which the document expires.
     *
     * @return The date at which the document expires.
     */
    @NotNull
    LocalDate getExpirationDate();
    
    /**
     * Whether or not this is a priority document or not.
     * <p>
     * This is detected by whether or not it has a stupid star beside it.
     * (I think that's what it was. I'll have to wait for the page to show up again,
     * so I can double check that shit)
     *
     * @return Whether this is a priority document or not.
     */
    boolean isPriority();
}
