package com.solostudios.omnivoxscraper.calandar;

public class CalendarParsingException extends RuntimeException {
    public CalendarParsingException() {
    }
    
    public CalendarParsingException(String message) {
        super(message);
    }
    
    public CalendarParsingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public CalendarParsingException(Throwable cause) {
        super(cause);
    }
    
    public CalendarParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
