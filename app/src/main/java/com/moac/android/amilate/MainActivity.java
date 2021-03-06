package com.moac.android.amilate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.*;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean monitoring = false;

    private TextView teaText;
    private ImageView teaImage;

    private TextView pumpText;
    private ImageView pumpImage;

    private LocationManager locationManager = null;
    private LocationProvider locationProvider = null;

    private final LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "Received location update: " + location);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }
        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        teaText = (TextView) findViewById(R.id.teaText);
        teaImage = (ImageView) findViewById(R.id.tea);
        teaText.setVisibility(View.INVISIBLE);
        teaImage.setVisibility(View.INVISIBLE);

        pumpText = (TextView) findViewById(R.id.pumpText);
        pumpImage = (ImageView) findViewById(R.id.pump);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new LinearInterpolator());
                fadeOut.setDuration(200);

                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new LinearInterpolator());
                fadeIn.setDuration(200);

                if (isChecked) {

                    // The toggle is enabled
                    teaText.setAnimation(fadeOut);
                    teaText.setVisibility(View.INVISIBLE);
                    teaImage.setAnimation(fadeOut);
                    teaImage.setVisibility(View.INVISIBLE);

                    pumpText.setAnimation(fadeIn);
                    pumpText.setVisibility(View.VISIBLE);
                    pumpImage.setAnimation(fadeIn);
                    pumpImage.setVisibility(View.VISIBLE);

                    startWatch();
                } else {
                    // The toggle is disabled
                    teaText.setAnimation(fadeIn);
                    teaText.setVisibility(View.VISIBLE);
                    teaImage.setAnimation(fadeIn);
                    teaImage.setVisibility(View.VISIBLE);

                    pumpText.setAnimation(fadeOut);
                    pumpText.setVisibility(View.INVISIBLE);
                    pumpImage.setAnimation(fadeOut);
                    pumpImage.setVisibility(View.INVISIBLE);

                    stopWatch();
                }
            }
        });

        toggle.setChecked(true);

        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        locationProvider = locationManager
                .getProvider(LocationManager.GPS_PROVIDER);
    }

    public void startWatch() {
        if (!monitoring) {
            Intent i = new Intent(this, NotificationService.class);
            startService(i);
            monitoring = true;
        }
    }

    public void stopWatch() {
        if (monitoring) {
            stopService(new Intent(this, NotificationService.class));
            monitoring = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                10000, // 10-second interval.
                10, // 10 meters resolution.
                listener);

        /**
         * This verification should be done during onStart() because the system
         * call this method when the user returns to the activity, which ensures the
         * desired location provider is enabled each time the activity resumes from the
         * stop state.
         */
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Toast.makeText(this, "Location access not enabled; how will you know if you're late?", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        locationManager.removeUpdates(listener);
        super.onStop();
    }
}
