package com.captainsviridian.smsgeoloc;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AndroidSMS extends Activity implements LocationListener {
	private double latitude = 0;
	private double longitude = 0;
	private LocationManager lm;
	boolean running = false;
	
	final SmsManager sms = SmsManager.getDefault();
	
	IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
	private BroadcastReceiver IncomingSms = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
		// Retrieves a map of extended data from the intent.
			final Bundle bundle = intent.getExtras();
			try {			
				if (bundle != null) {				
					final Object[] pdusObj = (Object[]) bundle.get("pdus");				
					for (int i = 0; i < pdusObj.length; i++) {
						SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
						String phoneNumber = currentMessage.getDisplayOriginatingAddress();					
						String senderNum = phoneNumber;
						String message = currentMessage.getDisplayMessageBody();	
				        int duration = Toast.LENGTH_LONG;
						/*Toast toast = Toast.makeText(context, "senderNum: "+ senderNum + ", message: " + message, duration);*/
				        Toast toast = Toast.makeText(context, message, duration);
						toast.show();
						parseStringAndSendSms(senderNum, message);
					} // end for loop
				} // bundle is null
			} catch (Exception e) {
				Log.e("SmsReceiver", "Exception smsReceiver" +e);				
			}
		}
	};
	private class periodicSendCoordinates extends AsyncTask<Object, Void, Void> {
		private boolean sending = false;
		@Override
	     protected Void doInBackground(Object... params) {
	    	 String number = (String) params[0];
	    	 int messages = (Integer) params[1];
	    	 int delay = (Integer) params[2];
	    	 int sent = 0;
	    	 sending = true;
	    	 messages--;
	    	 running = true;
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
	    	 running = false;
	    	 return null;
	     }
        protected void onPostExecute(Void result) {
            Toast.makeText(getApplicationContext(), "Le traitement asynchrone est terminé", Toast.LENGTH_LONG).show();
        }
		public void stop() {
			sending = false;
		}
		public boolean running() {
			return sending;
		}
	}
	private periodicSendCoordinates sendingThread;
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
    		 sendSMS(edittextSmsNumber.getText().toString(), edittextSmsText.getText().toString());
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
	  registerReceiver(IncomingSms, intentFilter);
  }
  protected void onPause() {
	  super.onPause();
	  lm.removeUpdates(this);
	  unregisterReceiver(IncomingSms);
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
      Toast toast3 = Toast.makeText(this, "parseString: "+text, Toast.LENGTH_SHORT);
      toast3.show();
	  TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(' ');
	  splitter.setString(text);
	  List<String> argsList = new ArrayList<String>();
	  for (String s : splitter) {
		  argsList.add(s);
	  }
	  String[] args = new String[argsList.size()]; 
	  argsList.toArray(args);
	  if (args.length > 0) {
		  String argsOutput = "";
		  for (int i = 0 ; i < args.length ; i++) {
			  argsOutput += "Arg "+i+": "+args[i]+"\n";
		  }
	      Toast toast2 = Toast.makeText(this, argsOutput, Toast.LENGTH_SHORT);
	      toast2.show();
		  if (args[0].matches("geoloc")) {
		      Toast toast5 = Toast.makeText(this, "geoloc...", Toast.LENGTH_SHORT);
		      toast5.show();
			  if (args.length == 3) {
			      Toast toast6 = Toast.makeText(this, "3 args", Toast.LENGTH_SHORT);
			      toast6.show();
				  if (args[1].matches("[0-9]+") && args[2].matches("[0-9]+")) {
					  if (running == false) {
					      Toast toast = Toast.makeText(this, "Send location every "+args[2]+" seconds, "+args[1]+" times to "+number, Toast.LENGTH_SHORT);
					      toast.show();
					      sendingThread = new periodicSendCoordinates();
						  sendingThread.execute(number, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
					  } else {
					      Toast toast = Toast.makeText(this, "Thread finite periodic already started", Toast.LENGTH_SHORT);
					      toast.show();
					  }
				  } else if (args[1].matches("start") && args[2].matches("[0-9]+")) {
					  if (running == false) {
					      Toast toast = Toast.makeText(this, "Begin to send location every "+args[2]+" seconds to "+number, Toast.LENGTH_SHORT);
					      toast.show();
					      sendingThread = new periodicSendCoordinates();
						  sendingThread.execute(number, -1, Integer.parseInt(args[2]));
					  } else {
					      Toast toast = Toast.makeText(this, "Thread infinite periodic already started", Toast.LENGTH_SHORT);
					      toast.show();
						  
					  }
				  } else {
				      Toast toast = Toast.makeText(this, "Incorrect input", Toast.LENGTH_SHORT);
				      toast.show();					  
				  }
			  } else if (args.length == 2) {
			      Toast toast6 = Toast.makeText(this, "2 args", Toast.LENGTH_SHORT);
			      toast6.show();
				  if (args[1].matches("stop")) {
					  if (running == true) {
					      Toast toast = Toast.makeText(this, "Stop to send location", Toast.LENGTH_SHORT);
					      toast.show();
					      sendingThread.stop();
					  } else {
					      Toast toast = Toast.makeText(this, "Location already stopped", Toast.LENGTH_SHORT);
					      toast.show();
					  }
				  }
			  } else {
				  sendCoordinates(number);
			      Toast toast = Toast.makeText(this, "Sending location to "+number, Toast.LENGTH_SHORT);
			      toast.show();
			  }
		  } else {
		      Toast toast = Toast.makeText(this, args[0]+" doesn't match geoloc", Toast.LENGTH_SHORT);
		      toast.show();		  
		  }
	  } else {
	      Toast toast = Toast.makeText(this, args.length+" arguments are not enough", Toast.LENGTH_SHORT);
	      toast.show();		  		  
	  }
      Toast toast = Toast.makeText(this, "ParseString OUT", Toast.LENGTH_SHORT);
      toast.show();	
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