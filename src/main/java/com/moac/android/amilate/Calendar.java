package com.moac.android.amilate;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.text.format.Time;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Calendar {

    private static final String TAG = Calendar.class.getSimpleName();
    private static final String[] CALENDAR_PROJECTION = new String[]{CalendarContract.Events.TITLE, CalendarContract.Events.EVENT_LOCATION, CalendarContract.Events.DTSTART};

    private List<CalendarEvent> mEvents;

    public Calendar(ContentResolver _contentResolver) {
        mEvents = getEvents(_contentResolver);
    }

    public List<CalendarEvent> getEvents() {
        return mEvents;
    }

    private List<CalendarEvent> getEvents(ContentResolver _resolver) {
        List<CalendarEvent> events;
        Time t = new Time();
        t.setToNow();
        String selection = "(" + CalendarContract.Events.DTSTART
                + " >= " + t.toMillis(false) + ")";
        Cursor mCursor = _resolver.query(CalendarContract.Events.CONTENT_URI, Calendar.CALENDAR_PROJECTION, selection, null, null);
        if(mCursor == null)
            return Collections.emptyList();

        events = query(mCursor);
        Log.i(TAG, "getEvents() - event count:" + events.size());
        return events;
    }

//    private void initTestData() {
//        // Sunday 4pm
//        /*CalendarEvent event1 = new CalendarEvent();
//        event1.setWhen(1359907200000l);
//		event1.setWhat("Demo time");
//		event1.setWhere("Schohausser Allee 36");*/
//
//		/* we should always be late for this one
//        CalendarEvent event2 = new CalendarEvent();
//		event2 .setWhen(1359846000000l);
//		event2 .setWhat("Party in Porto");
//		event2 .setWhere("Porto Portugal");*/
//
//        // Sunday 5pm
//        CalendarEvent event3 = new CalendarEvent();
//        event3.setWhen(1359931807l);
//        event3.setWhat("Mad Tea Party");
//        event3.setWhere("Kreuzberg");
//
//        //mEvents.add(event1);
//        //mEvents.add(event2);
//        mEvents.add(event3);
//    }

    private List<CalendarEvent> getNextHourEvents(ContentResolver _contentResolver) {

        String selection = "((" + CalendarContract.Events.DTSTART
                + " >= ?) AND (" + CalendarContract.Events.DTEND + " <= ?))";

        Time t = new Time();
        t.setToNow();
        String dtStart = Long.toString(t.toMillis(false));
        t.set(60, 60, 1, t.monthDay, t.month, t.year);
        String dtEnd = Long.toString(t.toMillis(false));
        String[] selectionArgs = new String[]{dtStart, dtEnd};

        Cursor nextHourCursor;
        nextHourCursor = _contentResolver.query(CalendarContract.Events.CONTENT_URI, Calendar.CALENDAR_PROJECTION, selection, selectionArgs, null);
        if(nextHourCursor == null)
            return Collections.emptyList();
        return query(nextHourCursor);
    }

    private static List<CalendarEvent> query(Cursor _cursor) {
        List<CalendarEvent> results = new ArrayList<CalendarEvent>();

        _cursor.moveToFirst();
        while (!_cursor.isAfterLast()) {
            try {
                CalendarEvent calendarEvent = new CalendarEvent();
                calendarEvent.setWhat(_cursor.getString(0));
                calendarEvent.setWhere(_cursor.getString(1));
                calendarEvent.setWhen(_cursor.getLong(2));
                results.add(calendarEvent);
            } catch (Exception e) {
                Log.e(TAG, "query() - Exception query Calendar: " + e.getLocalizedMessage());
            }
            _cursor.moveToNext();
        }
        _cursor.close();
        return results;
    }
}
