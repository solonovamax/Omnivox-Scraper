package com.solostudios.omnivoxscraper.calandar;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;


public class OmnivoxDay {
    private final List<OmnivoxClass> classList;
    private final OmnivoxSemester    semester;
    private final DayOfWeek          day;
    
    public OmnivoxDay(OmnivoxSemester semester, DayOfWeek day) {
        this.classList = new ArrayList<>();
        this.semester = semester;
        this.day = day;
    }
    
    public List<OmnivoxClass> getClassList() {
        return classList;
    }
    
    public OmnivoxSemester getSemester() {
        return semester;
    }
    
    public DayOfWeek getDay() {
        return day;
    }
    
    void addClass(OmnivoxClass omnivoxClass) {
        classList.add(omnivoxClass);
    }
    
    @Override
    public String toString() {
        return toJSONString();
    }
    
    
    public String toJSONString() {
        return "{" +
               "\"name\":\"OmnivoxDay\"" +
               ", \"classList\":" + classList +
               ", \"day\":\"" + day + "\"" +
               "}";
    }
}
