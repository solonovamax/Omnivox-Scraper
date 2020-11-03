package com.solostudios.omnivoxscraper.impl.utils.index;

import lombok.Getter;


@SuppressWarnings("unused")
public enum OmnivoxService {
    ACCESS_CENTER("access-centre"),
    ATTENDANCE("attendance"),
    CANCELLED_CLASSES("cancelled-classes"),
    CARPOOLING("carpooling"),
    CLASSIFICATION_TEST("classification-test"),
    COURSE_REGISTRATION("course-registration"),
    FEES("fees"),
    LEA("lea"),
    MESSAGES("messages"),
    OPUS("opus"),
    PERSONAL_FILE("personal-file"),
    PRINTING("printing"),
    PROGRAM_CHANGE("program-change"),
    PROGRESSION_CHART("progression-chart"),
    SCHEDULE("schedule"),
    SECONDHAND_BOOKS("secondhand-books"),
    SURVEY("survey"),
    TAX_FORMS("tax-forms"),
    ;
    
    @Getter
    private final String name;
    
    OmnivoxService(String name) {
        this.name = name;
    }
}
