package aero.zztrop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ScreenCockpitMainTkfLdg extends FragmentActivity implements
		OnClickListener,
		OnItemClickListener,
		OnFocusChangeListener,
		SeekBar.OnSeekBarChangeListener {

	boolean isShowCloseDialog = false;

	private Spinner spnrPause;
	private EditText etStart;
	private EditText etEnd;
	private EditText etFlightTime;
	private SeekBar sbAfterTkf, sbBeforeLdg;
	private TextView tvAfterTkfValue, tvBeforeLdgValue, tvSlideAfterTkf,
			tvSlideBeforeLdg;
	private long xTime = 0; // for patterns with a x value (forced by user)

	private boolean flagLaunch = true; // prevent timescreen display at startup

	private Button btnHelp, btnSettings;
	private ListView lvPatterns;

	private static final int TIME_TYPE_START = 101;
	private static final int TIME_TYPE_END = 102;
	private static final int TIME_TYPE_FLTTIME = 103;

	private static final int TIME_TYPE_X = 110;
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
		setContentView(R.layout.screen_cockpit_main_tkfldg);
		initComponents();
		// this is to prevent non conform entries in the
		// default focused editText via the keyboard
		btnHelp.requestFocus();
	}

	private void initComponents() {

		spnrPause = (Spinner) findViewById(R.id.spnrPause);

		// customize the spinner look
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.spnrPauseValues,
				R.layout.spinner_layout_16);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spnrPause.setAdapter(adapter);

		// load user preferences
		SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
				MODE_PRIVATE);
		spnrPause.setSelection(prefs.getInt("pauseTimeCockpit", 2));

		etStart = (EditText) findViewById(R.id.etStart);
		etStart.setOnFocusChangeListener(this);
		etStart.setOnClickListener(this);
		etStart.setInputType(0);

		etEnd = (EditText) findViewById(R.id.etEnd);
		etEnd.setOnFocusChangeListener(this);
		etEnd.setOnClickListener(this);
		etEnd.setInputType(0);

		etFlightTime = (EditText) findViewById(R.id.etFltTime);
		etFlightTime.setOnFocusChangeListener(this);
		etFlightTime.setOnClickListener(this);
		etFlightTime.setInputType(0);

		tvAfterTkfValue = (TextView) findViewById(R.id.tvAfterTkfValue);
		tvBeforeLdgValue = (TextView) findViewById(R.id.tvBeforeLdgValue);

		// IMPORTANT: set the bar progress BEFORE setting the changeListener
		sbAfterTkf = (SeekBar) findViewById(R.id.sbAfterTkf);
		sbAfterTkf.setProgress(prefs.getInt("afterTkf", 0));
		tvAfterTkfValue.setText("" + prefs.getInt("afterTkf", 0) + "'");
		sbAfterTkf.setOnSeekBarChangeListener(this);
		sbBeforeLdg = (SeekBar) findViewById(R.id.sbBeforeLdg);
		sbBeforeLdg.setProgress(prefs.getInt("beforeLdg", 0));
		tvBeforeLdgValue.setText("" + prefs.getInt("beforeLdg", 0) + "'");
		sbBeforeLdg.setOnSeekBarChangeListener(this);

		tvSlideAfterTkf = (TextView) findViewById(R.id.tvSlideAfterTkf);
		if (sbAfterTkf.getProgress() != 0)
			tvSlideAfterTkf.setVisibility(View.INVISIBLE);
		else
			tvSlideAfterTkf.setVisibility(View.VISIBLE);

		tvSlideBeforeLdg = (TextView) findViewById(R.id.tvSlideBeforeLdg);
		if (sbBeforeLdg.getProgress() != 0)
			tvSlideBeforeLdg.setVisibility(View.INVISIBLE);
		else
			tvSlideBeforeLdg.setVisibility(View.VISIBLE);
		
		btnHelp = (Button) findViewById(R.id.btnHelp);
		btnHelp.setOnClickListener(this);

		btnSettings = (Button) findViewById(R.id.btnSettings);
		btnSettings.setOnClickListener(this);

		lvPatterns = (ListView) findViewById(R.id.lv_patterns);
		adapter = new ArrayAdapter<CharSequence>(
				this, R.layout.row_pattern_cockpit, R.id.tvPattern,
				getDisplayablePatterns());
		lvPatterns.setAdapter(adapter);
		lvPatterns.setOnItemClickListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		// Save user preferences
		SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("pauseTimeCockpit", spnrPause.getSelectedItemPosition());
		editor.putInt("afterTkf", sbAfterTkf.getProgress());
		editor.putInt("beforeLdg", sbBeforeLdg.getProgress());
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
		
		if (sbAfterTkf.getProgress() != 0)
			tvSlideAfterTkf.setVisibility(View.INVISIBLE);
		else
			tvSlideAfterTkf.setVisibility(View.VISIBLE);

		if (sbBeforeLdg.getProgress() != 0)
			tvSlideBeforeLdg.setVisibility(View.INVISIBLE);
		else
			tvSlideBeforeLdg.setVisibility(View.VISIBLE);
	}

	public void onClick(View v) {

		if (v == etStart) {
			displayKeypad(TIME_TYPE_START, null,
					getString(R.string.takeoff_time));
		}

		if (v == etEnd) {
			displayKeypad(TIME_TYPE_END, null, getString(R.string.landing_time));
		}

		if (v == etFlightTime) {
			displayKeypad(TIME_TYPE_FLTTIME, null,
					getString(R.string.flight_time));
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
			displayKeypad(TIME_TYPE_START, null,
					getString(R.string.takeoff_time));
			return;
		}

		if (v == etEnd && hasFocus == true) {
			displayKeypad(TIME_TYPE_END, null, getString(R.string.landing_time));
			return;
		}

		if (v == etFlightTime && hasFocus == true) {
			displayKeypad(TIME_TYPE_FLTTIME, null,
					getString(R.string.flight_time));
			return;
		}
	}

	private void processPattern(String pattern) {
		// test if both start and end fields have been properly set
		if (testFieldsReady() == false) {
			return;
		}

		// create an intent containing start, end, pause and service times
		Intent intent = new Intent(this, ScreenResultCockpit.class);
		intent.putExtra("pattern", pattern);
		intent.putExtra("time_start", etStart.getText().toString());
		intent.putExtra("time_end", etEnd.getText().toString());
		intent.putExtra("after_tkf", sbAfterTkf.getProgress());
		intent.putExtra("before_ldg", sbBeforeLdg.getProgress());
		intent.putExtra("pause", spnrPause.getSelectedItemPosition() * 5);
		intent.putExtra("x_time", xTime);
		startActivity(intent);
	}

	private boolean testFieldsReady() {
		if (!Utils.stringIsValidTime(etStart.getText().toString())) {
			Toast.makeText(this, R.string.toastInvalidStartTimeFormat,
					Toast.LENGTH_LONG).show();
			return false;
		}

		if (!Utils.stringIsValidTime(etEnd.getText().toString())) {
			Toast.makeText(this, R.string.toastInvalidEndTimeFormat,
					Toast.LENGTH_LONG).show();
			return false;
		}

		return true;
	}

	private void displayKeypad(int timeType, String pattern,
			String timeSelectionTitle) {
		Intent intent = null;

		// if TIME_TYPE_X, force KeyPadService
		if (timeType == TIME_TYPE_X) {
			intent = new Intent(this, ScreenTimeKeypadService.class);
			intent.putExtra("timeSelectionTitle", timeSelectionTitle);
			intent.putExtra("pattern", pattern);

			startActivityForResult(intent, timeType);
			return;
		}

		// first get the prefs to know wich kind of
		// time selection screen the user wants
		SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
				MODE_PRIVATE);

		// create a new activity to display the choices and start it
		// according to the result of the switch
		switch (prefs.getInt("timeSelection", Utils.SETTINGS_KEYPAD)) {
		case Utils.SETTINGS_FASTPAD:
			intent = new Intent(this, ScreenTimeFastpad.class);
			break;

		case Utils.SETTINGS_KEYPAD:
			intent = new Intent(this, ScreenTimeKeypad.class);
			intent.putExtra("timeSelectionTitle", timeSelectionTitle);
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
				SharedPreferences prefs = getSharedPreferences(
						Utils.SHARED_PREFS_NAME, MODE_PRIVATE);
				// reset listview adapter cause pattern list may have changed
				lvPatterns.setAdapter(new ArrayAdapter<CharSequence>(
						this, R.layout.row_pattern_cockpit, R.id.tvPattern,
						getDisplayablePatterns()));

				// test if work mode has changed
				int mode = prefs
						.getInt(Utils.PREFS_STR_START_MODE,
								Utils.START_MODE_COCKPIT2);

				if (mode == Utils.START_MODE_COCKPIT1) {
					// display the main cockpit1 screen
					Intent intent = null;
					intent = new Intent(this, ScreenCockpitMainStrEnd.class);
					startActivity(intent);
				}

				if (mode == Utils.START_MODE_CABIN) {
					// display the main cabin screen
					Intent intent = null;
					intent = new Intent(this, ScreenCabinMain.class);
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
				updateTextFields(TIME_TYPE_START);
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
				updateTextFields(TIME_TYPE_END);
			}
		}

		if (requestCode == TIME_TYPE_FLTTIME) {

			Calendar calTime = Calendar
					.getInstance(TimeZone.getTimeZone("UTC"));

			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			if (resultCode == RESULT_OK) {
				calTime.setTimeInMillis(data.getLongExtra("time", 0));
				etFlightTime.setText(sdf.format(calTime.getTime()));
				updateTextFields(TIME_TYPE_FLTTIME);
			}
		}

		if (requestCode == TIME_TYPE_X) {

			Calendar calTime = Calendar
					.getInstance(TimeZone.getTimeZone("UTC"));

			if (resultCode == RESULT_OK) {
				calTime.setTimeInMillis(data.getLongExtra("time", 0));
				xTime = calTime.getTimeInMillis();
				Bundle bundle = data.getExtras();
				processPattern(bundle.getString("pattern"));
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {

		TextView textView = (TextView) v.findViewById(R.id.tvPattern);
		String pattern = textView.getText().toString();
		if (pattern.contains("x")) {
			displayKeypad(TIME_TYPE_X, pattern,
					getString(R.string.enter_x_valeur));
		} else
			processPattern(textView.getText().toString());
	}

	private String[] getDisplayablePatterns() {
		// open user's preferences
		SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
				MODE_PRIVATE);
		// scan the list of available patterns
		String[] source = getResources().getStringArray(
				R.array.patterns_cockpit);
		ArrayList<String> displayablePatterns = new ArrayList<String>();
		for (String s : source) {
			if (prefs.getBoolean(s, true)) {
				displayablePatterns.add(s);
			}
		}
		// copy arraylist in string array
		String[] patterns = new String[displayablePatterns.size()];
		for (int i = 0; i < displayablePatterns.size(); i++) {
			patterns[i] = displayablePatterns.get(i);
		}
		return patterns;
	}

	private void updateTextFields(int type) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		long d1 = 0;
		long d2 = 0;

		switch (type) {
		case TIME_TYPE_START:
			if (!etFlightTime.getText().toString().equals("")) {
				try {
					d1 = sdf.parse(etStart.getText().toString()).getTime();
					d2 = sdf.parse(etFlightTime.getText().toString()).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}

				cal.setTimeInMillis(d1 + d2);
				etEnd.setText(sdf.format(cal.getTime()));
			}

			if (!etEnd.getText().toString().equals("")) {
				try {
					d1 = sdf.parse(etStart.getText().toString()).getTime();
					d2 = sdf.parse(etEnd.getText().toString()).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (d2 < d1) {
					Calendar c = Calendar.getInstance(TimeZone
							.getTimeZone("UTC"));
					c.setTimeInMillis(d2);
					c.add(Calendar.DAY_OF_MONTH, 1);
					d2 = c.getTimeInMillis();
				}
				cal.setTimeInMillis(d2 - d1);
				etFlightTime.setText(sdf.format(cal.getTime()));
			}

			break;

		case TIME_TYPE_FLTTIME:
			if (!etStart.getText().toString().equals("")) {
				try {
					d1 = sdf.parse(etStart.getText().toString()).getTime();
					d2 = sdf.parse(etFlightTime.getText().toString()).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				cal.setTimeInMillis(d1 + d2);
				etEnd.setText(sdf.format(cal.getTime()));
			}

			if (etStart.getText().toString().equals("")
					&& !etEnd.getText().toString().equals("")) {
				try {
					d1 = sdf.parse(etFlightTime.getText().toString()).getTime();
					d2 = sdf.parse(etEnd.getText().toString()).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				cal.setTimeInMillis(d2 - d1);
				etStart.setText(sdf.format(cal.getTime()));
			}
			break;

		case TIME_TYPE_END:
			if (!etStart.getText().toString().equals("")) {
				try {
					d1 = sdf.parse(etStart.getText().toString()).getTime();
					d2 = sdf.parse(etEnd.getText().toString()).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (d2 < d1) {
					Calendar c = Calendar.getInstance(TimeZone
							.getTimeZone("UTC"));
					c.setTimeInMillis(d2);
					c.add(Calendar.DAY_OF_MONTH, 1);
					d2 = c.getTimeInMillis();
				}
				cal.setTimeInMillis(d2 - d1);
				etFlightTime.setText(sdf.format(cal.getTime()));
			}

			if (etStart.getText().toString().equals("")
					&& !etFlightTime.getText().toString().equals("")) {
				try {
					d1 = sdf.parse(etFlightTime.getText().toString()).getTime();
					d2 = sdf.parse(etEnd.getText().toString()).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				cal.setTimeInMillis(d2 - d1);
				etStart.setText(sdf.format(cal.getTime()));
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (seekBar.equals(sbAfterTkf)) {
			tvAfterTkfValue.setText("" + progress + "'");
			// if value != 0, hide the hint
			if (progress != 0) {
				tvSlideAfterTkf.setVisibility(View.INVISIBLE);
			} else
				tvSlideAfterTkf.setVisibility(View.VISIBLE);
		}
		if (seekBar.equals(sbBeforeLdg)) {
			tvBeforeLdgValue.setText("" + progress + "'");
			// if value != 0, hide the hint
			if (progress != 0) {
				tvSlideBeforeLdg.setVisibility(View.INVISIBLE);
			} else
				tvSlideBeforeLdg.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

}