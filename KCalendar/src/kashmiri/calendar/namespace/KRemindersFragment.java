package kashmiri.calendar.namespace;



	
import java.util.Vector;

import kashmiri.calendar.namespace.data.KReminderData;
import android.support.v4.app.DialogFragment;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
	import android.os.Bundle;
	import android.view.LayoutInflater;
	import android.view.View;
	import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
	import android.widget.TextView;
import android.widget.ViewFlipper;


	public class KRemindersFragment extends DialogFragment {
	    static KRemindersFragment newInstance() {
	        return new KRemindersFragment();
	    }
	    
	    
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	    	
	    	
	    	vector = new Vector<KReminderData>();
	    	//Get the main page.. [HACK]... The main page is the first child of the flipper
	    	MainPage main_page = (MainPage)KCalFragment.getFlipper().getChildAt(0);
	    	main_page.mgr.LoadDataBase(getActivity().getApplicationContext());
	    	Cursor cur = main_page.mgr.database_handler.getAllReminders();
	    	
	    	
	    	//m_adapter = new KRemindersAdapter(getActivity().getApplicationContext(), cur);
	    	//View first = m_adapter.getView(0, null, null);
	    	//int count = cur.getCount();
	    	//int count1 = m_adapter.getCount();
	    	main_page.mgr.CloseDataBase(getActivity().getApplicationContext());
	
	    	//View v = KStyleItems.GetTableReminder(getActivity().getApplicationContext());
	    	m_reminderTable = (LinearLayout)KStyleItems.GetTableReminder(getActivity().getApplicationContext());
	    	//m_reminderTable = new LinearLayout(getActivity().getApplicationContext());
	    	//m_reminderTable.setOrientation(LinearLayout.VERTICAL);
	    	//m_reminderTable.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
	    	ScrollView v = new ScrollView(getActivity().getApplicationContext());
			v.setFillViewport(true);
			v.setBackgroundResource(R.drawable.addreminder_bg);
			HorizontalScrollView rem_container = new HorizontalScrollView(getActivity().getApplicationContext());
			rem_container.setFillViewport(true);
			refreshTable(cur);
	    	cur.close();
	    	rem_container.addView(m_reminderTable);
	    	v.addView(rem_container);
	    	return v;
	    	
	    	
		    
	    }
	    
	    public void refreshTable(Cursor cur)
	    {
	    	
	    	if(m_reminderTable == null)
	    		return;
	    	m_reminderTable.removeAllViewsInLayout();
	    	m_reminderTable.removeAllViews();
	    	int count = cur.getCount();

	    	if(count < 1)
	    	{
	    		TextView v = new TextView(getActivity());
	    		v.setText("You have no reminders set yet!");
	    		v.setTextSize((float) 32.0);
	    		v.setTextColor(Color.RED);
	    		v.setMaxWidth(120);
	    		m_reminderTable.addView(v);
	    		return;
	    	}
	    		
	    	cur.moveToFirst();
	    	TextView v = new TextView(getActivity());
    		v.setText(String.format("You have %s reminder(s) set", count));
    		v.setTextSize((float) 32.0);
    		v.setTextColor(Color.BLUE);
    		v.setMaxWidth(120);
    		m_reminderTable.addView(v);
	    	for(int i = 0;i < count; ++i)
	    	{
	    		
	    			LinearLayout table = (LinearLayout)KStyleItems.GetTableReminder(getActivity().getApplicationContext());
	    			
	    			LinearLayout row = (LinearLayout)table.findViewById(R.id.reminder_row);
	    			CheckBox name = (CheckBox)row.findViewById(R.id.check_box_reminder);
	    			String rem_str = KCalendarManager.ConstructDate( cur.getString(cur.getColumnIndex("date")), cur.getString(cur.getColumnIndex("current_month")));
	    			name.setText(cur.getString(cur.getColumnIndex("name")) + " for " + rem_str);
	    			if(cur.getString(cur.getColumnIndex("name")).length() == 0)
	    				name.setText("(Reminder) for " + rem_str);
	    			
	    			
	    			table.removeView(row);
	    		
    				//Set data tag to the checkbox
	    			KReminderData tag = new KReminderData();
	    			tag.name = cur.getString(cur.getColumnIndex("name"));
	    			tag.date = cur.getString(cur.getColumnIndex("date"));
	    			tag.curr_month = cur.getString(cur.getColumnIndex("current_month"));
	    			
	    			name.setTag(tag);
	    			
	    			
	    			
	    			
	    			m_reminderTable.addView(row);
	    			vector.add(name);
	    			cur.moveToNext();
	    	}
	    	m_DeleteReminders = (Button)KStyleItems.GetReminderButton(getActivity().getApplicationContext());
	    	m_DeleteReminders.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					MainPage main_page = (MainPage)KCalFragment.getFlipper().getChildAt(0);
	    	    	main_page.mgr.LoadDataBase(getActivity().getApplicationContext());
	    	    	Cursor c1;
					for(int i = 0; i < vector.size(); ++i)
					{	
						CheckBox c = (CheckBox)vector.elementAt(i);
						if(c.isChecked())
						{
							KReminderData data = (KReminderData)c.getTag();
			    	    	main_page.mgr.database_handler.DeleteReminder(data.name,data.date,data.curr_month);
							//main_page.mgr.database_handler.DeleteAllReminders();
							//c1 = main_page.mgr.database_handler.getMatchingReminders(data.name,data.date,data.curr_month);
							//int i1 = c1.getCount();
							//i1++;
						}
					}
					main_page.mgr.CloseDataBase(getActivity().getApplicationContext());
				
					main_page.mgr.LoadDataBase(getActivity().getApplicationContext());
			    	Cursor cur = main_page.mgr.database_handler.getAllReminders();
			    	
			    	
			    	//m_adapter = new KRemindersAdapter(getActivity().getApplicationContext(), cur);
			    	//View first = m_adapter.getView(0, null, null);
			    	//int count = cur.getCount();
			    	//int count1 = m_adapter.getCount();
			    	main_page.mgr.CloseDataBase(getActivity().getApplicationContext());
					refreshTable(cur);
					
				}
			});
	    	m_reminderTable.addView(m_DeleteReminders);
	    }
	    KRemindersAdapter m_adapter;
	    LinearLayout	m_reminderTable;
	    Button 			m_DeleteReminders;
	    Vector 			vector;
	
}
