package aero.zztrop;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ScreenTimeFastpad extends Activity implements OnClickListener{

	private int timeType;
	private Calendar calTime;
	private int hours = 0;
	private int minutes = 0;
	private Button btnHourActiv; // latest pressed hour button
	private Button btnMinuteActiv; // latest pressed minute button
	private Boolean isHourPicked = false; // set whether a hour button has been pressed
	private Boolean isMinutePicked = false; // set whether a minute button has been pressed

	private Button btnH00, btnH01, btnH02, btnH03, btnH04, btnH05;
	private Button btnH06, btnH07, btnH08, btnH09, btnH10, btnH11;
	private Button btnH12, btnH13 ,btnH14, btnH15, btnH16, btnH17;
	private Button btnH18, btnH19, btnH20, btnH21, btnH22, btnH23;
	private Button btnM00, btnM05, btnM10, btnM15, btnM20, btnM25;
	private Button btnM30, btnM35, btnM40, btnM45, btnM50, btnM55;
	
	protected void onCreate(Bundle savedInstanceState) {
		Utils.applySharedTheme(this);
		super.onCreate(savedInstanceState);
		if (!Utils.isTablet(getApplicationContext())) {
			// set phones to portrait;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.screen_time_fastpad);
		initComponents();
	}
	
	private void assignButtonTextToField(View view, int i)
	{
		if (view instanceof Button)
		{
			switch (i)
			{
				case 0:
					isHourPicked = true;
					//on remet la couleur par defaut au btnActiv
					if (btnHourActiv != null)
					{
						btnHourActiv.setBackgroundResource(R.drawable.btn_cdu_normal);
					}
					//on affecte et on change la couleur du nouveau btn
					btnHourActiv = (Button) view;
					this.hours = Integer.parseInt((String) btnHourActiv.getText());
					this.calTime.set(Calendar.HOUR_OF_DAY, hours);
					btnHourActiv.setBackgroundResource(R.drawable.btn_cdu_pressed);
					break;

				case 1:
					isMinutePicked = true;
					//on remet la couleur par defaut au btnActiv
					if (btnMinuteActiv != null)
					{
						btnMinuteActiv.setBackgroundResource(R.drawable.btn_cdu_normal);
					}
					//on affecte et on changela couleur du nouveau btn
					btnMinuteActiv = (Button) view;
					this.minutes = Integer.parseInt((String) btnMinuteActiv.getText());
					this.calTime.set(Calendar.MINUTE, minutes);
					btnMinuteActiv.setBackgroundResource(R.drawable.btn_cdu_pressed);
					break;

				default:
					break;
			}
			sendTimeDataBack();
		}
	}
	
	private void sendTimeDataBack()
	{
		if (isHourPicked == true && isMinutePicked == true)
		{
			// prepare the return data
			Intent returnIntent = new Intent();
			returnIntent.putExtra("time", calTime.getTimeInMillis());
			returnIntent.putExtra("time_type", timeType);
			setResult(RESULT_OK, returnIntent);
			
			// reset the flags kill the dialog
			isHourPicked = false;
			isMinutePicked = false;
			btnHourActiv.setBackgroundResource(R.drawable.btn_cdu_normal);
			btnMinuteActiv.setBackgroundResource(R.drawable.btn_cdu_normal);
			
			// kill the screen
			finish();
		}
	}
	
	private void initComponents() {
		calTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calTime.setTimeInMillis(0);

		Bundle bundle = this.getIntent().getExtras();
		timeType = bundle.getInt("time_type");

		btnH00 = (Button) findViewById(R.id.btnH00);
		btnH00.setOnClickListener(this);
		btnH01 = (Button) findViewById(R.id.btnH01);
		btnH01.setOnClickListener(this);
		btnH02 = (Button) findViewById(R.id.btnH02);
		btnH02.setOnClickListener(this);
		btnH03 = (Button) findViewById(R.id.btnH03);
		btnH03.setOnClickListener(this);
		btnH04 = (Button) findViewById(R.id.btnH04);
		btnH04.setOnClickListener(this);
		btnH05 = (Button) findViewById(R.id.btnH05);
		btnH05.setOnClickListener(this);
		btnH06 = (Button) findViewById(R.id.btnH06);
		btnH06.setOnClickListener(this);
		btnH07 = (Button) findViewById(R.id.btnH07);
		btnH07.setOnClickListener(this);
		btnH08 = (Button) findViewById(R.id.btnH08);
		btnH08.setOnClickListener(this);
		btnH09 = (Button) findViewById(R.id.btnH09);
		btnH09.setOnClickListener(this);
		btnH10 = (Button) findViewById(R.id.btnH10);
		btnH10.setOnClickListener(this);
		btnH11 = (Button) findViewById(R.id.btnH11);
		btnH11.setOnClickListener(this);
		btnH12 = (Button) findViewById(R.id.btnH12);
		btnH12.setOnClickListener(this);
		btnH13 = (Button) findViewById(R.id.btnH13);
		btnH13.setOnClickListener(this);
		btnH14 = (Button) findViewById(R.id.btnH14);
		btnH14.setOnClickListener(this);
		btnH15 = (Button) findViewById(R.id.btnH15);
		btnH15.setOnClickListener(this);
		btnH16 = (Button) findViewById(R.id.btnH16);
		btnH16.setOnClickListener(this);
		btnH17 = (Button) findViewById(R.id.btnH17);
		btnH17.setOnClickListener(this);
		btnH18 = (Button) findViewById(R.id.btnH18);
		btnH18.setOnClickListener(this);
		btnH19 = (Button) findViewById(R.id.btnH19);
		btnH19.setOnClickListener(this);
		btnH20 = (Button) findViewById(R.id.btnH20);
		btnH20.setOnClickListener(this);
		btnH21 = (Button) findViewById(R.id.btnH21);
		btnH21.setOnClickListener(this);
		btnH22 = (Button) findViewById(R.id.btnH22);
		btnH22.setOnClickListener(this);
		btnH23 = (Button) findViewById(R.id.btnH23);
		btnH23.setOnClickListener(this);
		
		btnM00 = (Button) findViewById(R.id.btnM00);
		btnM00.setOnClickListener(this);
		btnM05 = (Button) findViewById(R.id.btnM05);
		btnM05.setOnClickListener(this);
		btnM10 = (Button) findViewById(R.id.btnM10);
		btnM10.setOnClickListener(this);
		btnM15 = (Button) findViewById(R.id.btnM15);
		btnM15.setOnClickListener(this);
		btnM20 = (Button) findViewById(R.id.btnM20);
		btnM20.setOnClickListener(this);
		btnM25 = (Button) findViewById(R.id.btnM25);
		btnM25.setOnClickListener(this);
		btnM30 = (Button) findViewById(R.id.btnM30);
		btnM30.setOnClickListener(this);
		btnM35 = (Button) findViewById(R.id.btnM35);
		btnM35.setOnClickListener(this);
		btnM40 = (Button) findViewById(R.id.btnM40);
		btnM40.setOnClickListener(this);
		btnM45 = (Button) findViewById(R.id.btnM45);
		btnM45.setOnClickListener(this);
		btnM50 = (Button) findViewById(R.id.btnM50);
		btnM50.setOnClickListener(this);
		btnM55 = (Button) findViewById(R.id.btnM55);
		btnM55.setOnClickListener(this);
	}

	public void onClick(View v) {

		if (v == btnH00) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH01) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH02) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH03) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH04) {
			assignButtonTextToField(v, 0);;
			return;
		}
		
		if (v == btnH05) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH06) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH07) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH08) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH09) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH10) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH11) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH12) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH13) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH14) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH15) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH16) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH17) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH18) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH19) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH20) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH21) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH22) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnH23) {
			assignButtonTextToField(v, 0);
			return;
		}
		
		if (v == btnM00) {
			assignButtonTextToField(v, 1);
			return;
		}
		
		if (v == btnM05) {
			assignButtonTextToField(v, 1);
			return;
		}
		
		if (v == btnM10) {
			assignButtonTextToField(v, 1);
			return;
		}
		
		if (v == btnM15) {
			assignButtonTextToField(v, 1);
			return;
		}
		
		if (v == btnM20) {
			assignButtonTextToField(v, 1);
			return;
		}
		
		if (v == btnM25) {
			assignButtonTextToField(v, 1);
			return;
		}
		
		if (v == btnM30) {
			assignButtonTextToField(v, 1);
			return;
		}
		
		if (v == btnM35) {
			assignButtonTextToField(v, 1);
			return;
		}
		
		if (v == btnM40) {
			assignButtonTextToField(v, 1);
			return;
		}
		
		if (v == btnM45) {
			assignButtonTextToField(v, 1);
			return;
		}
		
		if (v == btnM50) {
			assignButtonTextToField(v, 1);
			return;
		}
		
		if (v == btnM55) {
			assignButtonTextToField(v, 1);
			return;
		}
	}
}
