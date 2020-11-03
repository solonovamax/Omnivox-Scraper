package com.solostudios.omnivoxscraper.api.calendar.documents;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
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
    @NotNull
    String getDocumentName();
    
    /**
     * Returns the file name of this document.
     * <p>
     * This is the default name that will be provided when downloading this document.
     * Contains the extension of this document.
     *
     * @return The filename belonging to this document.
     */
    @NotNull
    String getFileName();
    
    /**
     * Returns the url that points to where the document is hosted.
     *
     * @return The url belonging to this document.
     */
    @NotNull
    URL getFileURL();
    
    /**
     * Returns the notes for this document.
     * <p>
     * The notes are most commonly empty strings, but will never be null.
     *
     * @return The notes belonging to this document.
     */
    @NotNull
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
    @NotNull
    LocalDate getPublicationDate();
    
    /**
     * Returns the size in bytes of this document.
     *
     * @return The size in bytes of this document.
     */
    long getSize();
    
    /**
     * Returns the file extension of this document.
     * If there is no extension, it may be an empty string, but never will be {@code null}.
     *
     * @return The file extension of this document.
     */
    @NotNull
    String getFileExtension();
    
    /**
     * An enum representing the filetype of this document.
     * The most common file types are in the enum, but {@link FileType#OTHER} is used as a default.
     *
     * @return The {@link FileType} of this document.
     *
     * @see FileType
     */
    @NotNull
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
        DOC("Microsoft Word 97-2003 Document", "doc"),
        /**
         * Standard Microsoft Word XML document.
         * Most common document, along with {@link FileType#PDF}.
         */
        DOCX("Microsoft Word XML Document", "docx"),
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
    
        private static final Map<String, FileType> typeMap         = new HashMap<>();
        /**
         * The regex used to get the extension from any filename or path name.
         */
        private static final String                EXTENSION_REGEX = ".*\\.(.+)$";
        static {
            for (FileType fileType : values()) {
                typeMap.put(fileType.getFileExtension(), fileType);
            }
        }
        private final String name;
        private final String fileExtension;
    
        FileType(@NonNull String name, @NonNull String fileExtension) {
            this.name = name;
            this.fileExtension = fileExtension;
        }
    
        FileType(@NonNull String name) {
            this.name = name;
            this.fileExtension = "";
        }
    
        /**
         * Uses a URL to get the file type, or {@link #OTHER} if it cannot be found.
         * This uses {@link URL#getPath()} to retrive the path for a URL.
         *
         * @param url The URL to get the file type from.
         *
         * @return The filetype of the URL.
         *
         * @see #getFileTypeWithName(String)
         */
        @SneakyThrows(IOException.class)
        public static FileType getFileTypeWithURL(URL url) {
            String   urlExtension = url.getPath().replaceAll(EXTENSION_REGEX, "$1");
            FileType fileType     = getFileTypeWithExtension(urlExtension);
            if (fileType == OTHER) {
                URLConnection conn = url.openConnection();
                //get header by 'key'
                String fileName = conn.getHeaderField("filename");
                fileType = getFileTypeWithName(fileName);
            }
            return fileType;
        }
    
        /**
         * Uses a name of a document to retrieve the known file type for that format, or {@link #OTHER} if it cannot be found.
         * <p>
         * This will parse the file name using the REGEX {@code .*\.(.+)$}.
         * Everything after the last period in the string will be used for the file extension to retrieve the type.
         *
         * @param fileName The file name to be parsed for an extension.
         *
         * @return The type based on the file extension of the name you provided.
         */
        public static FileType getFileTypeWithName(String fileName) {
            return getFileTypeWithExtension((fileName.replaceAll(EXTENSION_REGEX, "$1").equals(fileName) ?
                                             "" :
                                             fileName.replaceAll(EXTENSION_REGEX, "$1")));
        }
    
        /**
         * Returns the file type from an extension, or {@link #OTHER} if it cannot be found.
         *
         * @param fileExtension The extension used to get the file type.
         *
         * @return The file type of the provided extension.
         */
        public static FileType getFileTypeWithExtension(String fileExtension) {
            return typeMap.getOrDefault(fileExtension, OTHER);
        }
    
        /**
         * The never {@code null} extension for a specific file type.
         * <p>
         * {@link String#isEmpty()} may evaluate as {@code true} if this file type has no specific file exntesion
         *
         * @return A String containing the valid extension for this file type.
         */
        @NotNull
        public String getFileExtension() {
            return fileExtension;
        }
    
        /**
         * The name of the document standard.
         *
         * @return The name of the document standard.
         */
        @NotNull
        public String getName() {
            return name;
        }
    }
}
