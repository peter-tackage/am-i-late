package com.moac.android.amilate;

import android.test.AndroidTestCase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moac.android.amilate.api.model.Directions;
import com.moac.android.amilate.api.model.Distance;
import com.moac.android.amilate.api.model.Duration;
import com.moac.android.amilate.api.model.Leg;
import com.moac.android.amilate.api.model.Route;

import java.util.List;

/**
 * Candidate for JUNIT only test
 * Need to suppress warnings that say getContext could be null.
 * The AndroidTestCase already ensures that it isn't as vi test in itself.
 */

@SuppressWarnings("ConstantConditions")
public class JsonDeserialisationTest extends AndroidTestCase {

    /**
     * Simple deserialization of Directions JSON
     *
     * Only one route, leg as per our assumptions in the app.
     */
    public void test_deserializeDirection() {
        String json = TestFileUtils.readTestDataFile(getContext().getClassLoader(), "directions.json");
        Gson gson = new GsonBuilder().create();
        Log.i("directions", " json: " + json);

        Directions directions = gson.fromJson(json, Directions.class);
        assertNotNull(directions);
        assertTrue(directions.isOk());

        List<Route> routes = directions.getRoutes();
        assertNotNull(routes);
        assertEquals(1, routes.size());

        Route route = routes.get(0);
        List<Leg> legs = route.getLegs();
        assertEquals(1, legs.size());

        Leg leg = legs.get(0);
        Distance distance = leg.getDistance();
        Duration duration = leg.getDuration();

        assertNotNull(distance);
        assertEquals("542 km", distance.getText());
        assertEquals(542389, distance.getValue());

        assertNotNull(leg.getDuration());
        assertEquals("5 hours 12 mins", duration.getText());
        assertEquals(18749, duration.getValue());
    }
}
