/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kuryshee.schedulehelper;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This class sets up the graphical interface. It contains the main method of the program..
 * @author Ekaterina Kurysheva.
 */
public class GUI {
    private static Choice facultyChoice = new Choice();
    private static JTextField yearField = new JTextField();
    private static Choice semesterChoice = new Choice();
    private static JTextArea subjectsArea = new JTextArea();
    private static JButton addButton = new JButton("Add");
    private static JButton deleteButton = new JButton("Start over");
    private static JPanel schedulePanel = new JPanel();
    private static JFrame frame = new JFrame("Schedule Helper");
    
    public static JLabel monLabel = new JLabel(Day.MON.toString());
    public static JLabel tueLabel = new JLabel(Day.TUE.toString());
    public static JLabel wedLabel = new JLabel(Day.WED.toString());
    public static JLabel thuLabel = new JLabel(Day.THU.toString());
    public static JLabel friLabel = new JLabel(Day.FRI.toString());
    
    private static HTMLDownloader downloader = new HTMLDownloader();
    
    private static Map<String, String> faculties = new HashMap<>();
    private static final String FACULTIES_CONFIG = "faculties.txt";
    
    private static MyButtonList mylist = new MyButtonList();   
    private static ArrayList<JLabel> timeLabels = new ArrayList<>();
    private static ArrayList<String> subjectCodes = new ArrayList<>();
    
    public static int buttonHeight = 60;
    public static int space = 5;
    public static int preferredWidth = 1300;
    
    public static int totalMinutes = 750; //Minutes between 7:20 and 19:50
    public static int firstLectureOffset = 7*60 + 20; //First lecture starts at 7:20
    
