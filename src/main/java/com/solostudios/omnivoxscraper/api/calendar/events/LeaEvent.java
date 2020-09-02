package com.solostudios.omnivoxscraper.api.calendar.events;


public interface LeaEvent extends GenericEvent {
    LeaEventType getEventType();
    
    OmniClass getOmniClass();
    
    enum LeaEventType {
        DOCUMENT_TO_READ("Document To Read"),
        ASSESSMENT("Assessment");
        
        private final String name;
        
        LeaEventType(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
}
