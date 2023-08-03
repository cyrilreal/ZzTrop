package aero.zztrop;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ScreenCabin1S extends FragmentActivity implements OnClickListener,
        OnFocusChangeListener, PauseTimeDialogFragment.PauseTimeDialogListener {

    // to deal with android.support.v4 bug
    boolean isShowCloseDialog = false;

    private EditText etStart;
    private EditText etEnd;
    private EditText etPause;
    private EditText etServiceLast;
    private TextView tvPatternTitle;

    // private int pattern;
    private String pattern;
    private boolean flagLaunch = true; // to prevent timepick display at startup

    private Button btnCalculate;
    private Button btnHelp;
    private Button btnSettings;

    protected static final int DIALOG_CLOSE_APPLICATION = 0;

    private static final int TIME_TYPE_START = 101;
    private static final int TIME_TYPE_END = 102;
    private static final int TIME_TYPE_SERVICE_LAST = 103;

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
        setContentView(R.layout.screen_cabin_1s);
        // get data from cabin main screen activity
        Bundle extras = getIntent().getExtras();
        pattern = extras.getString("pattern");

        initComponents();
        // this is to prevent non conform entries in the
        // default focused editText via the keyboard
        btnHelp.requestFocus();
    }

    private void initComponents() {

        etPause = findViewById(R.id.etPause);

        // load user preferences
        SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
                MODE_PRIVATE);

        tvPatternTitle = (TextView) findViewById(R.id.tvPatternTitle1S);
        tvPatternTitle.setText(pattern);

        etStart = (EditText) findViewById(R.id.etStart);
        etStart.setOnFocusChangeListener(this);
        etStart.setOnClickListener(this);
        etStart.setInputType(0);

        etEnd = (EditText) findViewById(R.id.etEnd);
        etEnd.setOnFocusChangeListener(this);
        etEnd.setOnClickListener(this);
        etEnd.setInputType(0);

        etPause = findViewById(R.id.etPause);
        etPause.setText(prefs.getString("pause_time_cabin", " 10'"));
        etPause.setOnFocusChangeListener(this);
        etPause.setOnClickListener(this);
        etPause.setInputType(0);

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
        editor.putString("pause_time_cabin", etPause.getText().toString());
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
            displayKeypad(TIME_TYPE_START, getString(R.string.FirstRestStart));
            return;
        }

        if (v == etEnd) {
            displayKeypad(TIME_TYPE_END, getString(R.string.LastServiceEnd));
            return;
        }

        if (v == etPause) {
            DialogFragment dialog = new PauseTimeDialogFragment();
            dialog.show(getSupportFragmentManager(), "PauseTimeDialogFragment");
        }

        if (v == etServiceLast) {
            displayKeypad(TIME_TYPE_SERVICE_LAST,
                    getString(R.string.LastServiceDuration));
            return;
        }

        if (v == btnCalculate) {
            processPattern(pattern);
            return;
        }

        if (v == btnHelp) {
            // create a new activity to display the help screen
            Intent intent = new Intent(this, ScreenHelp.class);
            startActivity(intent);
            return;
        }

        if (v == btnSettings) {
            // create a new activity to display the help screen
            Intent intent = new Intent(this, ScreenSettings.class);
            displaySettingsLauncher.launch(intent);
        }
    }

    public void onFocusChange(View v, boolean hasFocus) {
        // prevent timepick from appearing at startup
        if (flagLaunch) {
            flagLaunch = false;
            return;
        }

        if (v == etStart && hasFocus) {
            displayKeypad(TIME_TYPE_START, getString(R.string.FirstRestStart));
        }

        if (v == etEnd && hasFocus) {
            displayKeypad(TIME_TYPE_END, getString(R.string.LastServiceEnd));
        }

        if (v == etPause && hasFocus) {
            DialogFragment dialog = new PauseTimeDialogFragment();
            dialog.show(getSupportFragmentManager(), "PauseTimeDialogFragment");
        }

        if (v == etServiceLast && hasFocus) {
            displayKeypad(TIME_TYPE_SERVICE_LAST,
                    getString(R.string.LastServiceDuration));
        }
    }

    private void processPattern(String pattern) {
        // test if all fields have been properly set
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
        intent.putExtra("pause", Utils.parsePauseValue(etPause.getText().toString()));
        intent.putExtra("service_last", etServiceLast.getText().toString());
        startActivity(intent);
    }

    /**
     * Display the keypad for time/duration selection timeType is either start
     * time, end time, or service duration keypadTitle is the string value that
     * will indicate the user wich kind of time/duration he has to enter
     */
    private void displayKeypad(int timeType, String keypadTitle) {
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
                if (keypadTitle.equals(this.getString(R.string.LastServiceDuration))) {
                    intent = new Intent(this, ScreenTimeKeypadService.class);
                } else
                    intent = new Intent(this, ScreenTimeKeypad.class);

                intent.putExtra("timeSelectionTitle", keypadTitle);
                break;

            default:
                break;
        }
        //startActivityForResult(intent, timeType);
        //added foractivityResultLauncher
        intent.putExtra("time_type", timeType);
        displayKeypadLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> displayKeypadLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent intent = result.getData();
                        Calendar calTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        calTime.setTimeInMillis(intent.getLongExtra("time", 0));

                        if (intent.getIntExtra("time_type", 0) == TIME_TYPE_START) {
                            etStart.setText(sdf.format(calTime.getTime()));
                        }

                        if (intent.getIntExtra("time_type", 0) == TIME_TYPE_END) {
                            etEnd.setText(sdf.format(calTime.getTime()));
                        }

                        if (intent.getIntExtra("time_type", 0) == TIME_TYPE_SERVICE_LAST) {
                            etServiceLast.setText(sdf.format(calTime.getTime()));
                        }
                    }
                }
            });

    ActivityResultLauncher<Intent> displaySettingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent intent = result.getData();
                        boolean b = intent.getBooleanExtra("theme_has_changed", false);
                        if (b) {
                            // use a boolean because of ICS bug with support package
                            isShowCloseDialog = true;
                        }
                        // test if work mode has changed (app is in cabin mode)
                        SharedPreferences prefs = getSharedPreferences(
                                Utils.SHARED_PREFS_NAME, MODE_PRIVATE);
                        int mode = prefs.getInt(Utils.PREFS_STR_START_MODE, Utils.START_MODE_CABIN);
                        if (mode == Utils.START_MODE_COCKPIT1) {
                            // display the main cabin screen
                            intent = new Intent(getApplicationContext(), ScreenCockpitMainStrEnd.class);
                            startActivity(intent);
                        }

                        if (mode == Utils.START_MODE_COCKPIT2) {
                            // display the main cockpit2 screen
                            intent = new Intent(getApplicationContext(), ScreenCockpitMainTkfLdg.class);
                            startActivity(intent);
                        }
                    }
                }
            });

    @Override
    public void onDialogOkClick(CharSequence val) {
        this.etPause.setText(" " + val + "'");
    }
}