package com.solostudios.omnivoxscraper.old.calandar;

/**
 * Exception thrown when there is an error parsing the calendar.
 */
public class CalendarParsingException extends RuntimeException {
    /**
     * Constructs a new calendar parsing exception with {@code null} as the message.
     */
    public CalendarParsingException() {
        super();
    }
    
    /**
     * Constructs a new calendar parsing exception with the specified message.
     *
     * @param message The detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public CalendarParsingException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new calendar parsing exception with a specified cause and message.
     *
     * @param message The detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   The cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public CalendarParsingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new calendar parsing exception with a cause.
     *
     * @param cause The cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A {@code null} value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     */
    public CalendarParsingException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Constructs a new calendar parsing exception with a message, a cause, suppression enabled or disabled,
     * and writable stack trace enabled or disabled.
     *
     * @param message            The detail message.
     * @param cause              The cause.  (A {@code null} value is permitted,
     *                           and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression  Whether or not suppression is enabled
     *                           or disabled
     * @param writableStackTrace Whether or not the stack trace should
     *                           be writable
     */
    public CalendarParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
