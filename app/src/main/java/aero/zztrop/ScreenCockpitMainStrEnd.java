package aero.zztrop;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ScreenCockpitMainStrEnd extends FragmentActivity implements
        OnClickListener,
        OnFocusChangeListener, OnItemClickListener,
        PauseTimeDialogFragment.PauseTimeDialogListener {

    // to deal with android.support.v4 bug
    boolean isShowCloseDialog = false;

    private EditText etStart;
    private EditText etEnd;
    private EditText etPauseDuration;

    private long xTime = 0; // for patterns with a x value (forced by user)

    private boolean flagLaunch = true; // prevent timescreen display at startup

    private Button btnHelp, btnSettings;
    private ListView lvPatterns;
    ArrayAdapter<String> adapter;
    private static final int TIME_TYPE_START = 101;
    private static final int TIME_TYPE_END = 102;
    private static final int TIME_TYPE_X = 110;
    private static final int REQUEST_SETTINGS = 53;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Utils.applySharedTheme(this);
        super.onCreate(savedInstanceState);
        if (!Utils.isTablet(getApplicationContext())) {
            // set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.screen_cockpit_main_strend);
        initComponents();
        // this is to prevent non conform entries in the
        // default focused editText via the keyboard
        btnHelp.requestFocus();
    }

    private void initComponents() {


        // load user preferences
        SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
                MODE_PRIVATE);

        etStart = findViewById(R.id.etStart);
        etStart.setOnFocusChangeListener(this);
        etStart.setOnClickListener(this);
        etStart.setInputType(0);

        etEnd = findViewById(R.id.etEnd);
        etEnd.setOnFocusChangeListener(this);
        etEnd.setOnClickListener(this);
        etEnd.setInputType(0);

        etPauseDuration = findViewById(R.id.etPause);
        etPauseDuration.setText(prefs.getString("pause_time_cockpit", " 10'"));
        etPauseDuration.setOnFocusChangeListener(this);
        etPauseDuration.setOnClickListener(this);
        etPauseDuration.setInputType(0);

        btnHelp = findViewById(R.id.btnHelp);
        btnHelp.setOnClickListener(this);

        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);

        lvPatterns = findViewById(R.id.lv_patterns);
        adapter = new ArrayAdapter<>(
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
        editor.putString("pause_time_cockpit", etPauseDuration.getText().toString());
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
            displayKeypad(TIME_TYPE_START, null,
                    getString(R.string.FirstRestStart));
        }

        if (v == etEnd) {
            displayKeypad(TIME_TYPE_END, null, getString(R.string.LastRestEnd));
        }

        if (v == etPauseDuration) {
            DialogFragment dialog = new PauseTimeDialogFragment();
            dialog.show(getSupportFragmentManager(), "PauseTimeDialogFragment");
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

        if (v == etStart && hasFocus) {
            displayKeypad(TIME_TYPE_START, null,
                    getString(R.string.FirstRestStart));
            return;
        }

        if (v == etEnd && hasFocus) {
            displayKeypad(TIME_TYPE_END, null, getString(R.string.LastRestEnd));
            return;
        }

        if (v == etPauseDuration && hasFocus) {
            DialogFragment dialog = new PauseTimeDialogFragment();
            dialog.show(getSupportFragmentManager(), "PauseTimeDialogFragment");
        }
    }

    private void processPattern(String pattern) {
        // test if both start and end fields have been properly set
        if (!testFieldsReady()) {
            return;
        }

        // create an intent containing start, end, pause and service times
        Intent intent = new Intent(this, ScreenResultCockpit.class);
        intent.putExtra("pattern", pattern);
        intent.putExtra("time_start", etStart.getText().toString());
        intent.putExtra("time_end", etEnd.getText().toString());
        intent.putExtra("pause", Utils.parsePauseValue(etPauseDuration.getText().toString()));
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTINGS) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                boolean bln = bundle.getBoolean("theme_has_changed");
                if (bln) {
                    // use a boolean because of ICS bug with support package
                    isShowCloseDialog = true;
                }
                SharedPreferences prefs = getSharedPreferences(
                        Utils.SHARED_PREFS_NAME, MODE_PRIVATE);
                // reset listview adapter cause pattern list may have changed
                lvPatterns.setAdapter(new ArrayAdapter<CharSequence>(
                        this, R.layout.row_pattern_cockpit, R.id.tvPattern,
                        getDisplayablePatterns()));

                // test if work mode has changed (app is in cockpit mode)
                int mode = prefs
                        .getInt(Utils.PREFS_STR_START_MODE,
                                Utils.START_MODE_COCKPIT1);

                if (mode == Utils.START_MODE_COCKPIT2) {
                    // display the main cockpit2 screen
                    Intent intent = new Intent(this, ScreenCockpitMainTkfLdg.class);
                    startActivity(intent);
                }

                if (mode == Utils.START_MODE_CABIN) {
                    // display the main cabin screen
                    Intent intent = new Intent(this, ScreenCabinMain.class);
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

    @Override
    public void onDialogOkClick(CharSequence val) {
        this.etPauseDuration.setText(" " + val + "'");
    }
}