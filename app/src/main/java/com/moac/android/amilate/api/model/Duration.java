package com.moac.android.amilate.api.model;

/**
 * See https://developers.google.com/maps/documentation/directions/
 */
public class Duration {
    private String text;
    private long value;

    public long getValue() { return value; }
    public String getText() { return text; }
}
