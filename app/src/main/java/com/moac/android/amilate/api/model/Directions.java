package com.moac.android.amilate.api.model;

import java.util.List;

/**
 * See https://developers.google.com/maps/documentation/directions/
 */
public class Directions {

    private static final String OK = "OK";

    private String status; // OK
    private List<Route> routes;

    public boolean isOk() {
        // A poor man's custom deserializer
        return status.equalsIgnoreCase(OK);
    }

    public List<Route> getRoutes() { return routes; }
}
