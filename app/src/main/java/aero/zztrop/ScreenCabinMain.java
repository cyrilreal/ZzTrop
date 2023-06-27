package aero.zztrop;

import java.util.ArrayList;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ScreenCabinMain extends FragmentActivity implements OnClickListener,
		OnItemClickListener {

	boolean isShowCloseDialog = false;

	private static final int REQUEST_SETTINGS = 0;
	private ListView lvPatterns;
	private ArrayAdapter<CharSequence> adapter;
	private Button btnHelp, btnSettings;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Utils.applySharedTheme(this);
		super.onCreate(savedInstanceState);
		if (!Utils.isTablet(getApplicationContext())) {
			// set phones to portrait;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.screen_cabin_main);
		initComponents();
		// this is to prevent non conform entries in the
		// default focused editText via the keyboard
		btnHelp.requestFocus();
	}

	private void initComponents() {
		btnHelp = (Button) findViewById(R.id.btnHelp);
		btnHelp.setOnClickListener(this);

		btnSettings = (Button) findViewById(R.id.btnSettings);
		btnSettings.setOnClickListener(this);

		lvPatterns = (ListView) findViewById(R.id.lv_patterns);
		adapter = new ArrayAdapter<CharSequence>(
				this, R.layout.row_pattern_cabin, R.id.tvPattern,
				getDisplayablePatterns());
		lvPatterns.setAdapter(adapter);
		lvPatterns.setOnItemClickListener(this);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
		// get clicked pattern's name
		TextView textView = (TextView) v.findViewById(R.id.tvPattern);
		String pattern = textView.getText().toString();

		Intent intent;
		// check number of services in pattern and display appropriate screen
		int s = countMatchs("Srv", pattern) - countMatchs("(Srv)", pattern);

		if (s > 1) {
			intent = new Intent(this, ScreenCabin2S.class);
		} else {
			intent = new Intent(this, ScreenCabin1S.class);
		}
		intent.putExtra("pattern", pattern);
		startActivity(intent);
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
						this, R.layout.row_pattern_cabin, R.id.tvPattern,
						getDisplayablePatterns()));

				// test if work mode has changed
				int mode = prefs
						.getInt(Utils.PREFS_STR_START_MODE,
								Utils.START_MODE_CABIN);

				if (mode == Utils.START_MODE_COCKPIT1) {
					// display the main cockpit1 screen
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
	}

	private String[] getDisplayablePatterns() {
		// open user's preferences
		SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
				MODE_PRIVATE);
		// scan the list of available patterns
		String[] source = getResources().getStringArray(
				R.array.patterns_cabin);
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

	private int countMatchs(String s, String target) {
		int i = 0;
		int n = 0;
		
		while (target.indexOf(s, i) != -1) {
			i = target.indexOf(s, i) + 1;
			n++;
		}
		return n;
	}
}