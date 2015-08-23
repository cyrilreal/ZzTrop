package aero.zztrop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ScreenTimeKeypad extends Activity implements OnClickListener {

	protected Calendar calTime; // holds the time data to be sent back
	// set the date time format for user inputs
	protected SimpleDateFormat sdfTime = new SimpleDateFormat();

	private String pattern; //used only in cockpit mode

	// buttons declarations
	protected Button btn01;
	protected Button btn02;
	protected Button btn03;
	protected Button btn04;
	protected Button btn05;
	protected Button btn06;
	protected Button btn07;
	protected Button btn08;
	protected Button btn09;
	protected Button btn0;
	protected Button btnBKSP;
	protected Button btnClear;
	protected Button btnOK;

	protected EditText etTime;
	protected TextView tvKeypadTitle;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Utils.applySharedTheme(this);
		super.onCreate(savedInstanceState);
		if (!Utils.isTablet(getApplicationContext())) {
			// set phones to portrait;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.screen_time_keypad);

		initComponents();
	}

	protected void initComponents() {

		// instantiate and initialize calendar
		calTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calTime.setTimeInMillis(0);
		// prevent the date formatter from accepting invalid value
		// and set it to UTC reference
		sdfTime.setLenient(false);
		sdfTime.setTimeZone(TimeZone.getTimeZone("UTC"));

		etTime = (EditText) findViewById(R.id.etTime);
		etTime.setInputType(0);
		tvKeypadTitle = (TextView) findViewById(R.id.tvKeypadTitle);

		Bundle bundle = this.getIntent().getExtras();
		tvKeypadTitle.setText(bundle.getString("timeSelectionTitle"));
		pattern = bundle.getString("pattern");

		// Buttons initialization
		btn01 = (Button) this.findViewById(R.id.Button01);
		btn01.setOnClickListener(this);

		btn02 = (Button) this.findViewById(R.id.Button02);
		btn02.setOnClickListener(this);

		btn03 = (Button) this.findViewById(R.id.Button03);
		btn03.setOnClickListener(this);

		btn04 = (Button) this.findViewById(R.id.Button04);
		btn04.setOnClickListener(this);

		btn05 = (Button) this.findViewById(R.id.Button05);
		btn05.setOnClickListener(this);

		btn05 = (Button) this.findViewById(R.id.Button05);
		btn05.setOnClickListener(this);

		btn06 = (Button) this.findViewById(R.id.Button06);
		btn06.setOnClickListener(this);

		btn07 = (Button) this.findViewById(R.id.Button07);
		btn07.setOnClickListener(this);

		btn08 = (Button) this.findViewById(R.id.Button08);
		btn08.setOnClickListener(this);

		btn09 = (Button) this.findViewById(R.id.Button09);
		btn09.setOnClickListener(this);

		btn0 = (Button) this.findViewById(R.id.Button0);
		btn0.setOnClickListener(this);

		btnBKSP = (Button) this.findViewById(R.id.btnBKSP);
		btnBKSP.setOnClickListener(this);

		btnClear = (Button) this.findViewById(R.id.btnClear);
		btnClear.setOnClickListener(this);

		btnOK = (Button) findViewById(R.id.btnOK);
		btnOK.setOnClickListener(this);
	}

	public void onClick(View v) {

		// Buttons action
		if (v == btn01) {
			updateNumberField("1");
		}

		if (v == btn02) {
			updateNumberField("2");
		}

		if (v == btn03) {
			updateNumberField("3");
		}

		if (v == btn04) {
			updateNumberField("4");
		}

		if (v == btn05) {
			updateNumberField("5");
		}

		if (v == btn06) {
			updateNumberField("6");
		}

		if (v == btn07) {
			updateNumberField("7");
		}

		if (v == btn08) {
			updateNumberField("8");
		}

		if (v == btn09) {
			updateNumberField("9");
		}

		if (v == btn0) {
			updateNumberField("0");
		}

		if (v == btnBKSP) {
			etTime.setText(fieldBackspace(etTime.getText().toString()));
		}

		if (v == btnClear) {
			etTime.setText("");
		}

		if (v == btnOK) {
			if (updateCalTime(etTime.getText().toString()) == true) {
				sendDataBack();
			}
		}
	}

	/**
	 * Update the calTime property with a given String, wich can be in different
	 * formats (HH:mm, H:mm, mm)
	 */
	protected boolean updateCalTime(String s) {
		// set the timeformat according to the number of digits
		if (s.length() == 2) {
			sdfTime.setLenient(true); // to allow "90" for "1h30"
			sdfTime.applyPattern("mm");
		} else if (s.length() > 2) {
			sdfTime.setLenient(false);
			sdfTime.applyPattern("HH:mm");
		}

		// get the time value from edittext
		try {
			calTime.setTimeInMillis(sdfTime.parse(s).getTime());
		} catch (ParseException e) {
			displayTimeFormatErrorMessage();
			return false;
		}
		return true;
	}

	protected void displayTimeFormatErrorMessage() {
		// display the message
		Toast.makeText(this, R.string.toastInvalidTimeFormat, Toast.LENGTH_LONG)
				.show();
		// reset both edittext and calendar
		etTime.setText("");
		calTime.setTimeInMillis(0);
	}

	protected String fieldBackspace(String s) {
		// create a stringbuilder to play with our string
		StringBuilder sb = new StringBuilder(s);

		// first remove the ":" caracter if any
		if (sb.indexOf(":") != -1) {
			sb.deleteCharAt(sb.indexOf(":"));
		}
		
		// delete last number (if exist)
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}

		// add the ":" caracter at the proper position
		switch (sb.length()) {
		case 3:
			sb.insert(1, ":");
			break;

		case 4:
			sb.insert(2, ":");
			break;

		default:
			break;
		}
		return sb.toString();
	}

	protected void updateNumberField(String s) {
		// first remove the ":" caracter if any
		if (etTime.getText().toString().indexOf(":") != -1) {
			etTime.getText().delete(etTime.getText().toString().indexOf(":"),
					etTime.getText().toString().indexOf(":") + 1);
		}

		// add the typed number except if they are already 4
		if (etTime.getText().length() < 4) {
			etTime.setText(etTime.getText().toString() + s);
		}

		// add the ":" caracter at the proper index
		switch (etTime.getText().length()) {
		case 3:
			etTime.getText().insert(1, ":");
			break;

		case 4:
			etTime.getText().insert(2, ":");
			break;

		default:
			break;
		}
	}

	protected void sendDataBack() {
		// prepare data to be sent back
		Intent returnIntent = new Intent();
		returnIntent.putExtra("time", calTime.getTimeInMillis());
		returnIntent.putExtra("pattern", pattern);
		// send it
		setResult(RESULT_OK, returnIntent);
		finish();
	}
}
