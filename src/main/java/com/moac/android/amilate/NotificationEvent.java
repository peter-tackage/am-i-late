package com.moac.android.amilate;

public class NotificationEvent {

    private CalendarEvent calendarEvent;
    private long travelTime;

    public NotificationEvent(CalendarEvent calendarEvent, long travelTime) {
        this.calendarEvent = calendarEvent;
        this.travelTime = travelTime;
    }

    public CalendarEvent getCalendarEvent() {
        return calendarEvent;
    }

    public long getTravelTime() {
        return travelTime;
    }
}
