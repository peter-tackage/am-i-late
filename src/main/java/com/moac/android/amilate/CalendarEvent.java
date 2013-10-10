package com.moac.android.amilate;

import android.text.format.Time;

public class CalendarEvent {

    private static int IMPORTANT = 0;
    private static int PARTY = 1;

    private String what;
    private String where;
    private long when;
    private int type;

    public CalendarEvent() {
        // defaut, all events are important
        type = IMPORTANT;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public void setWhen(long when) {
        this.when = when;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public void setType(int type) {
        this.type = type;
    }

    // We will only take the events with all the what, when, where in consideration for now
    // Later version, we can prompt the user for more details
        /*public boolean isComplete() {
            if (what == null || when == null || where == null ) return false;
			return true;				
		}*/

    public String getWhat() {
        return what;
    }

    public String getWhere() {
        return where;
    }

    public long getWhen() {
        return when;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return what;
    }

    public String getEventDetails() {
        Time t = new Time();
        t.set(when);
        String msg = "What: " + what + "\nWhere: " + where + "\nWhen: " + t.format3339(false) + "\nType: " + type;
        return msg;
    }

    //TODO proper checks
    public boolean isComplete() {
        if (what == null || where == null) return false;
        else return true;
    }
}
 