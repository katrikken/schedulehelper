package com.kuryshee.schedulehelper;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JButton;

/**
 * @author Ekaterina Kurysheva
 */
public class MyButton extends JButton{
    public ScheduleBlock block;
    
    private boolean chosen = false;
    private boolean placed = false;
    private int relativeX;
    private Color myBlue;
    
    /**
     * Getter for the attribute chosen.
     * @return attribute value.
     */
    public boolean isChosen() {
        return chosen;
    }

    /**
     * This method sets attribute of a class.  
     * @param chosen when true, the button has red background color and is assumed to be added to the final schedule.
     */
    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    /**
     * Getter for the relativeX attribute.
     * @return offset on the x axis based on lecture start time.
     */
    public int getRelativeX() {
        return relativeX;
    }

    /**
     * Setter for the relativeX attribute.
     * @param width gives the width of the content pane.
     */
    private void setRelativeX(int width) {
        double minutesOffset = (block.time.getHour() * 60 + block.time.getMinute()) - GUI.firstLectureOffset ;
        this.relativeX = (int)( (double)width/(double)GUI.totalMinutes * minutesOffset);
    }

    /**
     * Getter for the attribute placed.
     * @return true if the button is painted on the content pane for the schedule.
     */
    public boolean isPlaced() {
        return placed;
    }

    /**
     * Setter for the attribute placed.
     * @param placed 
     */
    public void setPlaced(boolean placed) {
        this.placed = placed;
    }
    
    /**
     * Constructor.
     * @param block ScheduleBlock bounded to the button.
     */
    public MyButton(ScheduleBlock block){
        this.block = block;
        if (this.block.type == SubjectType.PRACTICE){
            myBlue = new Color(130, 210, 250);
        }
        else{
            myBlue = new Color(0, 140, 230);
        }
        
        int width = GUI.preferredWidth - GUI.space - GUI.monLabel.getPreferredSize().width;

        setRelativeX(width);
        setAppearance(width);
    }
    
    /**
     * This method switches the button color depending on whether it is chosen to the schedule or not.
     */
    public void switchChosen(){
        if(chosen){
            setChosen(false);
            this.setBackground(myBlue);
        }
        else{
            setChosen(true);
            this.setBackground(Color.red);
        }
    }
    
    /**
     * This method changes button background color to grey to indicate, that a collision with other subjects appeared.
     */
    public void setCannotBeChosen(){
        setChosen(false);
        this.setBackground(Color.GRAY);
    }
    
    /**
     * This method resets color of the button to the standard.
     */
    public void resetColor(){
        if (!chosen){
            this.setBackground(myBlue);
        }
    }
    
    /**
     * This method sets the appearance of the button.
     * It sets the length of the button depending on the length of the lecture bounded to the button and width parameter.
     * It sets the text content of the button and the background color.
     * @param width is the width of a container for the button.
     */
    private void setAppearance(int width){  
        double pixels = (double)width/(double)GUI.totalMinutes * (double)block.length;
        this.setPreferredSize(new Dimension((int)pixels, GUI.buttonHeight));
        this.setText(block.toString());
        this.setBackground(myBlue);
    }
   
}
