package com.moac.android.amilate.api.model;

import java.util.List;

/**
 * See https://developers.google.com/maps/documentation/directions/
 */
public class Route {
    private List<Leg> legs;
    public List<Leg> getLegs() { return legs; }
}
