package aero.zztrop;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ScreenCabin2S extends FragmentActivity implements OnClickListener,
		OnFocusChangeListener {

	boolean isShowCloseDialog = false;

	private TextView tvPatternTitle;
	private Spinner spnrPause;
	private EditText etStart;
	private EditText etEnd;
	private EditText etService;
	private EditText etServiceLast;
	private String pattern;
	private boolean flagLaunch = true; // to prevent keypad display at startup

	private Button btnCalculate;
	private Button btnHelp;
	private Button btnSettings;

	protected static final int DIALOG_CLOSE_APPLICATION = 0;

	private static final int TIME_TYPE_START = 101;
	private static final int TIME_TYPE_END = 102;
	private static final int TIME_TYPE_SERVICE = 103;
	private static final int TIME_TYPE_SERVICE_LAST = 104;
	private static final int REQUEST_SETTINGS = 53;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Utils.applySharedTheme(this);
		super.onCreate(savedInstanceState);
		if (!Utils.isTablet(getApplicationContext())) {
			// set phones to portrait;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.screen_cabin_2s);
		// get data from cabin main screen activity
		Bundle extras = getIntent().getExtras();
		pattern = extras.getString("pattern");

		initComponents();
		// this is to prevent non conform entries in the
		// default focused editText via the keyboard
		btnHelp.requestFocus();
	}

	private void initComponents() {

		spnrPause = (Spinner) findViewById(R.id.spnrPause);

		// customize the spinner's look
		ArrayAdapter<CharSequence> adapterPause = ArrayAdapter
				.createFromResource(this, R.array.spnrPauseValues,
						android.R.layout.simple_spinner_item);
		adapterPause
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spnrPause.setAdapter(adapterPause);

		// load user preferences
		SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
				MODE_PRIVATE);
		spnrPause.setSelection(prefs.getInt("pauseTimeCabin", 2));

		tvPatternTitle = (TextView) findViewById(R.id.tvPatternTitle2S);
		tvPatternTitle.setText(pattern);

		etStart = (EditText) findViewById(R.id.etStart);
		etStart.setOnFocusChangeListener(this);
		etStart.setOnClickListener(this);
		etStart.setInputType(0);

		etEnd = (EditText) findViewById(R.id.etEnd);
		etEnd.setOnFocusChangeListener(this);
		etEnd.setOnClickListener(this);
		etEnd.setInputType(0);

		etService = (EditText) findViewById(R.id.etService);
		etService.setOnFocusChangeListener(this);
		etService.setOnClickListener(this);
		etService.setInputType(0);

		etServiceLast = (EditText) findViewById(R.id.etServiceLast);
		etServiceLast.setOnFocusChangeListener(this);
		etServiceLast.setOnClickListener(this);
		etServiceLast.setInputType(0);

		btnHelp = (Button) findViewById(R.id.btnHelp);
		btnHelp.setOnClickListener(this);

		btnSettings = (Button) findViewById(R.id.btnSettings);
		btnSettings.setOnClickListener(this);

		btnCalculate = (Button) findViewById(R.id.btnCalculate);
		btnCalculate.setOnClickListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		// Save user preferences
		SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("pauseTimeCabin", spnrPause.getSelectedItemPosition());
		editor.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isShowCloseDialog) {
			isShowCloseDialog = false;
			FragmentManager fragmentManager = getSupportFragmentManager();
			DialogFragment df = new CloseDialogFragment();
			df.show(fragmentManager, "close_frag_tag");
		}
	}

	public void onClick(View v) {

		if (v == etStart) {
			displayKeypad(TIME_TYPE_START, getString(R.string.first_rest_start));
		}

		if (v == etEnd) {
			displayKeypad(TIME_TYPE_END, getString(R.string.last_service_end));
		}

		if (v == etService) {
			displayKeypad(TIME_TYPE_SERVICE,
					getString(R.string.service_duration));
		}

		if (v == etServiceLast) {
			displayKeypad(TIME_TYPE_SERVICE_LAST,
					getString(R.string.last_service_duration));
		}

		if (v == btnCalculate) {
			processPattern(pattern);
		}

		if (v == btnHelp) {
			// create a new activity to display the help screen
			Intent intent = new Intent(this, ScreenHelp.class);
			startActivity(intent);
		}

		if (v == btnSettings) {
			// create a new activity to display the help screen
			Intent intent = new Intent(this, ScreenSettings.class);
			startActivityForResult(intent, REQUEST_SETTINGS);
		}
	}

	public void onFocusChange(View v, boolean hasFocus) {
		// prevent timepick from appearing at startup
		if (flagLaunch) {
			flagLaunch = false;
			return;
		}

		if (v == etStart && hasFocus == true) {
			displayKeypad(TIME_TYPE_START, getString(R.string.first_rest_start));
		}

		if (v == etEnd && hasFocus == true) {
			displayKeypad(TIME_TYPE_END, getString(R.string.last_service_end));
		}

		if (v == etService && hasFocus == true) {
			displayKeypad(TIME_TYPE_SERVICE,
					getString(R.string.service_duration));
		}

		if (v == etServiceLast && hasFocus == true) {
			displayKeypad(TIME_TYPE_SERVICE_LAST,
					getString(R.string.last_service_duration));
		}
	}

	private void processPattern(String pattern) {
		// test if both start and end fields have been properly set
		if (!Utils.stringIsValidTime(etStart.getText().toString())) {
			Toast.makeText(this, R.string.toastInvalidStartTimeFormat,
					Toast.LENGTH_LONG).show();
			return;
		}
		if (!Utils.stringIsValidTime(etEnd.getText().toString())) {
			Toast.makeText(this, R.string.toastInvalidEndTimeFormat,
					Toast.LENGTH_LONG).show();
			return;
		}
		if (!Utils.stringIsValidTime(etServiceLast.getText().toString())) {
			Toast.makeText(this, R.string.toastInvalidServiceTimeFormat,
					Toast.LENGTH_LONG).show();
			return;
		}

		// create an intent containing start, end,
		// pause and service times
		Intent intent = new Intent(this, ScreenResultCabin.class);
		intent.putExtra("pattern", pattern);
		intent.putExtra("time_start", etStart.getText().toString());
		intent.putExtra("time_end", etEnd.getText().toString());
		intent.putExtra("pause", spnrPause.getSelectedItemPosition() * 5);
		intent.putExtra("service", etService.getText().toString());
		intent.putExtra("service_last", etServiceLast.getText().toString());
		startActivity(intent);
	}

	/**
	 * Display the keypad for time/duration selection timeType is either start
	 * time, end time, or service duration keypadTitle is the string value that
	 * will indicate the user wich kind of time/duration he has to enter
	 */
	private void displayKeypad(int timeType, String timeSelectionTitle) {
		// first get the prefs to know wich kind of
		// time selection screen the user wants
		SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
				MODE_PRIVATE);

		// create a new activity to display the choices and start it
		// according to the result of the switch
		Intent intent = null;
		switch (prefs.getInt("timeSelection", Utils.SETTINGS_KEYPAD)) {
		case Utils.SETTINGS_FASTPAD:
			intent = new Intent(this, ScreenTimeFastpad.class);
			break;

		case Utils.SETTINGS_KEYPAD:
			if (timeSelectionTitle.equals(getString(R.string.service_duration))
					|| timeSelectionTitle
							.equals(getString(R.string.last_service_duration))) {
				intent = new Intent(this, ScreenTimeKeypadService.class);
				intent.putExtra("timeSelectionTitle", timeSelectionTitle);
			} else {
				intent = new Intent(this, ScreenTimeKeypad.class);
				intent.putExtra("timeSelectionTitle", timeSelectionTitle);
			}

			break;

		default:
			break;
		}
		startActivityForResult(intent, timeType);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_SETTINGS) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				boolean bln = bundle.getBoolean("theme_has_changed");
				if (bln == true) {
					// use a boolean because of ICS bug with support package
					isShowCloseDialog = true;
				}
				// test if work mode has changed (app is in cabin mode)
				SharedPreferences prefs = getSharedPreferences(
						Utils.SHARED_PREFS_NAME, MODE_PRIVATE);
				int mode = prefs
						.getInt(Utils.PREFS_STR_START_MODE,
								Utils.START_MODE_CABIN);
				if (mode == Utils.START_MODE_COCKPIT1) {
					// display the main cabin screen
					Intent intent = null;
					intent = new Intent(this, ScreenCockpitMainStrEnd.class);
					startActivity(intent);
				}

				if (mode == Utils.START_MODE_COCKPIT2) {
					// display the main cockpit2 screen
					Intent intent = null;
					intent = new Intent(this, ScreenCockpitMainTkfLdg.class);
					startActivity(intent);
				}
			}
		}

		if (requestCode == TIME_TYPE_START) {

			Calendar calTime = Calendar
					.getInstance(TimeZone.getTimeZone("UTC"));

			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			if (resultCode == RESULT_OK) {
				calTime.setTimeInMillis(data.getLongExtra("time", 0));
				etStart.setText(sdf.format(calTime.getTime()));
			}
		}

		if (requestCode == TIME_TYPE_END) {

			Calendar calTime = Calendar
					.getInstance(TimeZone.getTimeZone("UTC"));

			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			if (resultCode == RESULT_OK) {
				calTime.setTimeInMillis(data.getLongExtra("time", 0));
				etEnd.setText(sdf.format(calTime.getTime()));
			}
		}

		if (requestCode == TIME_TYPE_SERVICE) {

			Calendar calTime = Calendar
					.getInstance(TimeZone.getTimeZone("UTC"));

			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			if (resultCode == RESULT_OK) {
				calTime.setTimeInMillis(data.getLongExtra("time", 0));
				etService.setText(sdf.format(calTime.getTime()));
			}
		}

		if (requestCode == TIME_TYPE_SERVICE_LAST) {

			Calendar calTime = Calendar
					.getInstance(TimeZone.getTimeZone("UTC"));

			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			if (resultCode == RESULT_OK) {
				calTime.setTimeInMillis(data.getLongExtra("time", 0));
				etServiceLast.setText(sdf.format(calTime.getTime()));
			}
		}
	}
}