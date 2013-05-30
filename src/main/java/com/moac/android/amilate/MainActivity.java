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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

    private Calendar calendar = null;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final long FAILURE = -1;

    private boolean monitoring = false;

    TextView teaText;
    ImageView teaImage;

    TextView pumpText;
    ImageView pumpImage;

    LocationManager locationManager = null;
    LocationProvider locationProvider = null;

    private final LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // A new location update is received. Do something useful with it.
            // In this case,
            // we're sending the update to a handler which then updates the UI
            // with the new
            // location.
            // Message.obtain(mHandler,
            // UPDATE_LATLNG,
            // location.getLatitude() + ", " +
            // location.getLongitude()).sendToTarget();

            Log.i(TAG, "Received location update: " + location);

            // Can't query without a last known location.
            //amilateButton.setEnabled(location != null);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }
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
                if(isChecked) {
                    // The toggle is enabled
                    teaText.setVisibility(View.INVISIBLE);
                    teaImage.setVisibility(View.INVISIBLE);

                    pumpText.setVisibility(View.VISIBLE);
                    pumpImage.setVisibility(View.VISIBLE);

                    startWatch();
                } else {
                    // The toggle is disabled
                    teaText.setVisibility(View.VISIBLE);
                    teaImage.setVisibility(View.VISIBLE);

                    pumpText.setVisibility(View.INVISIBLE);
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

        calendar = new Calendar(getContentResolver());
    }

	/*public void onToggleClicked(View view) {
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {
	    	 // The toggle is enabled
        	teaText.setVisibility(View.INVISIBLE);
    		teaImage.setVisibility(View.INVISIBLE);

    		pumpText.setVisibility(View.VISIBLE);
    		pumpImage.setVisibility(View.VISIBLE);
    		
    		startWatch();
    		
	    } else {	    	
            // The toggle is disabled
        	teaText.setVisibility(View.VISIBLE);
    		teaImage.setVisibility(View.VISIBLE);	    		

    		pumpText.setVisibility(View.INVISIBLE);
    		pumpImage.setVisibility(View.INVISIBLE);
    		
    		stopWatch();
	    }
	}*/

    public void startWatch() {
        if(!monitoring) {
            Intent i = new Intent(this, NotificationService.class);
            startService(i);
            monitoring = true;
        }
    }

    public void stopWatch() {
        if(monitoring) {
            stopService(new Intent(this, NotificationService.class));
            monitoring = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
          10000, // 10-second interval.
          10, // 10 meters.
          listener);

        // This verification should be done during onStart() because the system
        // calls
        // this method when the user returns to the activity, which ensures the
        // desired
        // location provider is enabled each time the activity resumes from the
        // stopped state.
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		/*final boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!gpsEnabled) {
			Toast.makeText(this, "No GPS Enabled!", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "GPS is enabled! Good one!!!",
					Toast.LENGTH_SHORT).show();

		}*/

    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(listener);
    }
}
