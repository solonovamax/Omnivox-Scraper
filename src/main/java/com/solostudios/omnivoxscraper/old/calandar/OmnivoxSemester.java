package com.solostudios.omnivoxscraper.old.calandar;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OmnivoxSemester {
    private final int                  semesterId;
    private final String               season;
    private final int                  year;
    private final boolean              isCurrent;
    private final List<OmnivoxCourse>  courseList;
    private final List<OmnivoxDay>     days;
    private final List<OmnivoxClass>[] classList;
    private final HtmlPage             page;
    
    public OmnivoxSemester(int semesterId, String season, int year, boolean isCurrent, HtmlPage semesterPage)
            throws CalendarAccessException {
        if (semesterPage.getByXPath("//table[@class=\"tbAvertissement\"]").size() > 0)
            throw new CalendarAccessException("Could not find any data for this course!");
        this.semesterId = semesterId;
        this.season = season;
        this.year = year;
        this.isCurrent = isCurrent;
        courseList = new ArrayList<>();
        days = new ArrayList<>();
        //noinspection unchecked
        classList = new ArrayList[7];
        this.page = semesterPage;
        initCalendar(semesterPage);
    }
    
    private void initCalendar(HtmlPage semesterPage) {
        HtmlTable courseTable = semesterPage.getFirstByXPath(
                "//*[@id=\"tblContenuSSO\"]/table/tbody/tr/td/table/tbody/tr/td/center/table/tbody/tr/td/table[2]");
        HtmlTable scheduleTable = semesterPage.getFirstByXPath("//*[@id=\"pageHoraire\"]/tbody/tr[2]/td[2]/table");
        
        genCourseList(courseTable);
        genDayList();
        genClassList(scheduleTable);
    
        System.out.println(this);
    }
    
    private void genDayList() {
        for (int i = 0; i < 7; i++) {
            days.add(new OmnivoxDay(this, DayOfWeek.of(i + 1)));
            classList[i] = new ArrayList<>();
        }
    }
    
    /**
     * Formats dates from the provided [First 3 letters of month]-[days] to the ISO-8601 standard.
     *
     * @param inputDate
     *         The portal formatted date.
     *
     * @return The ISO-8601 formatted date.
     *
     * @see java.time.format.DateTimeFormatter#ISO_LOCAL_DATE
     */
    private static String dateFormat(String inputDate) {
        return LocalDate.now().getYear() + "-" + inputDate
                .replace("Jan", "01")
                .replace("Feb", "02")
                .replace("Mar", "03")
                .replace("Apr", "04")
                .replace("May", "05")
                .replace("Jun", "06")
                .replace("Jul", "07")
                .replace("Aug", "08")
                .replace("Sep", "09")
                .replace("Oct", "10")
                .replace("Nov", "11")
                .replace("Dec", "12");
    }
    
    private void genCourseList(HtmlTable courseTable) {
        for (HtmlTableRow courseRow : courseTable.getRows()) {
            if (courseRow.getCells().size() < 4 || courseRow.getByXPath("./td/font/b/span").size() > 1)
                continue;
            // Use String#strip() and not String#trim(), because String#strip() is unicode-aware.
            String    courseDates   = ((HtmlElement) courseRow.getFirstByXPath("./td[1]/font/span")).getTextContent().strip();
            String    courseNumber  = ((HtmlElement) courseRow.getFirstByXPath("./td[2]/font/span")).getTextContent().strip();
            String    courseSection = ((HtmlElement) courseRow.getFirstByXPath("./td[3]/font/span")).getTextContent().strip();
            String    courseTitle   = ((HtmlElement) courseRow.getFirstByXPath("./td[4]/font/span")).getTextContent().strip();
            String    courseTeacher = ((HtmlElement) courseRow.getFirstByXPath("./td[5]/font/span")).getTextContent().strip();
            HtmlTable hoursTable    = courseRow.getFirstByXPath("./td[6]/table");
            String    theoryHours   = ((HtmlElement) hoursTable.getFirstByXPath("./tbody/tr[1]/td[1]/font/span")).getTextContent().strip();
            String    labHours      = ((HtmlElement) hoursTable.getFirstByXPath("./tbody/tr[1]/td[2]/font/span")).getTextContent().strip();
            String    workHours     = ((HtmlElement) hoursTable.getFirstByXPath("./tbody/tr[1]/td[3]/font/span")).getTextContent().strip();
            String    courseExtraInfo;
            if (courseRow.getByXPath("./td[7]/a").isEmpty())
                courseExtraInfo = ((HtmlElement) courseRow.getFirstByXPath("./td[7]/a")).getAttribute("title").strip();
            else
                courseExtraInfo = "";
            
            // Why is there a non breaking space in this...
            courseDates = courseDates.replaceAll("\\u00A0", "");
            courseNumber = courseNumber.replaceAll("\\u00A0", "");
            courseSection = courseSection.replaceAll("\\u00A0", "");
            courseTitle = courseTitle.replaceAll("\\u00A0", "");
            courseTeacher = courseTeacher.replaceAll("\\u00A0", "");
            theoryHours = theoryHours.replaceAll("\\u00A0", "");
            labHours = labHours.replaceAll("\\u00A0", "");
            workHours = workHours.replaceAll("\\u00A0", "");
            
            Pattern datePattern = Pattern.compile("from ([A-Z][a-z]{2}-\\d\\d) to ([A-Z][a-z]{2}-\\d\\d)");
            Matcher dateMatcher = datePattern.matcher(courseDates);
            if (!dateMatcher.matches())
                throw new CalendarParsingException("Dates did not match regex. The date string was: " + courseDates);
            
            this.courseList.add(new OmnivoxCourse(courseTitle, courseNumber, Integer.parseInt(courseSection), courseTeacher, this,
                                                  LocalDate.parse(dateFormat(dateMatcher.group(1))),
                                                  LocalDate.parse(dateFormat(dateMatcher.group(2))),
                                                  Duration.ofHours(Long.parseLong(theoryHours)), Duration.ofHours(Long.parseLong(labHours)),
                                                  Duration.ofHours(Long.parseLong(workHours)), courseExtraInfo));
        }
    }
    
    private void genClassList(HtmlTable scheduleTable) {
        //noinspection unchecked
        Pair<String, Integer>[] remainingCourseLength = new Pair[7];
        for (int i = 0; i < remainingCourseLength.length; i++) {
            remainingCourseLength[i] = new ImmutablePair<>("", 0);
        }
        
        System.out.println("| Time     | Monday               | Tuesday              | Wednesday            | Thursday             " +
                           "| Friday               | Saturday             | Sunday               |");
        System.out.print("|----------|----------------------|----------------------|----------------------|----------------------" +
                         "|----------------------|----------------------|----------------------|\n| ");
        for (int i = 0; i < scheduleTable.getRows().size(); i++) {
            HtmlTableRow scheduleRow = scheduleTable.getRow(i);
            String       classTime;
            try {
                System.out.print((classTime = scheduleRow.getFirstByXPath("./td[1]/font/text()").toString()) + "    | ");
            } catch (NullPointerException e) {
                continue;
            }
            
            for (int j = 0, k = 1; j < 7; j++) {
                if (remainingCourseLength[j].getValue() > 0) {
                    remainingCourseLength[j].setValue(remainingCourseLength[j].getValue() - 1);
                    System.out.print(remainingCourseLength[j].getKey() + ".| ");
                    continue;
                }
                
                String textElement = scheduleRow.getCell(k)
                                                .getFirstByXPath("./font/text()[1]")
                                                .toString()
                                                .replaceAll("\\s{2,}L.*",
                                                            "") // sometimes there seems to be these random characters at the
                                                // end of the line. The regex "\s*L\n" didn\"t deal with it, so I used "\s{2,}L.*" instead.
                                                .strip();
                if (!textElement.isBlank()) {
                    System.out.print(textElement + " | ");
                    remainingCourseLength[j] = new MutablePair<>(textElement, scheduleRow.getCell(k).getRowSpan() - 1);
                    boolean isOnline = scheduleRow.getCell(k).getFirstByXPath("./font/font/b/i/text()") != null;
                    OmnivoxClass
                            omnivoxClass = new OmnivoxClass(Duration.ofMinutes(scheduleRow.getCell(k).getRowSpan() * 30),
                                                            LocalTime.parse(classTime),
                                                            courseList.stream()
                                                                      .filter((course) -> course.getCourseNumber()
                                                                                                .equals(textElement.replaceFirst(
                                                                                                        "(\\d{3}-[A-Z0-9]{3}-[A-Z0-9" +
                                                                                                        "]{2}) sec\\.\\d{5}",
                                                                                                        "$1")))
                                                                      .findAny()
                                                                      .get(), days.get(j), isOnline);
                    courseList.stream()
                              .filter((course) -> course.getCourseNumber().equals(textElement))
                              .forEach(omnivoxCourse -> omnivoxCourse.addClass(omnivoxClass));
    
                    classList[j].add(omnivoxClass);
                    days.get(j).addClass(omnivoxClass);
                } else {
                    System.out.print("                     | ");
                }
                k++;
            }
    
            System.out.print("\n| ");
        }
    }
    
    public HtmlPage getPage() {
        return page;
    }
    
    public List<OmnivoxCourse> getCourseList() {
        return courseList;
    }
    
    public List<OmnivoxDay> getDays() {
        return days;
    }
    
    public List<OmnivoxClass>[] getClassList() {
        return classList;
    }
    
    public int getSemesterId() {
        return semesterId;
    }
    
    public String getSeason() {
        return season;
    }
    
    public int getYear() {
        return year;
    }
    
    public boolean isCurrent() {
        return isCurrent;
    }
    
    public String getSemesterName() {
        return season + year;
    }
    
    @Override
    public String toString() {
        return toJSONString();
    }
    
    public String toJSONString() {
        return "{" +
               "\"name\":\"OmnivoxSemester\"" +
               ", \"semesterId\":" + semesterId +
               ", \"season\":\"" + season + "\"" +
               ", \"year\":" + year +
               ", \"isCurrent\":" + isCurrent +
               ", \"courseList\":" + courseList +
               ", \"days\":" + days +
               ", \"classList\":" + Arrays.toString(classList) +
               "}";
    }
}
