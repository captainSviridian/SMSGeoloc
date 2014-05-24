package com.exercise.AndroidSMS;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AndroidSMS extends Activity {
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
		  SmsManager smsManager = SmsManager.getDefault();
		  String smsNumber = edittextSmsNumber.getText().toString();
		  String smsText = edittextSmsText.getText().toString();
		  smsManager.sendTextMessage(smsNumber, null, smsText, null, null);
		  }});
      
      button_exit_btn.setOnClickListener(new OnClickListener() {
    	  @Override
	      public void onClick(View v) {
	      // TODO Auto-generated method stub
	      finish();
	      System.exit(0);
	      }});
  }
}