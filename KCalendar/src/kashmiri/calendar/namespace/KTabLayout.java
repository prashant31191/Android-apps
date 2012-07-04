package kashmiri.calendar.namespace;


import android.app.Activity;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class KTabLayout extends Fragment implements OnTabChangeListener {
 
    private static final String TAG = "Kashmiri Calendar 2012";
    public static final String TAB_CALENDAR = "Calendar";
    public static final String TAB_REMINDERS = "Reminders";
    public static final String TAB_FEEDBACK = "Feedback";
 
    private View mRoot;
    private TabHost mTabHost;
    private int mCurrentTab;

 
    public static KRemindersFragment GetReminderFragment()
    {
    	return m_reminderfragment;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.tabs_fragment, null);
        mTabHost = (TabHost) mRoot.findViewById(android.R.id.tabhost);
        setupTabs();
        
        ///KAchra code
       
        ////
        return mRoot;
    }
 
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
 
        mTabHost.setOnTabChangedListener(this);
        mTabHost.setCurrentTab(mCurrentTab);
        // manually start loading stuff in the first tab
        updateTab(TAB_CALENDAR, R.id.tab_1);
    }
 
    private void setupTabs() {
        mTabHost.setup(); // you must call this before adding your tabs!
        mTabHost.addTab(newTab(TAB_CALENDAR, R.string.tab_calendar, R.id.tab_1));
        mTabHost.addTab(newTab(TAB_REMINDERS, R.string.tab_reminders, R.id.tab_2));
        mTabHost.addTab(newTab(TAB_FEEDBACK, R.string.tab_feedback, R.id.tab_3));
        m_reminderfragment = new KRemindersFragment();
        m_calfragment = new KCalFragment();
        m_feedbackfragment = new KFeedbackFragment();
    }
 
    private TabSpec newTab(String tag, int labelId, int tabContentId) {
        Log.d(TAG, "buildTab(): tag=" + tag);
 
        View indicator = LayoutInflater.from(getActivity()).inflate(
                R.layout.tab,
                (ViewGroup) mRoot.findViewById(android.R.id.tabs), false);
        ((TextView) indicator.findViewById(R.id.text)).setText(labelId);
 
        TabSpec tabSpec = mTabHost.newTabSpec(tag);
        tabSpec.setIndicator(indicator);
        tabSpec.setContent(tabContentId);
        return tabSpec;
    }
 

  
    private void updateTab(String tabId, int placeholder) {
        FragmentManager fm = getFragmentManager();
       
        if (fm.findFragmentByTag(tabId) == null) {
            
        	if (TAB_CALENDAR.equals(tabId))
	        	fm.beginTransaction()
	                    .replace(placeholder, new KCalFragment(), tabId)
	                    .commit();
        	if (TAB_REMINDERS.equals(tabId))
        		{
        		fm.beginTransaction()
        		
                .replace(placeholder, m_reminderfragment, tabId)
                .commit();
        		MainPage main_page = (MainPage)KCalFragment.getFlipper().getChildAt(0);
    	    	main_page.mgr.LoadDataBase(getActivity().getApplicationContext());
    	    	Cursor cur = main_page.mgr.database_handler.getAllReminders();
    	    	    	    	main_page.mgr.CloseDataBase(getActivity().getApplicationContext());
    	
	    	    	m_reminderfragment.refreshTable(cur);
    	    	cur.close();
    	    	main_page.mgr.CloseDataBase(getActivity().getApplicationContext());
        		
        		}
        	if (TAB_FEEDBACK.equals(tabId))
	        	fm.beginTransaction()
	                    .replace(placeholder, new KFeedbackFragment(), tabId)
	                    .commit();

        }
        //Keep updating content here
    }
     


	    public void onTabChanged(String tabId) {
	        Log.d(TAG, "onTabChanged(): tabId=" + tabId);
	        
	        
	        
	        if (TAB_CALENDAR.equals(tabId)) {
	            updateTab(tabId, R.id.tab_1); 
	            mCurrentTab = 0;
	            return;
	        }
	        if (TAB_REMINDERS.equals(tabId)) {
	            updateTab(tabId, R.id.tab_2);
	            mCurrentTab = 1;
	            MainPage main_page = (MainPage)KCalFragment.getFlipper().getChildAt(0);
    	    	main_page.mgr.LoadDataBase(getActivity().getApplicationContext());
    	    	Cursor cur = main_page.mgr.database_handler.getAllReminders();
    	    	    	    	main_page.mgr.CloseDataBase(getActivity().getApplicationContext());
    	
	    	    	m_reminderfragment.refreshTable(cur);
    	    	cur.close();
    	    	main_page.mgr.CloseDataBase(getActivity().getApplicationContext());
	            return;
	        }
	        if (TAB_FEEDBACK.equals(tabId)) {
	            updateTab(tabId, R.id.tab_3); 
	            mCurrentTab = 2;
	            return;
	        }
	    }
	    KCalFragment m_calfragment;
	    static KRemindersFragment m_reminderfragment;
	    KFeedbackFragment m_feedbackfragment;
}