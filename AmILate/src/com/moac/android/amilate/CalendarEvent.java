package com.moac.android.amilate;

import android.util.Log;

public class CalendarEvent {
	
		private static int IMPORTANT= 0;
		private static int PARTY= 1;
		
		private String what;
		private String where;
		private long when;
		private int type;
		
		public CalendarEvent() {
			// defaut, all events are important
			type = IMPORTANT;
		}
		
		public void setWhat(String what) {
			this.what = what;
		}
		
		public void setWhen(long when) {
			this.when = when;
		}
		
		public void setWhere(String where) {
			this.where = where;
		}
		
		public void setType(int type) {
			this.type = type;
		}
		
		
		// We will only take the events with all the what, when, where in consideration for now
		// Later version, we can prompt the user for more details
		/*public boolean isComplete() {
			if (what == null || when == null || where == null ) return false;
			return true;				
		}*/

		public void printMe() {

	        String msg = "What: " + what + " Where: " + where + " When: "+ when + " Type: " + type;
			Log.d("CalendarEvent", msg);
		}
}
 