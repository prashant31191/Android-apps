package kashmiri.calendar.namespace;

import java.io.IOException;
import java.security.KeyRep;
import java.util.Calendar;
import java.util.Currency;
import java.util.Vector;

import kashmiri.calendar.namespace.data.DayView;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

public class KCalendarManager {
	
	static public String[] Months = {"MAR 2012", "APR 2012","MAY 2012", "JUN 2012", "JUL 2012", "AUG 2012", "SEP 2012", "OCT 2012", "NOV 2012", "DEC 2012", "JAN 2013", "FEB 2013","MAR 2013", "APR 2013"};
	
	public KCalendarManager(Context context, CalendarWidget cal)
	{
		calendar = cal;
		LoadDataBase(context);
		SetCurrentMonth(0);
		UpdateCalendar(CalculateCurrentMonth());
		_context = context;
		vectDayView = new Vector<DayView>();
		LoadDayView();
		CloseDataBase(context);
	}
	private int CalculateCurrentMonth()
	{
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		if (cal.get(Calendar.YEAR) == 2012)
			{
				SetCurrentMonth(month - 2);
				return month - 2;
			}
			else
			{
				SetCurrentMonth(month + 10);
				return month + 10;
			}
			
	}
	
	private void LoadDayView() {
		// TODO Auto-generated method stub
		Cursor c = database_handler.getAllDays();
		c.moveToFirst();
		int count_days = c.getCount();
		
		for(int i = 0; i < count_days;++i)
		{
			DayView day = new DayView();
			day.day_no = c.getString(c.getColumnIndex("_id"));
			day.tithi = c.getString(c.getColumnIndex("Tithi"));
			day.festival= c.getString(c.getColumnIndex("Festival"));
			day.day = c.getString(c.getColumnIndex("Day"));
			day.month = GetMonth(day.day_no,c.getString(c.getColumnIndex("Date")));
			 
			String date = c.getString(c.getColumnIndex("Date"));
     	   String[] arr = date.split("-");
     	   day.date = arr[0];
			vectDayView.add(day);
			c.moveToNext();
		}
	}
	private String GetMonth(String day_no, String string) {
		// TODO Auto-generated method stub
		if(string.indexOf("Mar") > 0)
		{
			if(Integer.parseInt(day_no) < 100)
				return Integer.toString(0);
			else 
				return Integer.toString(12);
		}
		if(string.indexOf("Apr") > 0)
		{
			if(Integer.parseInt(day_no) < 100)
				return Integer.toString(1);
			return Integer.toString(1);
		}
		if(string.indexOf("May") > 0)
		{
			return Integer.toString(2);
		}
		if(string.indexOf("Jun") > 0)
		{
			return Integer.toString(3);
		}
		if(string.indexOf("Jul") > 0)
		{
			return Integer.toString(4);
		}
		if(string.indexOf("Aug") > 0)
		{
			return Integer.toString(5);
		}
		if(string.indexOf("Sep") > 0)
		{
			return Integer.toString(6);
		}
		if(string.indexOf("Oct") > 0)
		{
			return Integer.toString(7);
		}
		if(string.indexOf("Nov") > 0)
		{
			return Integer.toString(8);
		}
		if(string.indexOf("Dec") > 0)
		{
			return Integer.toString(9);
		}
		if(string.indexOf("Jan") > 0)
		{
			return Integer.toString(10);
		}
		if(string.indexOf("Feb") > 0)
		{
			return Integer.toString(11);
		}
		return "";
		
	}
	public void LoadDataBase(Context context)
	{
		database_handler = new DatabaseHandler(context);
		try {
			database_handler.createDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void CloseDataBase(Context context)
	{
	
		if(database_handler != null)
		{
			database_handler.close();
		}
	}
	
	public void SetCurrentMonth(int currMonth)
	{
		CurrentMonth = currMonth;
	}
	
	public int GetCurrentMonth()
	{
		return CurrentMonth;
	}
	
	
	public void SetCalendar(CalendarWidget cal)
	{
		
		calendar = cal;
	}
	
	public void UpdateCalendar(int currMonth)
	{
		
		//First clear all the cells
		for(int ii = 0; ii < 42; ++ii)
			calendar.cell[ii].ClearCell();
	
		Cursor cursor = database_handler.getMonthInformation(currMonth);
		String[] Days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}; 
		
		if (cursor.moveToFirst()) {
			for (int n = 0; n < Days.length; n++)
			{
				if (Days[n].equals(cursor.getString(2)))
					{
						strStartDay = n;
						break;
					}
				
			}
		}
		int count = strStartDay;
		if (cursor.moveToFirst()) {
           do {
        	   
        	  
        	   String info = cursor.getString(cursor.getColumnIndex("Tithi"));
        	   String note = cursor.getString(cursor.getColumnIndex("Festival"));
        	   String id = cursor.getString(cursor.getColumnIndex("_id"));
        	   id = String.valueOf(Integer.parseInt(id) - 1);
        	   //Start populating the calendar
        	   String date = cursor.getString(cursor.getColumnIndex("Date"));
         	   String[] arr = date.split("-");
        	   calendar.SetCellInfo(id,count,arr[0], info,note);
        	   count++;
           } while (cursor.moveToNext());
       }
		cursor.close();
		MainPage main_page = (MainPage)KCalFragment.getFlipper().getChildAt(0);
		if(main_page != null)
			main_page.SetCurrentDate(calendar);
		//setBitmap(calendar);
	}
	
	public void SetReminder(String date, String ReminderName, String ReminderDescription, String daysbefore, boolean quickset)
	{
		//1. Insert into the database.
		//2. Setup an alarm in the phone.
		Bundle _bundle = new Bundle();
		_bundle.putString("REMINDER_NAME", ReminderName);
		_bundle.putString("REMINDER_DESCRIPTION", ReminderDescription);
		_bundle.putString("DATE", date);
		_bundle.putString("CURRENT_MONTH", String.valueOf(CurrentMonth));
		KCalReminders reminder = new KCalReminders(_context, _bundle, ReminderName, Integer.parseInt(daysbefore), quickset);
		
		//Add to reminder table
		LoadDataBase(_context);
		database_handler.InsertReminder(CalendarCell.reminder_name.getText().toString(), 
				CalendarCell.reminder_desc.getText().toString(), 
				daysbefore,
				date,Integer.toString(CurrentMonth));
		CloseDataBase(_context);	
	}
	
	public static String ConstructDate(String Date, String Month)
	{
		String str = null;
		switch(Integer.parseInt(Month) + 1)
		{
		case 1:str = Date + " MARCH 2012";
		break;
		case 2:str = Date + " APRIL 2012";
		break;

		case 3:str = Date + " MAY 2012";
		break;

		case 4:str = Date + " JUNE 2012";
		break;

		case 5:str = Date + " JULY 2012";
		break;

		case 6:str = Date + " AUGUST 2012";
		break;

		case 7:str = Date + " SEPT 2012";
		break;

		case 8:str = Date + " OCT 2012";
		break;

		case 9:str = Date + " NOV 2012";
		break;

		case 10:str = Date + " DEC 2012";
		break;

		case 11:str = Date + " JAN 2013";
		break;

		case 12:str = Date + " FEB 2013";
		break;

		case 13:str = Date + " MARCH 2013";
		break;

		case 14:str = Date + " APR 2013";
		break;

			
		}
		return str;	
	}
	CalendarWidget calendar;
	public int				CurrentMonth;
	DatabaseHandler	database_handler;
	int				strStartDay;
	Context			_context;
	Vector			vectDayView;
	
	public  void setBitmap(View dayview)
	{
		setBitmap(dayview, String.valueOf(CurrentMonth));
	}
	
	public static void setBitmap(View dayview, String month) {
		// TODO Auto-generated method stub
		switch(Integer.parseInt(month))
		{
		case 0:
			dayview.setBackgroundResource(R.drawable.march);
			break;
		case 1:
			dayview.setBackgroundResource(R.drawable.april);
			break;
		case 2:
			dayview.setBackgroundResource(R.drawable.may);
			break;
		case 3:
			dayview.setBackgroundResource(R.drawable.june);
			break;
		case 4:
			dayview.setBackgroundResource(R.drawable.july);
			break;
		case 5:
			dayview.setBackgroundResource(R.drawable.august);
			break;
		case 6:
			dayview.setBackgroundResource(R.drawable.september);
			break;
		case 7:
			dayview.setBackgroundResource(R.drawable.october);
			break;
		case 8:
			dayview.setBackgroundResource(R.drawable.november);
			break;
		case 9:
			dayview.setBackgroundResource(R.drawable.december);
			break;
		case 10:
			dayview.setBackgroundResource(R.drawable.january);
			break;
		case 11:
			dayview.setBackgroundResource(R.drawable.february);
			break;
		case 12:
			dayview.setBackgroundResource(R.drawable.march1);
			break;
		case 13:
			dayview.setBackgroundResource(R.drawable.april1);
			break;
		}
	}

}
