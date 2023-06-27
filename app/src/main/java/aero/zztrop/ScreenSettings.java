package aero.zztrop;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;

public class ScreenSettings extends Activity implements OnClickListener {

	private Button btnPatternsFilter, btnSettingsOK;
	private RadioGroup rgModeSelection;
	private RadioGroup rgTimeSelection;
	private RadioGroup rgThemeSelection;
	private CheckBox cbPauseAfterService;

	private int themeResId; // hold the theme loaded at startup to see if user
							// changed it
	private boolean themeHasChanged = false;

	protected void onCreate(Bundle savedInstanceState) {
		Utils.applySharedTheme(this);
		super.onCreate(savedInstanceState);
		if (!Utils.isTablet(getApplicationContext())) {
			// set phones to portrait;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.screen_settings);
		initComponents();
	}

	private void initComponents() {
		rgModeSelection = (RadioGroup) findViewById(R.id.rgModeSelection);
		rgModeSelection.clearCheck();
		rgTimeSelection = (RadioGroup) findViewById(R.id.rgTimeSelection);
		rgTimeSelection.clearCheck();
		rgThemeSelection = (RadioGroup) findViewById(R.id.rgThemeSelection);
		rgThemeSelection.clearCheck();
		cbPauseAfterService = (CheckBox) findViewById(R.id.cbPauseAfterService);
		btnPatternsFilter = (Button) findViewById(R.id.btnPatternFilter);
		btnPatternsFilter.setOnClickListener(this);
		btnSettingsOK = (Button) findViewById(R.id.btnSettingsOK);
		btnSettingsOK.setOnClickListener(this);
		
		assignValuesFromPrefs();
	}

	@Override
	public void onClick(View v) {
		if (v.equals(btnPatternsFilter)) {
			Intent intent = new Intent(this, ScreenSettingsPatterns.class);
			boolean cockpit_mode;
			// get computation mode
			if (rgModeSelection.getCheckedRadioButtonId() != R.id.rbtnCabinMode)
				cockpit_mode = true;
			else
				cockpit_mode = false;
			// add mode in intent
			intent.putExtra("cockpit_mode", cockpit_mode);
			startActivity(intent);
		}
		
		if (v.equals(btnSettingsOK)) {
			updatePrefsFromValues();
			Bundle b = new Bundle();
			b.putBoolean("theme_has_changed", themeHasChanged);
			Intent mIntent = new Intent();
			mIntent.putExtras(b);
			setResult(RESULT_OK, mIntent);
			finish();
		}
	}

	private void assignValuesFromPrefs() {
		// set the correct radiobutton on with parameters from preferences
		SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
				MODE_PRIVATE);

		// deal with WorkMode radiobuttons
		switch (prefs.getInt(Utils.PREFS_STR_START_MODE,
				Utils.START_MODE_COCKPIT1)) {
		case Utils.START_MODE_COCKPIT1:
			rgModeSelection.check(R.id.rbtnCockpitMode1);
			break;
			
		case Utils.START_MODE_COCKPIT2:
			rgModeSelection.check(R.id.rbtnCockpitMode2);
			break;

		case Utils.START_MODE_CABIN:
			rgModeSelection.check(R.id.rbtnCabinMode);
			break;

		default:
			break;
		}

		// deal with TimeSelection radiobuttons
		switch (prefs.getInt("timeSelection", Utils.SETTINGS_KEYPAD)) {
		case Utils.SETTINGS_FASTPAD:
			rgTimeSelection.check(R.id.rbtnTimeSelectionFastpad);
			break;

		case Utils.SETTINGS_KEYPAD:
			rgTimeSelection.check(R.id.rbtnTimeSelectionKeypad);
			break;

		default:
			break;
		}

		// deal with ThemeSelection radiobuttons
		switch (prefs.getInt(Utils.PREFS_THEME_RESID_ID,
				Utils.DEFAULT_THEME_RESID)) {

		case R.style.airbus:
			rgThemeSelection.check(R.id.rbtnThemeAirbus);
			themeResId = R.style.airbus;
			break;

		case R.style.boeing:
			rgThemeSelection.check(R.id.rbtnThemeBoeing);
			themeResId = R.style.boeing;
			break;

		case R.style.boeing_grey:
			rgThemeSelection.check(R.id.rbtnThemeBoeingGrey);
			themeResId = R.style.boeing_grey;
			break;

		default:
			rgThemeSelection.check(R.id.rbtnThemeBoeingGrey);
			themeResId = R.style.boeing_grey;
			break;
		}

		// deal with miscellaneous
		cbPauseAfterService.setChecked(prefs.getBoolean("pauseAfterService",
				false));
	}

	private void updatePrefsFromValues() {
		// Save user preferences
		SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();

		// getting the checked mode selection method
		switch (rgModeSelection.getCheckedRadioButtonId()) {
		case R.id.rbtnCockpitMode1:
			editor.putInt(Utils.PREFS_STR_START_MODE,
					Utils.START_MODE_COCKPIT1);
			break;
			
		case R.id.rbtnCockpitMode2:
			editor.putInt(Utils.PREFS_STR_START_MODE,
					Utils.START_MODE_COCKPIT2);
			break;

		case R.id.rbtnCabinMode:
			editor.putInt(Utils.PREFS_STR_START_MODE,
					Utils.START_MODE_CABIN);
			break;

		default:
			break;
		}

		// getting the checked time selection method
		switch (rgTimeSelection.getCheckedRadioButtonId()) {
		case R.id.rbtnTimeSelectionFastpad:
			editor.putInt("timeSelection", Utils.SETTINGS_FASTPAD);
			break;

		case R.id.rbtnTimeSelectionKeypad:
			editor.putInt("timeSelection", Utils.SETTINGS_KEYPAD);
			break;

		default:
			break;
		}

		// getting the checked theme selection method
		switch (rgThemeSelection.getCheckedRadioButtonId()) {
		case R.id.rbtnThemeAirbus:
			editor.putInt(Utils.PREFS_THEME_RESID_ID, R.style.airbus);
			if (themeResId != R.style.airbus) {
				themeHasChanged = true;
			}
			break;

		case R.id.rbtnThemeBoeing:
			editor.putInt(Utils.PREFS_THEME_RESID_ID, R.style.boeing);
			if (themeResId != R.style.boeing) {
				themeHasChanged = true;
			}
			break;

		case R.id.rbtnThemeBoeingGrey:
			editor.putInt(Utils.PREFS_THEME_RESID_ID, R.style.boeing_grey);
			if (themeResId != R.style.boeing_grey) {
				themeHasChanged = true;
			}
			break;

		default:
			break;
		}

		// getting miscellaneous states
		editor.putBoolean("pauseAfterService", cbPauseAfterService.isChecked());

		editor.apply();
	}
}
