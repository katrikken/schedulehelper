package com.kuryshee.schedulehelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is a specific container for the @MyButton class instances.
 * @author Ekaterina Kurysheva
 */
public class MyButtonList {      
    private List<MyButton> Monday;
    private List<MyButton> Tuesday;
    private List<MyButton> Wednesday;
    private List<MyButton> Thursday;
    private List<MyButton> Friday;
    private List<MyButton> Unknown;

    /**
     * Getter for the Monday attribute.
     * @return list containing buttons associated with @ScheduleBlock instances from Monday time slots.
     */
    public List<MyButton> getMonday() {
        return Monday;
    }

    /**
     * Getter for the Tuesday attribute.
     * @return list containing buttons associated with @ScheduleBlock instances from Tuesday time slots.
     */
    public List<MyButton> getTuesday() {
        return Tuesday;
    }

    /**
     * Getter for the Wednesday attribute.
     * @return list containing buttons associated with @ScheduleBlock instances from Wednesday time slots.
     */
    public List<MyButton> getWednesday() {
        return Wednesday;
    }

    /**
     * Getter for the Thursday attribute.
     * @return list containing buttons associated with @ScheduleBlock instances from Thursday time slots.
     */
    public List<MyButton> getThursday() {
        return Thursday;
    }

    /**
     * Getter for the Friday attribute.
     * @return list containing buttons associated with @ScheduleBlock instances from Friday time slots.
     */
    public List<MyButton> getFriday() {
        return Friday;
    }
    
    /**
     * Getter for the attribute containing buttons associated with @ScheduleBlock instances with no specified time.
     * @return list of buttons.
     */
    public List<MyButton> getUnknown() {
        return Unknown;
    }
    
    /**
     * This method returns all buttons from attributes, which are bounded to some day of the week.
     * @return list of buttons.
     */
    public List<MyButton> getAllButtons() {
        List<MyButton> allBlocks = new ArrayList<>();
        allBlocks.addAll(Monday);
        allBlocks.addAll(Tuesday);
        allBlocks.addAll(Wednesday);
        allBlocks.addAll(Thursday);
        allBlocks.addAll(Friday);
        return allBlocks;
    }

    /**
     * Constructor.
     */
    public MyButtonList(){
        Monday = new ArrayList<>();
        Tuesday = new ArrayList<>();
        Wednesday = new ArrayList<>();
        Thursday = new ArrayList<>();
        Friday = new ArrayList<>();
        Unknown = new ArrayList<>();
    }
    
    /**
     * This method adds new buttons to the container.
     * It sorts buttons by day of the week the button belongs to and by the time the associated lecture begins.
     * @param blocks is a list of instances of MyButton class.
     */
    public void Add(List<MyButton> blocks){
        for (MyButton button : blocks){
            switch (button.block.day){
                case MON: Monday.add(button);
                            break;
                case TUE: Tuesday.add(button);
                            break;
                case WED: Wednesday.add(button);
                            break;
                case THU: Thursday.add(button);
                            break;  
                case FRI: Friday.add(button);
                            break;
                case Unknown: Unknown.add(button);
                            break;
                default: Logger.getLogger("MyButton container").log(Level.WARNING, "Unknown day: {0}", button.block.day);
                    break;
            }
        }
        Sort(Monday);
        Sort(Tuesday);
        Sort(Wednesday);
        Sort(Thursday);
        Sort(Friday);
    }   
    
    /**
     * This method empties the container.
     */
    public void empty(){
        Monday = new ArrayList<>();
        Tuesday = new ArrayList<>();
        Wednesday = new ArrayList<>();
        Thursday = new ArrayList<>();
        Friday = new ArrayList<>();
        Unknown = new ArrayList<>();
    }
    
    /**
     * This method sorts the buttons by the time, when the associated lecture begins. 
     * This method assumes all lectures in the list are at the same day.
     * @param blocks is a list of buttons to be sorted.
     */
    private void Sort(List<MyButton> blocks){
        Collections.sort(blocks, (MyButton o1, MyButton o2) -> o1.block.time.compareTo(o2.block.time));
    }
}
