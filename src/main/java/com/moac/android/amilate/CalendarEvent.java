package com.moac.android.amilate;

import android.text.format.Time;

// Model class
public class CalendarEvent {

    public enum EventKinds {IMPORTANT, PARTY};

    private String mWhat;
    private String mWhere;
    private long mWhen; // ms
    private EventKinds mType;

    public CalendarEvent() {
        // Default, all events are important
        mType = EventKinds.IMPORTANT;
    }

    public void setWhat(String what) { mWhat = what; }
    public void setWhen(long when) { mWhen = when; }
    public void setWhere(String where) { mWhere = where; }
    public void setType(EventKinds type) { mType = type; }
    public String getWhat() { return mWhat; }
    public String getWhere() { return mWhere; }
    public long getWhen() { return mWhen; }
    public EventKinds getType() { return mType; }

    @Override
    public String toString() {
        return mWhat;
    }

    public String getDebugString() {
        Time t = new Time();
        t.set(mWhen);
        return "What: " + mWhat + "\nWhere: " + mWhere + "\nWhen: " + t.format3339(false) + "\nType: " + mType;
    }

    //TODO proper checks
    public boolean isComplete() {
        return !(mWhat == null || mWhere == null);
    }
}
 