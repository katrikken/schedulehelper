package com.kuryshee.schedulehelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class downloads data from SIS and parses it into @ScheduleBlock class instances. 
 * @author Ekaterina Kurysheva
 */
public class HTMLDownloader {
    private final String url = "https://is.cuni.cz/studium/rozvrhng/roz_predmet_macro.php?";
    String fac = "fak=";
    String year = "skr=";
    String sem = "sem=";
    String subject = "predmet=";
    String amp = "&";
    
    //Zero-bazed, table columns contain:
    //1. X or P
    //2. subject name
    //3. teacher name
    //4. Day + Time //optional
    //5. Room //optional
    //6. Length
    //7. lang
    // This part may change over time as SIS pages evolves.
    
    int subNameTD = 2;
    int teacherTD = 3;
    int dayTimeTD = 4;
    int roomTD = 5;
    int lengthTD = 6;
    int langTD = 7;
    
    /**
     * This method downloads HTML of page with schedule data based on parameters
     * @param facPar faculty code
     * @param yearPar school year
     * @param semPar semester number    
     * @param subjPar subject code
     * @return list of @ScheduleBlock instances bounded to time slots, when the subject is taught.
     * @throws IOException when downloading of HTML fails.
     */
    public List <ScheduleBlock> Download(String facPar, String yearPar, String semPar, String subjPar) throws IOException {
        String fullurl = url + fac + facPar + amp +
                year + yearPar.trim() + amp +
                sem + semPar + amp +
                subject + subjPar.trim();
        
        Document document = Jsoup.connect(fullurl).get();
        
        Elements elements = document.getElementsByAttributeValue("class", "row1");
        elements.addAll(document.getElementsByAttributeValue("class", "row2"));
        
        List <ScheduleBlock> blocks = new ArrayList<>();
        
        for(Element e: elements){ 

            String subjectName;
            SubjectType type;
            if(e.child(subNameTD).child(0).children().isEmpty()){
                subjectName = e.child(subNameTD).child(0).text();
                type = SubjectType.PRACTICE;
            }
            else{
                subjectName = e.child(subNameTD).child(0).child(0).text();
                type = SubjectType.LECTURE;
            }      
            String teacher = e.child(teacherTD).text().trim();

            String dayNtime = "";
            dayNtime += e.child(dayTimeTD).text();
            String day = "";
            String time = "";
            if(!dayNtime.isEmpty()){
                String[] dt = dayNtime.split(" ");
                day = dt[0].trim();
                time = dt[1].trim();
            }

            String room = "";
            room += e.child(roomTD).text();
            
            String length = e.child(lengthTD).text().trim();
            String language = e.child(langTD).text().trim();
        
            blocks.add(new ScheduleBlock(subjectName, teacher, day, time, room, language, type, length));
        }
        
        return blocks;
    }  
}
