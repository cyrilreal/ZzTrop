package aero.zztrop;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ToggleButton;

public class ScreenResultCabin extends Activity implements OnClickListener {

	private ListView lvResults;
	private TimePairCabinAdapter m_adapter;

	private Button btnResultsOK;
	private ToggleButton tbtnRound;
	private Button btnShiftPlus, btnShiftMinus;
	private Button btnDurationPlus, btnDurationMinus;

	private ZzEngineCabin zzEngCab;

	SharedPreferences prefs;

	protected void onCreate(Bundle savedInstanceState) {
		Utils.applySharedTheme(this);
		super.onCreate(savedInstanceState);
		if (!Utils.isTablet(getApplicationContext())) {
			// set phones to portrait;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.screen_result_cabin);
		prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME, MODE_PRIVATE);
		initComponents();

		zzEngCab.computeRest();
		if (tbtnRound.isChecked()) {
			zzEngCab.roundTimes();
		}
		// prepare the list to be displayed
		populateList();
	}

	@Override
	protected void onStop() {
		super.onStop();

		// Save user preferences

		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("startupMode", 0); // 0 = cockpit mode
		editor.putBoolean("roundTimes", tbtnRound.isChecked());
		editor.commit();
	}

	private void initComponents() {

		zzEngCab = new ZzEngineCabin();
		// load engine with passed parameters
		Bundle bundle = this.getIntent().getExtras();
		zzEngCab.setPattern(bundle.getString("pattern"));
		zzEngCab.setCalStart(bundle.getString("time_start"));
		zzEngCab.setCalEnd(bundle.getString("time_end"));
		zzEngCab.setPause(bundle.getInt("pause"));
		// because no simple service in RRS pattern
		if (bundle.getString("service") != null) {
			zzEngCab.setService(bundle.getString("service"));
		}
		zzEngCab.setServiceLast(bundle.getString("service_last"));
		zzEngCab.setPauseAfterService(prefs.getBoolean("pauseAfterService",
				false));

		btnResultsOK = (Button) findViewById(R.id.btnResultsOK);
		btnResultsOK.setOnClickListener(this);
		btnShiftMinus = (Button) findViewById(R.id.btnShiftMinus);
		btnShiftMinus.setOnClickListener(this);
		btnShiftPlus = (Button) findViewById(R.id.btnShiftPlus);
		btnShiftPlus.setOnClickListener(this);
		btnDurationMinus = (Button) findViewById(R.id.btnDurationMinus);
		btnDurationMinus.setOnClickListener(this);
		btnDurationPlus = (Button) findViewById(R.id.btnDurationPlus);
		btnDurationPlus.setOnClickListener(this);
		tbtnRound = (ToggleButton) findViewById(R.id.tbtnRound);
		tbtnRound.setOnClickListener(this);

		tbtnRound.setChecked(prefs.getBoolean("roundTimes", false));

		lvResults = (ListView) findViewById(R.id.lvResults);
		// add a header to the list
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup header = (ViewGroup) inflater.inflate(R.layout.result_header,
				lvResults, false);
		lvResults.addHeaderView(header, null, false);
	}

	private void populateList() {

		m_adapter = new TimePairCabinAdapter(this, 0, // the view is defined in
														// the TimePairAdapter
														// class
				zzEngCab.getAlTimePairs());

		lvResults.setAdapter(m_adapter);

	}

	@Override
	public void onClick(View v) {
		if (v == btnResultsOK) {
			finish();
		}

		if (v == tbtnRound) {
			if (tbtnRound.isChecked()) {
				zzEngCab.roundTimes();
				populateList();
			} else {
				zzEngCab.computeRest();
				populateList();
			}
		}

		if (v == btnShiftMinus) {
			if (tbtnRound.isChecked()) {
				zzEngCab.shiftTimes(-5);
			} else
				zzEngCab.shiftTimes(-1);

			populateList();
		}

		if (v == btnShiftPlus) {
			if (tbtnRound.isChecked()) {
				zzEngCab.shiftTimes(5);
			} else
				zzEngCab.shiftTimes(1);

			populateList();
		}

		if (v == btnDurationMinus) {
			if (tbtnRound.isChecked()) {
				zzEngCab.adjustTimes(-5);
			} else
				zzEngCab.adjustTimes(-1);

			populateList();
		}

		if (v == btnDurationPlus) {
			if (tbtnRound.isChecked()) {
				zzEngCab.adjustTimes(5);
			} else
				zzEngCab.adjustTimes(1);

			populateList();
		}
	}
}
