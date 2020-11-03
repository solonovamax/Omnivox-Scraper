package com.solostudios.omnivoxscraper.api.calendar.documents;

import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * Manages all your documenst.
 * <p>
 * TODO: add more methods for this.
 *
 * @author solonovamax
 */
public interface DocumentManager {
    /**
     * TODO: documentation.
     *
     * @return
     */
    @NotNull
    List<OmniDocument> getDocuments();
}
