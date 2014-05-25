package com.exercise.AndroidSMS;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextUtils;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import java.util.ArrayList;
import java.util.List;
import android.os.AsyncTask;

public class AndroidSMS extends Activity implements LocationListener {
	private double latitude = 0;
	private double longitude = 0;
	private LocationManager lm;
	
	private class periodicSendCoordinates extends AsyncTask<Object, Void, Void> {
		private boolean sending = false;
		@Override
	     protected Void doInBackground(Object... params) {
	    	 String number = (String) params[0];
	    	 int messages = (Integer) params[1];
	    	 int delay = (Integer) params[2];
	    	 int sent = 0;
	    	 sending = true;
	    	 while (sending) {
	    		 sendCoordinates(number);
	    		 try {
	    			 Thread.sleep(delay*1000);
	    		 } catch (InterruptedException e) {
	    			 e.printStackTrace();
	    		 }	    		 
	    		 if (sent < messages) {
	    			 sent++;
	    		 } else if (sent == messages) {
	    			 sending = false;
	    		 }
	    	 }
	    	 return null;
	     }
		public void stop() {
			sending = false;
		}
		public boolean running() {
			return sending;
		}
	}
	private periodicSendCoordinates sendingThread = new periodicSendCoordinates();
	/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_android_sms);
    
      final EditText edittextSmsNumber = (EditText)findViewById(R.id.smsnumber);
      final EditText edittextSmsText = (EditText)findViewById(R.id.smstext);
      Button buttonSendSms = (Button)findViewById(R.id.sendsms);
      Button button_exit_btn = (Button)findViewById(R.id.exit_btn);
    
      buttonSendSms.setOnClickListener(new Button.OnClickListener(){
    	 @Override
		 public void onClick(View arg0) {
		  // TODO Auto-generated method stub
    		 parseStringAndSendSms(edittextSmsNumber.getText().toString(), edittextSmsText.getText().toString());
		  }});
      
      button_exit_btn.setOnClickListener(new OnClickListener() {
    	  @Override
	      public void onClick(View v) {
	      // TODO Auto-generated method stub
	      finish();
	      System.exit(0);
	      }});
  }
  protected void onResume() {
	  super.onResume();
	  lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
	  if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
		  lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
	  lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
  }
  protected void onPause() {
	  super.onPause();
	  lm.removeUpdates(this);
  }
  protected void sendSMS(String smsNumber, String smsText) {
	  SmsManager smsManager = SmsManager.getDefault();
	  smsManager.sendTextMessage(smsNumber, null, smsText, null, null);	  
  }
  protected void sendCoordinates(String number) {
	  String output = "Coordinates: "+latitude+" / "+longitude;
	  sendSMS(number, output);
  }
  public void parseStringAndSendSms(String number, String text) {
	  TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(' ');
	  splitter.setString(text);
	  List<String> argsList = new ArrayList<String>();
	  for (String s : splitter) {
		  argsList.add(s);
	  }
	  String[] args = new String[argsList.size()]; 
	  argsList.toArray(args);
	  if (args.length > 0) {
		  if (TextUtils.equals(args[0], "geoloc")) {
			  if (args.length == 3) {
				  if (args[1].matches("^[0-9]+$") && args[2].matches("^[0-9]+$")) {
					  if (sendingThread.running() == false) {
						  sendingThread.execute(number, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
					  }
				  } else if (TextUtils.equals(args[1], "start") && args[2].matches("^[0-9]+$")) {
					  if (sendingThread.running() == false) {
						  sendingThread.execute(number, -1, Integer.parseInt(args[2]));
					  }
				  }
			  } else if (args.length == 2) {
				  if (TextUtils.equals(args[1], "stop")) {
					  if (sendingThread.running() == true) {
						  sendingThread.stop();
					  }
				  }
			  } else {
				  sendCoordinates(number);
			  }
		  }
	  }
  }
  public void onLocationChanged(Location loc) {
	  latitude = loc.getLatitude();
	  longitude = loc.getLongitude();
  }
  public void onStatusChanged(String s, int i, Bundle b) {
	  
  }
  public void onProviderEnabled(String s) {
  }
  public void onProviderDisabled(String s) {
	  
  }
}