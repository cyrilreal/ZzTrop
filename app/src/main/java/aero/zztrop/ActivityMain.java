package aero.zztrop;

//TODO: consider one class only for ScreenResult
//TODO:	consider one class only for ScreenCabin (with appropriate xml file
//		loaded according to the number of services...)
//TODO: Alarm at rest end

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class ActivityMain extends Activity {

	int startMode; // 1 = cockpit , 2 = cockpit (based on tkf/ldg), 3 = cabin

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// load user preferences
		SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
				MODE_PRIVATE);
		startMode = prefs.getInt(Utils.PREFS_STR_START_MODE,
				Utils.START_MODE_COCKPIT1);

		// create an intent that will be instanciated following the startup mode
		Intent intent;

		switch (startMode) {
		case Utils.START_MODE_COCKPIT1:
			intent = new Intent(this, aero.zztrop.ScreenCockpitMainStrEnd.class);
			startActivity(intent);
			break;

		case Utils.START_MODE_COCKPIT2:
			intent = new Intent(this, aero.zztrop.ScreenCockpitMainTkfLdg.class);
			startActivity(intent);
			break;

		case Utils.START_MODE_CABIN:
			intent = new Intent(this, aero.zztrop.ScreenCabinMain.class);
			startActivity(intent);
			break;
		}

		// Main activity is no more necessary, let's terminate it !
		finish();
	}
}
