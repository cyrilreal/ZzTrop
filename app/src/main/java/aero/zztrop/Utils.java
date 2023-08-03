package aero.zztrop;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * @param act Activity to set the shared theme to.
     */

    public static void applySharedTheme(Activity act) {

        SharedPreferences sPref = act.getSharedPreferences(SHARED_PREFS_NAME,
                Context.MODE_PRIVATE);

        int themeID = sPref.getInt(PREFS_THEME_RESID_ID, R.style.boeing_grey);

        // deal with theme id as it might change between app versions
        if (themeID == R.style.airbus) {
            act.setTheme(R.style.airbus);
        } else if (themeID == R.style.boeing) {
            act.setTheme(R.style.boeing);
        } else {
            act.setTheme(R.style.boeing_grey);
        }
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

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().smallestScreenWidthDp) >= 600;
    }

    public static int parsePauseValue(String s) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(s);
        if (m.find()) {
            return Integer.parseInt(m.group(0));
        }
        return 0;
    }
}
