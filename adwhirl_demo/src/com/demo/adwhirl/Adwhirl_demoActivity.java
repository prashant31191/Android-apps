package com.demo.adwhirl;

import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.adwhirl.AdWhirlManager;
import com.adwhirl.AdWhirlTargeting;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Adwhirl_demoActivity extends Activity implements AdWhirlInterface{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        AdWhirlManager.setConfigExpireTimeout(1000 * 60 * 5);
        //AdWhirlTargeting.setTestMode(true); 
//        AdWhirlTargeting.setAge(23);
//        AdWhirlTargeting.setGender(AdWhirlTargeting.Gender.MALE);
//        AdWhirlTargeting.setKeywords("online games gaming");
//        AdWhirlTargeting.setPostalCode("94123");
//        AdWhirlTargeting.setTestMode(false);
//      
//        AdWhirlLayout adWhirlLayout = (AdWhirlLayout)findViewById(R.id.adwhirl_layout);
//        LinearLayout layout = (LinearLayout)findViewById(R.id.layout_main);
//        TextView textView = new TextView(this);
//        RelativeLayout.LayoutParams layoutParams = new
//          RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
//                                      LayoutParams.WRAP_CONTENT);
//        int diWidth = 320;
//        int diHeight = 52;
//        int density = (int) getResources().getDisplayMetrics().density;
////      
//        adWhirlLayout.setAdWhirlInterface(this);
//        adWhirlLayout.setMaxWidth((int)(diWidth * density));
//        adWhirlLayout.setMaxHeight((int)(diHeight * density));
//      
//        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        textView.setText("Below AdWhirlLayout");
//      
//        
//        
//        layout.setGravity(Gravity.CENTER_HORIZONTAL);
//        layout.addView(adWhirlLayout, layoutParams);
//        layout.addView(textView, layoutParams);
//        layout.invalidate();
    }

	public void adWhirlGeneric() {
		// TODO Auto-generated method stub
		
	}
}