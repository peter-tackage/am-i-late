package com.moac.android.amilate.api.model;

/**
 * See https://developers.google.com/maps/documentation/directions/
 */
public class Leg {

    private Distance distance;
    private Duration duration;

    public Distance getDistance() { return distance; }
    public Duration getDuration() { return duration; }
}
