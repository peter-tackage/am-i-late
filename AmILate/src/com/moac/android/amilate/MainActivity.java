package com.moac.android.amilate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private static final long FAILURE = -1;

	EditText whereEditText;
	EditText whenDateEditText;
	EditText whenTimeEditText;
	TextView answerText;

	Button amilateButton;

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
			amilateButton.setEnabled(location != null);

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

		amilateButton = (Button) findViewById(R.id.amilateButton);
		amilateButton.setOnClickListener(new AmilateOnClickListener());

		whereEditText = (EditText) findViewById(R.id.whereEditText);
		whenDateEditText = (EditText) findViewById(R.id.whenDateEditText);
		whenTimeEditText = (EditText) findViewById(R.id.whenTimeEditText);
		answerText = (TextView) findViewById(R.id.resultTextView);

		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		locationProvider = locationManager
				.getProvider(LocationManager.GPS_PROVIDER);

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
		final boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!gpsEnabled) {
			Toast.makeText(this, "No GPS Enabled!", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "GPS is enabled! Good one!!!",
					Toast.LENGTH_SHORT).show();

		}
		amilateButton.setEnabled(gpsEnabled);
	}

	@Override
	protected void onStop() {
		super.onStop();
		locationManager.removeUpdates(listener);
	}

	/**
	 * 
	 * Separate callback for the main query button.
	 * 
	 * @author peter
	 * 
	 */
	private class AmilateOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// Get the location value
			String whereString = whereEditText.getText().toString();
			String whenDateString = whenDateEditText.getText().toString();
			String whenTimeString = whenTimeEditText.getText().toString();
			// Now parse them

			QueryForDirectionsTask task;
			try {
				task = new QueryForDirectionsTask(whereString, whenDateString,
						whenTimeString);
				task.execute();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG, "Some problem parsing dates");
			}
		}

	}

	private class QueryForDirectionsTask extends AsyncTask<Void, Void, Long> {

		public String where;
		public long when;

		public QueryForDirectionsTask(String where, String whenDate,
				String whenTime) throws ParseException {
			this.where = where;

			java.text.DateFormat dFormat = DateFormat
					.getDateFormat(MainActivity.this);
			java.text.DateFormat tFormat = DateFormat
					.getTimeFormat(MainActivity.this);

			// long d = dFormat.parse(whenDate).getTime();
			long d = 1359842400000l;
			// long t = tFormat.parse(whenTime).getTime();
			long t = 0;

			// Time is SECONDS
			this.when = (d + t) / 1000;

		}

		public QueryForDirectionsTask(String where, long when) {
			this.where = where;
			this.when = when / 1000;
		}

		@Override
		protected Long doInBackground(Void... params) {
			Log.i(TAG, "Running background directions query with params");

			// Let's ask the location provider for our current location
			Location lastKnown = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			Log.i(TAG, "Last known location was: " + lastKnown);

			// origin Ñ The address or textual latitude/longitude value from
			// which you wish to calculate directions. If you pass an address as
			// a string, the Directions service will geocode the string and
			// convert it to a latitude/longitude coordinate to calculate
			// directions. If you pass coordinates, ensure that no space exists
			// between the latitude and longitude values.
			// destination Ñ The address or textual latitude/longitude value
			// from which you wish to calculate directions. If you pass an
			// address as a string, the Directions service will geocode the
			// string and convert it to a latitude/longitude coordinate to
			// calculate directions. If you pass coordinates, ensure that no
			// space exists between the latitude and longitude values.
			// arrival_time specifies the desired time of arrival for transit
			// directions as seconds since midnight, January 1, 1970 UTC. One of
			// departure_time or arrival_time must be specified when requesting
			// transit directions.

			// HTTP get
			// Check response code
			// Parse JSON
			// Check directions status
			// Extract time.
			// Compare to current time
			// If event time - travel current > current time .. then YOU'RE
			// LATE!!!
			AndroidHttpClient client = AndroidHttpClient
					.newInstance("com.moac.android.amilate");

			try {

				// URI uri = new
				// URI("http://maps.googleapis.com/maps/api/directions/json");

				List<NameValuePair> qparams = new ArrayList<NameValuePair>();
				qparams.add(new BasicNameValuePair("origin", lastKnown
						.getLatitude() + "," + lastKnown.getLongitude()));
				qparams.add(new BasicNameValuePair("destination", this.where));
				qparams.add(new BasicNameValuePair("arrival_time", Long
						.toString(this.when)));
				qparams.add(new BasicNameValuePair("sensor", Boolean.TRUE
						.toString()));

				URI uri = URIUtils.createURI("http", "maps.googleapis.com", -1,
						"/maps/api/directions/json",
						URLEncodedUtils.format(qparams, "UTF-8"), null);
				HttpGet httpget = new HttpGet(uri);

				Log.i(TAG, "About to make query: "
						+ httpget.getRequestLine().getUri());

				HttpResponse response = client.execute(httpget);

				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					Log.i(TAG, "Successful HTTP Response Google");

					// Now check the JSON value
					HttpEntity entity = response.getEntity();
					String jsonString = EntityUtils.toString(entity);
					JSONObject json = new JSONObject(jsonString);
					// Log.i(TAG, json.toString());

					String status = json.getString("status");
					if (status.equals("OK")) {
						// Let's extract the values of interest.
						Log.i(TAG, "Got good status from Google: " + status);

						long totalValue = 0;
						JSONArray routes = json.getJSONArray("routes");
						// TODO Assumptions about length etc here.
						JSONArray legs = routes.getJSONObject(0).getJSONArray(
								"legs");
						for (int i = 0; i < legs.length(); i++) {
							// Get the time - DURATION IN SECONDS!!
							JSONObject duration = legs.getJSONObject(i)
									.getJSONObject("duration");
							long value = duration.getLong("value");
							totalValue += value;
						}

						Log.i(TAG, "Calculated total duration (sec): "
								+ totalValue);
						return Long.valueOf(totalValue);

					} else {
						Log.e(TAG, "Got bad status from Google: " + status);
					}

				} else {
					Log.e(TAG, "Got bad status code from Google: " + statusCode);
				}

			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				client.close();
			}

			return Long.valueOf(FAILURE);

		}

		public void onPostExecute(Long travelTime) {
			
			if (!travelTime.equals(FAILURE)) {
				Time t = new Time();
				t.setToNow();
				long currentSec = t.toMillis(false) / 1000;

				Log.i(TAG, "Current time in seconds is: " + currentSec);

				// This is SECONDS from epoch
				long eventTime = 1359842400l;

				
				// Now we work out if we're ok.
				// Event Time - travel time
				long leaveTime = eventTime - travelTime.longValue();
				if (leaveTime > currentSec) {
					answerText.setText("You're on time");
				} else {
					long lateBy = currentSec - leaveTime;
					answerText
							.setText("You're late by " + lateBy + " seconds.");
				}
			} else {
				answerText.setText("Sorry, I can't tell you...");
			}
		}

	}

}
