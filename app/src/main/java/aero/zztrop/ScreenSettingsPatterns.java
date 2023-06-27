package aero.zztrop;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class ScreenSettingsPatterns extends Activity {

	private Button btnSettingsOK;
	private ListView lvPatternFilter;
	private PatternFilterAdapter adapter;
	private String[] patternArray;
	private PatternFilter[] arrayFilter;
	private boolean cockpit_mode;

	protected void onCreate(Bundle savedInstanceState) {
		Utils.applySharedTheme(this);
		super.onCreate(savedInstanceState);
		if (!Utils.isTablet(getApplicationContext())) {
			// set phones to portrait;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.screen_settings_patterns);
		Bundle extras = getIntent().getExtras();
		cockpit_mode = extras.getBoolean("cockpit_mode");
		initComponents();
	}

	private void initComponents() {
		// generate pattern array with all filters on true by default
		if (cockpit_mode)
			patternArray = getResources().getStringArray(R.array.patterns_cockpit);
		else
			patternArray = getResources().getStringArray(R.array.patterns_cabin);

		arrayFilter = new PatternFilter[patternArray.length];
		for (int i = 0; i < arrayFilter.length; i++) {
			arrayFilter[i] = new PatternFilter(patternArray[i], true);
		}
		
		// then call the prefs and update array
		assignValuesFromPrefs();

		lvPatternFilter = (ListView) findViewById(R.id.lvPatternsFilter);
		adapter = new PatternFilterAdapter(
				this,
				R.layout.row_pattern_filter,
				arrayFilter);
		lvPatternFilter.setAdapter(adapter);

		// deal with OK button
		btnSettingsOK = findViewById(R.id.btnSettingsOK);
		btnSettingsOK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				updatePrefsFromValues();
				finish();
			}
		});
	}

	private void assignValuesFromPrefs() {
		// set the correct radiobutton on with parameters from preferences
		SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
				MODE_PRIVATE);
		// scan the array of patterns
		for (int i = 0; i < patternArray.length; i++) {
			arrayFilter[i].setChecked(
					prefs.getBoolean(patternArray[i],
					true));
		}
	}

	private void updatePrefsFromValues() {
		// Save user preferences
		SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();

		// scan the list
		for (PatternFilter pf : arrayFilter) {
			editor.putBoolean(pf.getPattern(), pf.isChecked());
		}
		editor.commit();
	}
}
