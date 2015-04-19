package com.moac.android.amilate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;

import com.moac.android.amilate.api.GoogleMaps;
import com.moac.android.amilate.api.model.Directions;
import com.moac.android.amilate.api.model.Leg;
import com.moac.android.amilate.api.model.Route;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;

public class NotificationService extends Service {

    private static final String TAG = NotificationService.class.getSimpleName();

    private static final long FAILURE = -1;

    private Calendar calendar = null;
    private Timer timer;
    private Handler handler;

    private LocationManager locationManager = null;
    private LocationProvider locationProvider = null;

    private GoogleMaps mApi;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");

        // Create the Google Maps API interface
        Uri.Builder uri = new Uri.Builder().scheme("http").authority("maps.googleapis.com");
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(uri.toString())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        mApi = restAdapter.create(GoogleMaps.class);

        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        locationProvider = locationManager
                .getProvider(LocationManager.GPS_PROVIDER);

        handler = new Handler();

        // Kick off a timer that will check calendar every minute
        timer = new Timer();
        timer.schedule(new CheckWatchTimerTask(),
                0,        //initial delay
                120 * 1000);  //subsequent rate

        return (START_NOT_STICKY);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind()");
        return (null);
    }

    private void stop() {
        Log.i(TAG, "Got to stop()!");
        timer.cancel();
        stopForeground(true);
    }

    class CheckWatchTimerTask extends TimerTask {
        @Override
        public void run() {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            QueryForDirectionsTask task;
                            calendar = new Calendar(getContentResolver());
                            for (CalendarEvent event : calendar.getEvents()) {
                                if (event.isComplete()) {
                                    task = new QueryForDirectionsTask(event);
                                    task.execute();
                                }
                            }
                        }
                    });
                }
            };
            new Thread(runnable).start();
        }
    }

    // TODO Remove business logic and make more testable.
    private class QueryForDirectionsTask extends AsyncTask<Void, Void, NotificationEvent> {

        private CalendarEvent mCalendarEvent;
        public String mEventLocation;
        public long mEventTimeSec;

        public QueryForDirectionsTask(CalendarEvent _event) {
            this.mCalendarEvent = _event;
            this.mEventLocation = _event.getWhere();
            this.mEventTimeSec = TimeUnit.SECONDS.convert(_event.getWhen(), TimeUnit.MILLISECONDS);
        }

        @Override
        protected NotificationEvent doInBackground(Void... _params) {
            Log.i(TAG, "Running background directions query");

            // Let's ask the location provider for our current location
           String best = locationManager.getBestProvider(new Criteria(), true);
           Log.i(TAG, "Using Location Provider: " + best);
           Location lastKnown = locationManager.getLastKnownLocation(best);

            Log.i(TAG, "Last known location was: " + lastKnown);

            if (lastKnown == null)
                return null;

            // Query Google Maps API
            String origin = String.format("%s,%s", lastKnown.getLatitude(), lastKnown.getLongitude());
            Directions directions = mApi.getDirections(origin, mEventLocation, mEventTimeSec, true);

            if (directions != null && directions.isOk()) {
                long totalValue = 0;
                List<Route> routes = directions.getRoutes();
                if (!routes.isEmpty()) {
                    // Pick the first, assume the best (usually only one, according to API docs)
                    Route bestRoute = routes.get(0);
                    for (Leg leg : bestRoute.getLegs()) {
                        // Get the time - DURATION IN SECONDS!!
                        totalValue += leg.getDuration().getValue();
                    }
                    Log.i(TAG, "Calculated total duration (sec): "
                            + totalValue);
                    return new NotificationEvent(mCalendarEvent, totalValue);
                }
            }
            return new NotificationEvent(mCalendarEvent, FAILURE);
        }

        @Override
        public void onPostExecute(NotificationEvent _event) {
            if (_event == null || _event.getTravelTime() == FAILURE) {
                Log.w(TAG, "Some problem determining whether late or not");
                // TODO User notification
                return;
            }

            Time t = new Time();
            t.setToNow();
            long nowSec = TimeUnit.SECONDS.convert(t.toMillis(false), TimeUnit.MILLISECONDS);

            boolean isLate = isLate(nowSec, _event.getTravelTime(), _event.getCalendarEvent().getWhen());
            if (isLate) {
                Log.i(TAG, "Event *is* late: " + mCalendarEvent.getDebugString());
                buildLateNotification(_event, mCalendarEvent);
            } else {
                Log.i(TAG, "Event *is not* late: " + mCalendarEvent.getDebugString());
            }
        }

        protected void buildLateNotification(NotificationEvent _event, CalendarEvent _calendarEvent) {
            // long lateBy = Math.abs(currentSec - leaveTime);
            long minutes = TimeUnit.MINUTES.convert(_event.getTravelTime(), TimeUnit.SECONDS);
            String lateText = "Let's go!\n"
                    + "It takes " + minutes + " minutes to get there!";
            showNotification(_calendarEvent, lateText);
        }
    }

    protected boolean isLate(long _now, long _travelTime, long _eventTime) {
        Log.i(TAG, "Current time in seconds since epoch is: " + _now);
        // Now we work out if we're ok.
        // TODO A buffer related to the polling period should be factored in.
        long leaveTime = _eventTime - _travelTime;
        return leaveTime > _now;
    }

    private void showNotification(CalendarEvent event, String text) {

        // TODO Google Calendar Intent.
        Notification.Builder whiteRabbitNotification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.white_rabbit)
                .setContentTitle(event.getWhat() + " in " + event.getWhere())
                .setContentText(text);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // let's keep it simple
        // use the time as the unique id for the Notification
        int id = ((Long) event.getWhen()).intValue();
        mNotificationManager.notify(id, whiteRabbitNotification.build());
    }
}
