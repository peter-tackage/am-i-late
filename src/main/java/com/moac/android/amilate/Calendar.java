package com.moac.android.amilate;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.text.format.Time;

import java.util.ArrayList;

public class Calendar {

    public static final String[] CALENDAR_PROJECTION = new String[]{ CalendarContract.Events.TITLE, CalendarContract.Events.EVENT_LOCATION, CalendarContract.Events.DTSTART };

    private ArrayList<CalendarEvent> allEvents;

    private Cursor mCursor = null;

    private ContentResolver contentResolver;

    public Calendar(ContentResolver contentResolver) {
        allEvents = new ArrayList<CalendarEvent>();
        this.contentResolver = contentResolver;
        initAllEvents();
        //initTestData();
    }

    public void initAllEvents() {
        allEvents.clear();

        Time t = new Time();
        t.setToNow();
        String selection = "(" + CalendarContract.Events.DTSTART
          + " >= " + t.toMillis(false) + ")";
        mCursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, Calendar.CALENDAR_PROJECTION, selection, null, null);
        mCursor.moveToFirst();
        allEvents = query(mCursor);
    }

    private void initTestData() {
        // Sunday 4pm
        /*CalendarEvent event1 = new CalendarEvent();
		event1.setWhen(1359907200000l);
		event1.setWhat("Demo time");
		event1.setWhere("Schohausser Allee 36");*/
		
		/* we should always be late for this one
		CalendarEvent event2 = new CalendarEvent();
		event2 .setWhen(1359846000000l);
		event2 .setWhat("Party in Porto");
		event2 .setWhere("Porto Portugal");*/

        // Sunday 5pm
        CalendarEvent event3 = new CalendarEvent();
        event3.setWhen(1359931807l);
        event3.setWhat("Mad Tea Party");
        event3.setWhere("Kreuzberg");

        //allEvents.add(event1);
        //allEvents.add(event2);
        allEvents.add(event3);
    }

    public ArrayList<CalendarEvent> getAllEvents() {
        return allEvents;
    }

    public ArrayList<CalendarEvent> getNextHourEvents(ContentResolver contentResolver) {

        String selection = "((" + CalendarContract.Events.DTSTART
          + " >= ?) AND (" + CalendarContract.Events.DTEND + " <= ?))";

        Time t = new Time();
        t.setToNow();
        String dtStart = Long.toString(t.toMillis(false));
        t.set(60, 60, 1, t.monthDay, t.month, t.year);
        String dtEnd = Long.toString(t.toMillis(false));
        String[] selectionArgs = new String[]{ dtStart, dtEnd };

        Cursor nextHourCursor;
        nextHourCursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, Calendar.CALENDAR_PROJECTION, selection, selectionArgs, null);
        nextHourCursor.moveToFirst();
        return query(nextHourCursor);
    }

    public ArrayList<CalendarEvent> query(Cursor mCursor) {

        ArrayList<CalendarEvent> results = new ArrayList<CalendarEvent>();

        mCursor.moveToFirst();
        while(mCursor.isAfterLast() == false) {
            try {

                CalendarEvent calendarEvent = new CalendarEvent();
                calendarEvent.setWhat(mCursor.getString(0));
                calendarEvent.setWhere(mCursor.getString(1));
                calendarEvent.setWhen(mCursor.getLong(2));
                results.add(calendarEvent);
            } catch(Exception e) {
                //ignore
            }

            mCursor.moveToNext();
        }
        mCursor.close();
        return results;
    }

    public Cursor getCursor() {
        return mCursor;
    }
}
