package com.captainsviridian.smsgeoloc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.Intent;

public class SMSGeolocUI extends Activity {
	  public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_android_sms);
	    
	      Button buttonStart = (Button)findViewById(R.id.start_btn);
	      Button buttonStop = (Button)findViewById(R.id.stop_btn);
	      Button buttonClose = (Button)findViewById(R.id.close_btn);
	      
		  final Intent serviceIntent = new Intent(this, SMSGeolocService.class);
	    
	      buttonStart.setOnClickListener(new Button.OnClickListener(){
	    	 @Override
			 public void onClick(View arg0) {
	    		 startService(serviceIntent);
			  }});
	      buttonStop.setOnClickListener(new Button.OnClickListener(){
	    	 @Override
			 public void onClick(View arg0) {
	    		 stopService(serviceIntent);
			  }});
	      
	      buttonClose.setOnClickListener(new OnClickListener() {
	    	  @Override
		      public void onClick(View v) {
	    		  finish();
		      }});
	  }
	  protected void onResume() {
		  super.onResume();

	  }
	  protected void onPause() {
		  super.onPause();

	  }
}
