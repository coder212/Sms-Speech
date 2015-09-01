package com.dev.speechtomessaging;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class SpeechToMessagingActivity extends Activity {
	
	private final int CODE_SPEECH_TO_TEXT = 100;
	String phone_text;
	private EditText phoneNum;
	private ImageButton vbut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODOs Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		phoneNum = (EditText)findViewById(R.id.edittPhone);
		vbut = (ImageButton)findViewById(R.id.voicebutton);
		vbut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODOs Auto-generated method stub
				phone_text = phoneNum.getText().toString();
				if(phone_text != null && phone_text.length()>0){
					speechTotext();
				}
				
			}
		});
	}
	
	private void speechTotext(){
		Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
		
		try{
			startActivityForResult(speechIntent, CODE_SPEECH_TO_TEXT);
		}catch(ActivityNotFoundException e){
			Toast.makeText(getApplicationContext(), getString(R.string.youd), Toast.LENGTH_SHORT).show();
		}
		
	}
	private void kirimSms(String psn){
		String KIRIM = "SMS_SENT";
		String TERKIRIM = "SMS_DELIVERED";
		PendingIntent kirpen = PendingIntent.getBroadcast(this, 0, new Intent(KIRIM), 0);
		PendingIntent terkipen = PendingIntent.getBroadcast(this,0, new Intent(TERKIRIM), 0);
		registerReceiver(new BroadcastReceiver(){

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODOs Auto-generated method stub
				switch(getResultCode()){
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), getString(R.string.text_send), Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(), getString(R.string.text_gen), Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), getString(R.string.text_noser), Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(), getString(R.string.text_ernul), Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), getString(R.string.text_radof), Toast.LENGTH_SHORT).show();
					break;
				}
			}
			
		},new IntentFilter(KIRIM));
		
		registerReceiver(new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODOs Auto-generated method stub
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), getString(R.string.text_deliver), Toast.LENGTH_SHORT).show();
					break;

				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), getString(R.string.text_cancel), Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(TERKIRIM));
		
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phone_text, null, psn, kirpen, terkipen);
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODOs Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CODE_SPEECH_TO_TEXT:
			if(resultCode == RESULT_OK && data != null){
				ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				kirimSms(result.get(0));
			}
			break;

		
		}
	}
	
	
	
}
