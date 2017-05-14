package com.kuryshee.schedulehelper;

/**
 * This enum structure helps to convert Czech day abbreviations to a reusable format.
 * @author Ekaterina Kurysheva
 */
public enum Day {
    MON, TUE, WED, THU, FRI, Unknown;

    /**
     * This method overrides toString().
     * @return string corresponding to Czech abbreviation of day names. 
     */
    @Override
    public String toString() {
        switch(this) {
            case MON: return "Po";
            case TUE: return "Út";
            case WED: return "St";
            case THU: return "Čt";
            case FRI: return "Pá";
            case Unknown: return "Nerozvrženo";
            default: throw new IllegalArgumentException();
        }
    }
    
    /**
     * This method converts day abbreviation to a constant based on parameter.
     * @param day
     * @return Day constant.
     */
    public static Day getDay(String day){
        switch(day) {
            case "Po": return MON;
            case "Út": return TUE;
            case "St": return WED;
            case "Čt": return THU;
            case "Pá": return FRI;
            case "": return Unknown;
            default: throw new IllegalArgumentException();
        }
    }
}
