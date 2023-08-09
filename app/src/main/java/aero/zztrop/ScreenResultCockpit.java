package aero.zztrop;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ToggleButton;

public class ScreenResultCockpit extends FragmentActivity implements
        OnClickListener {

    private ListView lvResults;
    private TimePairCockpitAdapter m_adapter;
    private Button btnResultsOK;
    private ToggleButton tbtnRound;
    private Button btnShiftPlus, btnShiftMinus;
    private Button btnDurationPlus, btnDurationMinus;
    private ZzEngineCockpit zzEngCock;

    Context context = ScreenResultCockpit.this.getBaseContext();

    protected void onCreate(Bundle savedInstanceState) {
        Utils.applySharedTheme(this);
        super.onCreate(savedInstanceState);
        if (!Utils.isTablet(getApplicationContext())) {
            // set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.screen_result_cockpit);
        initComponents();
        // load user preferences
        SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME, MODE_PRIVATE);
        tbtnRound.setChecked(prefs.getBoolean("roundTimes", false));

        zzEngCock.computeRest();
        if (tbtnRound.isChecked()) {
            zzEngCock.roundTimes();
        }
        // prepare the list to be displayed
        populateList();
        //createAlarm();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Save user preferences
        SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("startupMode", 0); // 0 = cockpit mode
        editor.putBoolean("roundTimes", tbtnRound.isChecked());
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        // am.cancel(pi);
        // unregisterReceiver(br);
        super.onDestroy();
    }

    private void initComponents() {

        zzEngCock = new ZzEngineCockpit();
        // load engine with passed parameters
        Bundle bundle = this.getIntent().getExtras();
        zzEngCock.setPattern(bundle.getString("pattern"));
        zzEngCock.setCalStart(bundle.getString("time_start"));
        zzEngCock.setCalEnd(bundle.getString("time_end"));
        zzEngCock.setPause(bundle.getInt("pause"));
        zzEngCock.setxTime(bundle.getLong("x_time"));
        zzEngCock.setAfterTkf(bundle.getInt("after_tkf", 0));
        zzEngCock.setBeforeLdg(bundle.getInt("before_ldg", 0));
        zzEngCock.adjustStartEnd();
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

        lvResults = (ListView) findViewById(R.id.lvResults);
        // add a header to the list
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(
                R.layout.result_header, lvResults, false);
        lvResults.addHeaderView(header, null, false);
    }

    private void populateList() {
        // the view is defined in the TimePairAdapter class
        m_adapter = new TimePairCockpitAdapter(this, 0, zzEngCock.getAlTimePairs());

        lvResults.setAdapter(m_adapter);
        lvResults.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                zzEngCock.selectTimePair(arg2 - 1);
                m_adapter.notifyDataSetChanged();
            }
        });
    }

//	private void createAlarm() {
//		TimePair tp = zzEngCock.getAlTimePairs().get(0);
//		Calendar alarmCal =
//				Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//
//		// alarmCal.set(Calendar.HOUR_OF_DAY,
//		// tp.getCalEnd().get(Calendar.HOUR_OF_DAY));
//		// alarmCal.set(Calendar.MINUTE, tp.getCalEnd().get(Calendar.MINUTE));
//
//		alarmCal.add(Calendar.SECOND, 10);
//
//		/**
//		 * This intent invokes the activity AlarmActivity, which in turn opens
//		 * the AlarmDialog window
//		 */
//		Intent i = new Intent("aero.zztrop.AlarmActivity");
//
//		/** Creating a Pending Intent */
//		PendingIntent operation = PendingIntent.getActivity(
//				getBaseContext(), 0, i, Intent.FLAG_ACTIVITY_NEW_TASK);
//
//		/** Getting a reference to the System Service ALARM_SERVICE */
//		AlarmManager alarmManager = (AlarmManager) getBaseContext()
//				.getSystemService(ALARM_SERVICE);
//
//		/**
//		 * Creating a calendar object corresponding to the date and time set by
//		 * the user
//		 */
//		GregorianCalendar calendar = new GregorianCalendar();
//
//		/**
//		 * Converting the date and time in to milliseconds elapsed since epoch
//		 */
//		long alarm_time = calendar.getTimeInMillis();
//
//		/** Setting an alarm, which invokes the operation at alart_time */
//		alarmManager
//				.set(AlarmManager.RTC_WAKEUP, alarm_time + 10000, operation);
//
//		/** Alert is set successfully */
//		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
//		Toast.makeText(this, sdf.format(alarmCal.getTime()), Toast.LENGTH_LONG)
//				.show();
//	}

    @Override
    public void onClick(View v) {
        if (v == btnResultsOK) {
            finish();
        }

        if (v == tbtnRound) {
            if (tbtnRound.isChecked()) {
                zzEngCock.roundTimes();
                m_adapter.notifyDataSetChanged();
            } else {
                zzEngCock.computeRest();
                m_adapter.notifyDataSetChanged();
            }
//			createAlarm();
        }

        if (v == btnShiftMinus) {
            if (tbtnRound.isChecked()) {
                zzEngCock.shiftTimes(-5);
            } else
                zzEngCock.shiftTimes(-1);

            m_adapter.notifyDataSetChanged();
//			createAlarm();

        }

        if (v == btnShiftPlus) {
            if (tbtnRound.isChecked()) {
                zzEngCock.shiftTimes(5);
            } else
                zzEngCock.shiftTimes(1);

            m_adapter.notifyDataSetChanged();
//			createAlarm();

        }

        if (v == btnDurationMinus) {
            if (tbtnRound.isChecked()) {
                if (isTimePairSelection()) {
                    zzEngCock.modifyTimePair(zzEngCock.getSelectedTimePair(),
                            -5);
                } else {
                    zzEngCock.modifyAllTimes(-5);
                }
            } else {
                if (isTimePairSelection()) {
                    zzEngCock.modifyTimePair(zzEngCock.getSelectedTimePair(),
                            -1);
                } else {
                    zzEngCock.modifyAllTimes(-1);
                }
            }

            m_adapter.notifyDataSetChanged();
//			createAlarm();

        }

        if (v == btnDurationPlus) {
            if (tbtnRound.isChecked()) {
                if (isTimePairSelection()) {
                    zzEngCock
                            .modifyTimePair(zzEngCock.getSelectedTimePair(), 5);
                } else {
                    zzEngCock.modifyAllTimes(5);
                }
            } else {
                if (isTimePairSelection()) {
                    zzEngCock
                            .modifyTimePair(zzEngCock.getSelectedTimePair(), 1);
                } else {
                    zzEngCock.modifyAllTimes(1);
                }
            }

            m_adapter.notifyDataSetChanged();
//			createAlarm();

        }
    }

    private boolean isTimePairSelection() {
        for (TimePair tp : zzEngCock.getAlTimePairs()) {
            if (tp.isSelected()) {
                return true;
            }
        }
        return false;
    }
}
