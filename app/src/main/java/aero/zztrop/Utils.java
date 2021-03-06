package aero.zztrop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Utils {

	// !!! DO NOT CHANGE THESE START MODE VALUES TO ASSURE COMPATIBILITY WITH
	// OLDER VERSIONS !!!
	static final int START_MODE_COCKPIT1 = 0;
	static final int START_MODE_COCKPIT2 = 1;
	static final int START_MODE_CABIN = 2;

	public static final String SHARED_PREFS_NAME = "zztropPrefs";
	public static final String PREFS_STR_START_MODE = "startMode";

	public static final String PREFS_THEME_RESID_ID = "theme_resid";
	public static final int DEFAULT_THEME_RESID = R.style.boeing_grey;

	public static final int SETTINGS_KEYPAD = 0;
	public static final int SETTINGS_FASTPAD = 1;

	/**
	 * Applies the theme saved in Preferences to the Activity parameter.
	 * 
	 * @param act
	 *            Activity to set the shared theme to.
	 */

	public static void applySharedTheme(Activity act) {

		act.getApplicationContext();
		SharedPreferences sPref = act.getSharedPreferences(SHARED_PREFS_NAME,
				Context.MODE_PRIVATE);

		int themeID = sPref.getInt(PREFS_THEME_RESID_ID, DEFAULT_THEME_RESID);

		act.setTheme(themeID);
	}

	public static int getSharedThemeResId(Activity act) {
		act.getApplicationContext();
		SharedPreferences sPref = act.getSharedPreferences(SHARED_PREFS_NAME,
				Context.MODE_PRIVATE);

		return sPref.getInt(PREFS_THEME_RESID_ID, DEFAULT_THEME_RESID);
	}

	public static boolean stringIsValidTime(String s) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.setLenient(false);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		// set the timeformat according to the number of digits
		if (s.length() == 2) {
			sdf.setLenient(true); // to allow "90" for "1h30"
			sdf.applyPattern("mm");
		} else if (s.length() > 2) {
			sdf.setLenient(false);
			sdf.applyPattern("HH:mm");
		}

		// test the time value from edittext
		try {
			sdf.parse(s).getTime();
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	@TargetApi(13)
	public static boolean isTablet(Context context) {
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			// honeycomb
			// test screen size, use reflection because isLayoutSizeAtLeast is
			// only available since 11
			return (context.getResources().getConfiguration().smallestScreenWidthDp) >= 600;
		}
		return false;
	}
}
