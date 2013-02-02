package com.moac.android.amilate;

import java.text.Format;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.text.format.DateFormat;
import android.text.format.Time;

public class Calendar {
		
	public static final String[] COLS = new String[] { CalendarContract.Events.TITLE, CalendarContract.Events.EVENT_LOCATION, CalendarContract.Events.DTSTART};
	
	private ArrayList<CalendarEvent> allEvents;

	private Cursor mCursor = null;
	
	public Calendar(ContentResolver contentResolver) {
		allEvents = new ArrayList<CalendarEvent>();
		initAllEvents(contentResolver);
	}
	
	public void initAllEvents(ContentResolver contentResolver) {
    	mCursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, Calendar.COLS, null, null, null);
        mCursor.moveToFirst(); 
        allEvents = query(mCursor); 
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
	    String[] selectionArgs = new String[] { dtStart, dtEnd };
	    
		Cursor nextHourCursor;
		nextHourCursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, Calendar.COLS, selection, selectionArgs, null);
		nextHourCursor.moveToFirst();        
        return query(nextHourCursor);  
	}
	
	public ArrayList<CalendarEvent> query(Cursor mCursor) {
		
		ArrayList<CalendarEvent> results = new ArrayList<CalendarEvent> ();
		
		mCursor.moveToFirst();
        while (mCursor.isAfterLast() == false) {
            try {

            	CalendarEvent calendarEvent = new CalendarEvent();
            	calendarEvent.setWhat(mCursor.getString(0));
            	calendarEvent.setWhere(mCursor.getString(1));
            	calendarEvent.setWhen(mCursor.getLong(2));
            	calendarEvent.printMe();
            	results.add(calendarEvent);
            	
            } catch (Exception e) {
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
