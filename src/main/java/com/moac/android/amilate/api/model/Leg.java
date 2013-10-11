package com.moac.android.amilate.api.model;

/**
 * See https://developers.google.com/maps/documentation/directions/
 */
public class Leg {

    Distance distance;
    Duration duration;

    public Distance getDistance() { return distance; }
    public Duration getDuration() { return duration; }
}
