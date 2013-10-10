package com.moac.android.amilate;

// Model class
public class NotificationEvent {

    private CalendarEvent mCalendarEvent;
    private long mTravelTime;

    public NotificationEvent(CalendarEvent _calendarEvent, long _travelTime) {
        mCalendarEvent = _calendarEvent;
        mTravelTime = _travelTime;
    }

    public CalendarEvent getCalendarEvent() { return mCalendarEvent; }
    public long getTravelTime() { return mTravelTime; }
}
