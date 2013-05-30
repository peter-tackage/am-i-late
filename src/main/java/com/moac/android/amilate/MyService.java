package com.moac.android.amilate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
    private static final String TAG = "MyService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
        showOldNotification();
        return START_STICKY;
    }

    public void showOldNotification() {

        Notification.Builder mBuilder =
          new Notification.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("My notification")
            .setContentText("Hello World!");

        for(int i = 0; i < 10; i++) {
            try {
                Thread.sleep(10000);
                Log.d(TAG, "wake up");
            } catch(InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Log.d(TAG, "Updating " + i);
            mBuilder.setContentText("Updating " + i);

            NotificationManager mNotificationManager =
              (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(0, mBuilder.build());
        }

        stopSelf();
    }

	/*public void showNotification() {
	    // Prepare intent which is triggered if the
	    // notification is selected
	    Intent intent = new Intent(this, NotificationReceiverActivity.class);
	    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

	    // Build notification
	    // Actions are just fake
	    Notification noti = new Notification.Builder(this)
	        .setContentTitle("White Rabbit")
	        .setContentText("Time to get going!").setSmallIcon(R.drawable.ic_launcher)
	        .setContentIntent(pIntent)
	        .addAction(R.drawable.ic_launcher, "Acceptable to be fashionably late", pIntent)
	        .addAction(R.drawable.ic_launcher, "Send an apologetic SMS", pIntent)
	        .addAction(R.drawable.ic_launcher, "Cancel appointment", pIntent).build();
	    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    // Hide the notification after its selected
	    noti.flags |= Notification.FLAG_AUTO_CANCEL;

	    notificationManager.notify(0, noti);    	  
	}*/
}
