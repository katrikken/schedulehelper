
package com.kuryshee.schedulehelper;

import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is a structure for the time slot occupied by the lecture.
 * @author Ekaterina Kurysheva.
 */
public class ScheduleBlock { 
    String subject;
    String teacher;
    Day day;
    LocalTime time;
    String room;
    String language;
    SubjectType type; 
    int length;
    String additional = "";
    
    /**
     * Constructor.
     * @param subject is a subject name,
     * @param teacher contains information about all teachers, which teach the subject,
     * @param day is a day when the lecture is taught, may be the empty string,
     * @param time is a time, when the lecture begins, may be the empty string,
     * @param room contains information about where the lecture is taught, may be the empty string,
     * @param language is an indicator of the language the lecture is taught in,
     * @param type indicates whether this time slot is for the lecture or a practice,
     * @param length is the length of the lecture in minutes.
     */
    public ScheduleBlock(String subject, String teacher, String day, 
            String time, String room, String language, 
            SubjectType type, String length){
        this.subject = subject;
        this.teacher = teacher;
        this.day = Day.getDay(day);
        try
        {
            if(!time.isEmpty()){
                if(time.length() == 4){ time = "0" + time; }
                this.time = LocalTime.parse(time);
            }
            else{
                this.time = LocalTime.MIDNIGHT;
            }
        }
        catch(java.time.format.DateTimeParseException ex){
            Logger.getLogger("ScheduleBlock").log(Level.WARNING, "Invalid time of: {0} {1}", new Object[]{subject, time});
            this.time = LocalTime.MIDNIGHT;
        }
        
        this.room = room;
        this.language = language;
        this.type = type;
        try{
            if(length.contains(" ")){
                this.length = Integer.parseInt(length.substring(0, length.indexOf(" "))); 
                this.additional = length.substring(length.indexOf(" ") + 1);
            }
            else{
                this.length = Integer.parseInt(length);   
            }
        }
        catch(Exception e){
            this.length = 0;
            Logger.getLogger("ScheduleBlock").log(Level.WARNING, "{0} length parsing error {1}", new Object[]{subject, e.getMessage()});
        }
    }
    
    /**
     * This method returns short information about this instance for representing on the related @MyButton instance.
     * @return String
     */
    public String getShortInfo(){
        String space = " ";
        String nl = "<br>";
        return "<html>" + subject + space + language + nl +
                teacher + space + additional + nl + "</html>";
    }
    
    /**
     * This method returns formatted information about this instance of a @SchedulBlock.
     * @return String.
     */
    public String getFullInfo(){
        String space = " ";
        String nl = "\n";
        return subject + space + type.toString() + nl + 
                room + space + teacher + nl +
                day.toString() + space + time.toString() + space + additional + nl +
                language + nl;            
    }
    
    /**
     * This method checks whether the other @ScheduleBlock instance is of the same type as this object.
     * @param other
     * @return true if the other ScheduleBlock instance is for the same subject and is of the same SubjectType.
     */
    public Boolean isSameType(ScheduleBlock other){
        return( this.subject.equals(other.subject) && this.type.equals(other.type));
    }
    
    /**
     * This method checks whether the other @ScheduleBlock instance has the same time slot as this object. 
     * @param other
     * @return true if the other @ScheduleBlock instance has the same day and time attributes.
     */
    public Boolean isAtSameTime(ScheduleBlock other){
        return this.day.equals(other.day) && this.time.equals(other.time);
    }
}
