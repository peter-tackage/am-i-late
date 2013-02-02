package com.moac.android.amilate;

import android.os.Bundle;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.view.Menu;

import java.text.Format;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity { //implements OnClickListener {

	private Calendar calendar = null;

	//private Cursor mCursor = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        

        calendar = new Calendar(getContentResolver()); 
        showNotification();
        // FOR TEST
        //mCursor = calendar.getCursor();
        //createDisplay();
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void showNotification() {
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
    }
    
    
    /*private void createDisplay() {        
        Button b = (Button)findViewById(R.id.next);
        b.setOnClickListener(this);
        b = (Button)findViewById(R.id.previous);
        b.setOnClickListener(this);
        //onClick(findViewById(R.id.previous));
    }

    
    @Override
	public void onClick(View v) {
		TextView tv = (TextView)findViewById(R.id.data);
		String title = "N/A";
		String location = "N/A";
		Long start = 0L;
	    switch(v.getId()) {
		case R.id.next:
			if(!mCursor.isLast()) mCursor.moveToNext();
			break;
		case R.id.previous:
			if(!mCursor.isFirst()) mCursor.moveToPrevious();
			break;
		}
		Format df = DateFormat.getDateFormat(this);
		Format tf = DateFormat.getTimeFormat(this);
        try {
        	title = mCursor.getString(0);
        	location = mCursor.getString(1);
        	start = mCursor.getLong(2);
        } catch (Exception e) {
        	//ignore
        }
        tv.setText("What: " + title+ " Where: " + location + " When: "+df.format(start)+" at "+tf.format(start));
	}*/
    
}
