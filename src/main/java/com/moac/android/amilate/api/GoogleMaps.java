package com.moac.android.amilate.api;

import com.moac.android.amilate.api.model.Directions;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * A representation of the Google Map/Directions API
 */
public interface GoogleMaps {

    /**
     * From the Google Directions API
     *
     * See - https://developers.google.com/maps/documentation/directions/#RequestParameters
     *
     * origin - The address or textual latitude/longitude value from
     * which you wish to calculate directions. If you pass an address as
     * a string, the Directions service will geocode the string and
     * convert it to a latitude/longitude coordinate to calculate
     * directions. If you pass coordinates, ensure that no space exists
     * between the latitude and longitude values.
     *
     * destination - The address or textual latitude/longitude value
     * from which you wish to calculate directions. If you pass an
     * address as a string, the Directions service will geocode the
     * string and convert it to a latitude/longitude coordinate to
     * calculate directions. If you pass coordinates, ensure that no
     * space exists between the latitude and longitude values.

     * arrival_time - specifies the desired time of arrival for transit
     * directions as seconds since midnight, January 1, 1970 UTC. One of
     * departure_time or arrival_time must be specified when requesting
     * transit directions.
     *
     * Generally, only one entry in the "routes" array is returned for directions lookups,
     * though the Directions service may return several routes if you pass alternatives=true.
     */
    @GET("/maps/api/directions/json")
    public Directions getDirections(@Query("origin") String _origin,
                                    @Query("destination") String _destination,
                                    @Query("arrival_time") long _arrivalTime,
                                    @Query("sensor") boolean _isFromSensor);
}