    /**
     * This method reads information about faculties from a configuration file.
     */
    private static void readFaculties(){
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FACULTIES_CONFIG), "Cp1252"))){
            String line;
            while((line = br.readLine()) != null){
                String[] parts = line.split("\t");
                if(parts.length == 2){
                    faculties.put(parts[1], parts[0]);
                    facultyChoice.add(parts[1]);
                }
                else{
                    Logger.getLogger("Helper").log(Level.CONFIG, "faculties.txt contains invalid data.");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger("Helper").log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * This method writes the information about selected schedule blocks to the provided output writer.
     * @param writer
     * @throws IOException
     */
    private static void writeChosenSchedule(BufferedWriter writer) throws IOException{
        for (MyButton b: mylist.getAllButtons()){
            if (b.isChosen()){
                writer.write(b.block.getFullInfo());
                writer.newLine();
            }
        }
    }
    
    /**
     * This method invokes file choosing dialog for writing the output.
     * @param evt 
     */
    private static void saveActionPerformed(){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {       
            try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(chooser.getSelectedFile()), "UTF-8"))){
               writeChosenSchedule(bw);            
               
               JOptionPane.showMessageDialog(null, "The schedule was successfully saved.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            }
            catch(IOException e){
                Logger.getLogger("File saving").log(Level.WARNING, "Could not write to the file", e);
                JOptionPane.showMessageDialog(null, "Failed to save the schedule to the chosen file!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * This method shows message dialog with short how-to information about the program.
     */
    private static void showHelp(){
        JOptionPane.showMessageDialog(null, "1. Choose the faculty.\n"
                + "2. Type in the year when the school year started in format YYYY.\n"
                + "3. Choose the semester.\n"
                + "4. Enter subject codes separated by semicolon. You can add them all at once or add other subjects later.\n"
                + "5. Press the Add button to load schedule options.\n"
                + "6. Click on the loaded options you want to choose. Their color will turn red.\n"
                + "7. Save the options you chose to the text file through File -> Save.\n",
                            "How-to", JOptionPane.PLAIN_MESSAGE);
    }
    
    /**
     * This method creates menu.
     * @return menu bar.
     */
    private static JMenuBar createMenu() {
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem item = new JMenuItem("Save");
        item.addActionListener((ActionEvent evt) -> {
            saveActionPerformed();
        });
        menu.add(item);
        mb.add(menu);

        menu = new JMenu("Help");
        item = new JMenuItem("Show how-to");
        item.addActionListener((ActionEvent evt) -> {
            showHelp();
        });
        menu.add(item);

        mb.add(menu);

        return mb;
    }
    
    /**
     * This method draws the buttons from one day to the container.
     * @param btns buttons,
     * @param xOffset offset on x axis,
     * @param yOffset offset on y axis, 
     * @return y axis offset where the last button ended.
     */
    private static Dimension setDay(List<MyButton> btns, int xOffset, int yOffset){
        Boolean buttonsLeft = false;
        Boolean stop = false;
        int maxX = xOffset;
        
        while(!stop){
            int lastX = xOffset;
            for (MyButton b : btns){ //buttons are sorted by time
                if(!b.isPlaced() && lastX <= b.getRelativeX() + xOffset){
                    Dimension size = b.getPreferredSize();
                    b.setBounds(b.getRelativeX() + xOffset, yOffset,
                            size.width, size.height);
                
                    lastX = b.getRelativeX() + xOffset + size.width;
                    if(lastX > maxX){
                        maxX = lastX;
                    }
                    b.setPlaced(true);
                }
                else if(!b.isPlaced()){
                    buttonsLeft = true;
                }
            }
            if(buttonsLeft){
                yOffset += (buttonHeight + space); 
                buttonsLeft = false;
            }
            else{
                stop = true;
            }
        }
        
        Dimension d = new Dimension(maxX, yOffset + buttonHeight + space);
        return d;
    }
    
    /**
     * This method draws time labels to the container.
     * @param pane is a container
     * @param leftOffset is an offset on x axis.
     * @return offset on the y axis to paint other elements, so they don't intersect.
     */
    private static int addDayTime(Container pane, int leftOffset){
        ArrayList<LocalTime> time = new ArrayList<>();
        int topOffset = 0;
        
        for(MyButton b : mylist.getAllButtons()){
            if(!time.contains(b.block.time)){
                time.add(b.block.time);
            }
        }
        
        for(LocalTime t: time){
            Boolean exists = false;
            for(JLabel lab: timeLabels){
                if(lab.getText().equals(t.toString())){
                    exists = true;
                    break;
                }
            }
            if(!exists){
                int offset = (int)(((pane.getPreferredSize().getWidth() - leftOffset) / (double)totalMinutes) * 
                        (t.getHour() * 60 + t.getMinute() - firstLectureOffset));
                JLabel l = new JLabel(t.toString());
                timeLabels.add(l);
                pane.add(l);
                Dimension size = l.getPreferredSize();
                l.setBounds(offset + leftOffset, 0, size.width, size.height);
                if(size.height > topOffset){
                    topOffset = size.height;
                }
            } 
        }
        
        return topOffset;
    }
    
    /**
     * Resets all buttons to prepare them for repainting.
     */
    private static void setButtonsPlaced(){
        for(MyButton b : mylist.getAllButtons()){
            b.setPlaced(false);
        }
    }
    
    /**
     * This method sets buttons color associated with the same subject to grey.
     * @param button which may cause a collision.
     */
    private static void checkCollisions(MyButton button){
        for(MyButton b : mylist.getAllButtons()){
            if(!button.equals(b)){ 
                if(b.block.isSameType(button.block)){
                    b.setCannotBeChosen();
                }
            }
        }
    }
    
    /**
     * This method sets button colors according to the schedule creating logic.
     */
    private static void setButtonsColor(){
        for(MyButton b: mylist.getAllButtons()){
            if(!b.isChosen()){
                b.resetColor();
            }
        }
        
        for (MyButton b1: mylist.getAllButtons()){
            if(b1.isChosen()){
                for(MyButton b2 : mylist.getAllButtons()){
                    if(!b1.equals(b2)){ 
                        if (b2.block.isSameType(b1.block)){                   
                            b2.setCannotBeChosen();
                        }
                        else if (!b2.isChosen() && b2.block.isAtSameTime(b1.block)){
                            b2.setCannotBeChosen();
                        }
                    }
                }
            }
        }          
    }
    
    /**
     * This method draws the buttons in the container visualizing the schedule. 
     * @param pane container.
     */
    private static void addSchedule(Container pane){
        pane.setLayout(null);
        pane.setBackground(Color.WHITE);
        for (MyButton b : mylist.getAllButtons()){
            pane.add(b);
        }
        setButtonsPlaced();
        Insets insets = pane.getInsets();
        
        int leftOffset = (int) monLabel.getPreferredSize().getWidth() + space;
        int topOffset = addDayTime(pane, leftOffset);
        
        Dimension d;
        int maxX = pane.getPreferredSize().width;

        monLabel.setBounds(insets.left, topOffset, monLabel.getPreferredSize().width, monLabel.getPreferredSize().height);      
        d = setDay(mylist.getMonday(), leftOffset, topOffset);
        topOffset = d.height; 
        if(d.width > maxX){
            maxX = d.width;
        }
        
        tueLabel.setBounds(insets.left, topOffset, tueLabel.getPreferredSize().width, tueLabel.getPreferredSize().height);  
        d = setDay(mylist.getTuesday(), leftOffset, topOffset);
        topOffset = d.height; 
        if(d.width > maxX){
            maxX = d.width;
        }
        
        wedLabel.setBounds(insets.left, topOffset, wedLabel.getPreferredSize().width, wedLabel.getPreferredSize().height);  
        d = setDay(mylist.getWednesday(), leftOffset, topOffset);
        topOffset = d.height; 
        if(d.width > maxX){
            maxX = d.width;
        }
        
        thuLabel.setBounds(insets.left, topOffset, thuLabel.getPreferredSize().width, thuLabel.getPreferredSize().height);  
        d = setDay(mylist.getThursday(), leftOffset, topOffset);
        topOffset = d.height; 
        if(d.width > maxX){
            maxX = d.width;
        }
        
        friLabel.setBounds(insets.left, topOffset, friLabel.getPreferredSize().width, friLabel.getPreferredSize().height);  
        d = setDay(mylist.getFriday(), leftOffset, topOffset);
        topOffset = d.height; 
        if(d.width > maxX){
            maxX = d.width;
        }
        
        pane.setPreferredSize(new Dimension(maxX, topOffset));
        frame.pack();
    }
    
    /**
     * This method reacts to checking or un-checking buttons associated with time slots.
     * It calls for the methods to change the coloring of the buttons in the schedule.
     * @param evt 
     */
    public static void scheduleButtonListener(ActionEvent evt){
        MyButton button = (MyButton) evt.getSource();
        button.switchChosen();
        if(button.isChosen()){
            checkCollisions(button);
        }
        setButtonsColor();
    }
    
    /**
     * This method deletes all the downloaded data and enables the user to restart choosing the schedule.
     * @param evt 
     */
    public static void deleteButtonActionPerformed(ActionEvent evt){
        yearField.setEditable(true);
        semesterChoice.setEnabled(true);
        mylist.empty();
        subjectCodes = new ArrayList<>();
        timeLabels = new ArrayList<>();
        schedulePanel.removeAll();
        schedulePanel.repaint();
        schedulePanel.add(monLabel);
        schedulePanel.add(tueLabel);
        schedulePanel.add(wedLabel);
        schedulePanel.add(thuLabel);
        schedulePanel.add(friLabel);      
    }
    
    /**
     * This method validates user input for the year of the scheduled lectures.
     * @return true if the year is acceptable.
     */
    private static Boolean checkYear(){
         try{
            int year = Integer.parseInt(yearField.getText().trim());
            int now = LocalDate.now().getYear();
           
            if(LocalDate.now().getMonth().compareTo(Month.SEPTEMBER) <= 0){
                now--;
            }
            //SIS stores the schedule for last 3 years only 
            if(year <= now && year >= now - 3 ){
                return true;
            }
        }
        catch(Exception e){
            Logger.getLogger("Year checker").log(Level.WARNING, "Exception while resolving the year ", e);
        }
        return false;
    }
    
    /**
     * This method tries to download contents from SIS if user provided correct data for building the query.
     * If data were downloaded, the method stores them and creates instances of @MyButton class associated with new data.
     * @param evt.
     */
    private static void addButtonActionPerformed(ActionEvent evt) {                                                 
        if(yearField.getText().trim().isEmpty() || !checkYear()){
            JOptionPane.showMessageDialog(null, "Type in correct year!",
                        "Error", JOptionPane.ERROR_MESSAGE);
        }
        else{    
            String[] subjects = subjectsArea.getText().trim().split(";");
            
            String year = yearField.getText().trim();
            //Student cannot choose subjects from different year or semester in one session.
            yearField.setEditable(false); 
            
            String semester = semesterChoice.getSelectedItem();
            semesterChoice.setEnabled(false);
            
            ArrayList<ScheduleBlock> newblocks = new ArrayList<>();
            
            for(String s: subjects){
                if(!subjectCodes.contains(s)){
                    try{
                        List<ScheduleBlock> blocks = downloader.Download(
                                faculties.get(facultyChoice.getSelectedItem()),year, semester, s);
                        newblocks.addAll(blocks);
                        subjectCodes.add(s);     
                    }
                    catch(IOException e){
                        JOptionPane.showMessageDialog(null, "Error while downloading subject " + s +" occured!\nCheck your Internet connection and provided data.",
                            "Error", JOptionPane.ERROR_MESSAGE);

                        Logger.getLogger("Helper").log(Level.SEVERE, null, e);
                    }
                }
            }
            
            if(newblocks.isEmpty()){
                JOptionPane.showMessageDialog(null, "No data were downloaded. Check your Internet connection and provided data.",
                            "Info", JOptionPane.INFORMATION_MESSAGE);
            }
            else{
                ArrayList<MyButton> buttons = new ArrayList<>();
                for(ScheduleBlock b : newblocks){
                    MyButton button = new MyButton(b);
                    button.addActionListener((ActionEvent ev) -> {
                        scheduleButtonListener(ev);
                    });
                    button.setToolTipText(b.getShortInfo());
                    button.setVerticalAlignment(SwingConstants.TOP);
                    buttons.add(button);
                }
                mylist.Add(buttons);
                addSchedule(schedulePanel);
            }          
        }    
    }  
    
    /**
     * This method adds components responsible for the downloading settings.
     * @param pane is a container for the components.
     */
    public static void addComponentsToPane(Container pane) {
        pane.setLayout(new FlowLayout());
        pane.add(new JLabel("Faculty: "));
        pane.add(facultyChoice);
        pane.add(new JLabel("Year: "));
        yearField.setPreferredSize(new Dimension(50, 25));
        yearField.setToolTipText("YYYY format");
        int now = LocalDate.now().getYear();
        if(LocalDate.now().getMonth().compareTo(Month.SEPTEMBER) <= 0){
            now--;
        }
        yearField.setText("" + now);
        pane.add(yearField);
        
        pane.add(new JLabel("Semester: "));
        semesterChoice.add("1"); semesterChoice.add("2");
        semesterChoice.setPreferredSize(new Dimension(50, 25));
        pane.add(semesterChoice);
        
        pane.add(new JLabel("Subjects: "));

        subjectsArea.setPreferredSize(new Dimension(400, 50));  
        subjectsArea.setToolTipText("Enter subject's codes separated by semicolon ';'");
        subjectsArea.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(subjectsArea, 
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JPanel bigPanel = new JPanel();      
        bigPanel.setLayout(new BorderLayout());
        bigPanel.setPreferredSize(subjectsArea.getPreferredSize());
        bigPanel.add(scroll, BorderLayout.CENTER);
        pane.add(bigPanel);
        
        addButton.addActionListener((ActionEvent evt) -> {
            addButtonActionPerformed(evt);
        });
        pane.add(addButton);
        
        deleteButton.setToolTipText("Deletes all added subjects");
        deleteButton.addActionListener((ActionEvent evt) -> {
            deleteButtonActionPerformed(evt);
        });
        pane.add(deleteButton);
        
        pane.setComponentOrientation(
                ComponentOrientation.LEFT_TO_RIGHT);
    }

    /**
     * This method sets the basic graphical interface.
     */
    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel parametersPanel = new JPanel();
        parametersPanel.setPreferredSize(new Dimension(preferredWidth, 50));
        
        addComponentsToPane(parametersPanel);
        
        frame.add(parametersPanel, BorderLayout.PAGE_START);
            
        schedulePanel.add(monLabel);
        schedulePanel.add(tueLabel);
        schedulePanel.add(wedLabel);
        schedulePanel.add(thuLabel);
        schedulePanel.add(friLabel);
        schedulePanel.setPreferredSize(new Dimension(preferredWidth, 500));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(schedulePanel);
        JPanel bigPanel = new JPanel();      
        bigPanel.setLayout(new BorderLayout());
        bigPanel.setPreferredSize(schedulePanel.getPreferredSize());
        bigPanel.add(scrollPane, BorderLayout.CENTER);
        frame.add(bigPanel, BorderLayout.CENTER);

        frame.setJMenuBar(createMenu());
    
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        frame.setVisible(true);
        frame.setResizable(false);
    }
     
    /**
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        readFaculties();
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }
}
