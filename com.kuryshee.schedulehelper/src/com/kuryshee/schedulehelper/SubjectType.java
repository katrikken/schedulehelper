package com.kuryshee.schedulehelper;

/**
 * This enum is for constants defining whether time slot contains lecture or practice.
 * @author Ekaterina Kurysheva
 */
public enum SubjectType {
    LECTURE, PRACTICE;
    
    /**
     * This method overrides inherited toString().
     * @return Czech name for the constant.
     */
    @Override
    public String toString() {
        switch(this) {
            case LECTURE: return "Přednáška";
            case PRACTICE: return "Cvičení";           
            default: throw new IllegalArgumentException();
        }
    }
}
