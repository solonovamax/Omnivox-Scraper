package com.solostudios.omnivoxscraper.api.calendar.documents;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


/**
 * A generic document published by some entity to the student.
 * A document is represents downloadable file hosted on omnivox.ca.
 * <p>
 * A document can have any file format.
 */
public interface OmniDocument {
    /**
     * Returns the name of this document.
     * This is not the same as {@link #getFileName()}.
     *
     * @return The name of the document.
     */
    String getDocumentName();
    
    /**
     * Returns the file name of this document.
     * <p>
     * This is the default name that will be provided when downloading this document.
     * Contains the extension of this document.
     *
     * @return The filename belonging to this document.
     */
    String getFileName();
    
    /**
     * Returns the url that points to where the document is hosted.
     *
     * @return The url belonging to this document.
     */
    URL getFileURL();
    
    /**
     * Returns the notes for this document.
     * <p>
     * The notes are most commonly empty strings, but will never be null.
     *
     * @return The notes belonging to this document.
     */
    String getDocumentNotes();
    
    /**
     * Returns whether or not the student has previously viewed this document.
     * If the value of this is false, then this document will be considered a 'new' document.
     *
     * @return Whether or not the student has already seen this document.
     */
    boolean isViewed();
    
    /**
     * Returns the publication date of this document.
     *
     * @return The date the document was published on.
     */
    LocalDate getPublicationDate();
    
    /**
     * Returns the size in bytes of this document.
     *
     * @return The size in bytes of this document.
     */
    long getSize();
    
    /**
     * Returns the file extension of this document.
     * If there is no extension, it may be an empty string.
     *
     * @return The file extension of this document.
     */
    String getFileExtension();
    
    /**
     * An enum representing the filetype of this document.
     * The most common file types are in the enum, but {@link FileType#OTHER} is used as a default.
     *
     * @return The {@link FileType} of this document.
     *
     * @see FileType
     */
    FileType getFileType();
    
    /**
     * Represents the file type of the document.
     * <p>
     * The most common file types are listed in this enum, but if the file type is unknown, or the document has no extension, then it will
     * default to {@link FileType#OTHER}.
     */
    enum FileType {
        /**
         * Standard Microsoft Word document.
         * Pre-XML formats.
         */
        DOC("Microsoft Word document", "doc"),
        /**
         * Standard Microsoft Word XML document.
         * Most common document, along with {@link FileType#PDF}.
         */
        DOCX("Document XML: Microsoft Office 2007 Document in XML format", "docx"),
        /**
         * Adobe PDF format document.
         * Most common document, along with {@link FileType#DOCX}.
         */
        PDF("Adobe Acrobat Portable Document File.", "pdf"),
        /**
         * MP4 multimedia container format.
         */
        MP4("MPEG-4 Video File", "mp4"),
        /**
         * Generic format used when format is not in list of most common formats.
         * <p>
         * Members of this format are not guaranteed to have a specific file ending,
         * nor are they guaranteed to be in a specific encoding scheme.
         */
        OTHER("Unrecognized Format");
        
        private static final Map<String, FileType> typeMap = new HashMap<>();
        static {
            for (FileType fileType : values()) {
                typeMap.put(fileType.getFileExtension(), fileType);
            }
        }
        private final String name;
        private final String fileExtension;
        
        FileType(String name, String fileExtension) {
            this.name = name;
            this.fileExtension = fileExtension;
        }
        
        FileType(String name) {
            this.name = name;
            this.fileExtension = "";
        }
        
        public static FileType getFileTypeWithExtension(String fileExtension) {
            return typeMap.get(fileExtension);
        }
        
        public static FileType getFileTypeWithName(String fileName) {
            return getFileTypeWithExtension((fileName.replaceAll(".*\\.(.+)$", "$1").equals(fileName) ?
                                             "" :
                                             fileName.replaceAll(".*\\.(.+)$", "$1")));
        }
        
        public static FileType getFileTypeWithURL(URL url) {
            return getFileTypeWithName(url.getPath());
        }
        
        public String getFileExtension() {
            return fileExtension;
        }
        
        public String getName() {
            return name;
        }
    }
}
