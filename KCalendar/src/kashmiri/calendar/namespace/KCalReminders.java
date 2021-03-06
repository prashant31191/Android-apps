package kashmiri.calendar.namespace;

import java.sql.Date;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class KCalReminders extends BroadcastReceiver {
	    private final String REMINDER_BUNDLE = "KCalReminders"; 

	    // this constructor is called by the alarm manager.
	    public KCalReminders(){ }

	    // you can use this constructor to create the alarm. 
	    //  Just pass in the main activity as the context, 
	    //  any extras you'd like to get later when triggered 
	    //  and the timeout
	     public KCalReminders(Context context, Bundle extras, String reminder,int daysbefore, boolean quickSet){
	         AlarmManager alarmMgr = 
	             (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	         Intent intent = new Intent(context, KCalReminders.class);
	         intent.putExtra(REMINDER_BUNDLE, extras);
	         PendingIntent pendingIntent =
	             PendingIntent.getBroadcast(context, 60, intent, 
	             PendingIntent.FLAG_CANCEL_CURRENT);
	         
	         //Construct the calendar date for reminder first
	         int currMonth = Integer.valueOf(extras.getString("CURRENT_MONTH"));
	         int date = Integer.valueOf(extras.getString("DATE"));
	         String[] Months =  {"MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC","JAN", "FEB"};
	         int year = 2012;
	         if(currMonth > 10)
	         {
	        	 year = 2013;
	        	 currMonth = currMonth - 10;
	         }
	         else
	        	 currMonth = currMonth + 2;
	         Date setDate = new Date(year,currMonth,date);
	         
	         
	         
	         
	         Calendar time = Calendar.getInstance();
	         time.set(year, currMonth, date);
	         time.add(Calendar.DATE, -daysbefore);
	         //time.setTimeInMillis(System.currentTimeMillis());
	         //time.add(Calendar.SECOND, daysbefore);
	         //alarmMgr.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(),
	           //           pendingIntent);
	         if(quickSet)
	        	 CalendarCell.AddReminderInCalendarSilent(time, reminder);
	         else
	        	 CalendarCell.AddReminderInCalendar(time, reminder);
	     }

	      @Override
	     public void onReceive(Context context, Intent intent) {
	         // here you can get the extras you passed in when creating the alarm
	         //intent.getBundleExtra(REMINDER_BUNDLE));
	    	 
	    	  Bundle strreminder = intent.getBundleExtra(REMINDER_BUNDLE);
	    	  String reminder =  "Kashmiri Calendar 2012: You requested to be reminded today for :" + strreminder.getString("REMINDER_NAME") + strreminder.getString("REMINDER_DESCRIPTION");
	    	  
	         Toast.makeText(context, reminder, Toast.LENGTH_LONG).show();

	    	  
	    	    //start activity
	          Intent i = new Intent();
	          i.setClassName("kashmiri.calendar.namespace", "kashmiri.calendar.namespace.KReminderActivity");
	          i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	          i.putExtra("REMINDER_NAME", reminder);	
	          context.startActivity(i);

	     }
	}

