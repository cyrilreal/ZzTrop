package aero.zztrop;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;

public class ScreenTimeKeypadService extends ScreenTimeKeypad implements
		OnLongClickListener {

	private Button btnMem01, btnMem02, btnMem03, btnMem04, btnMem05, btnMem06,
			btnMem07, btnMem08;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Utils.applySharedTheme(this);
		super.onCreate(savedInstanceState);
		if (!Utils.isTablet(getApplicationContext())) {
			// set phones to portrait;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.screen_time_keypad_service);

		super.initComponents();

		initMemButtons();
		loadPrefs();
	}

	private void initMemButtons() {

		btnMem01 = (Button) findViewById(R.id.btnMem01);
		btnMem01.setOnClickListener(this);
		btnMem01.setOnLongClickListener(this);

		btnMem02 = (Button) findViewById(R.id.btnMem02);
		btnMem02.setOnClickListener(this);
		btnMem02.setOnLongClickListener(this);

		btnMem03 = (Button) findViewById(R.id.btnMem03);
		btnMem03.setOnClickListener(this);
		btnMem03.setOnLongClickListener(this);

		btnMem04 = (Button) findViewById(R.id.btnMem04);
		btnMem04.setOnClickListener(this);
		btnMem04.setOnLongClickListener(this);

		btnMem05 = (Button) findViewById(R.id.btnMem05);
		btnMem05.setOnClickListener(this);
		btnMem05.setOnLongClickListener(this);

		btnMem06 = (Button) findViewById(R.id.btnMem06);
		btnMem06.setOnClickListener(this);
		btnMem06.setOnLongClickListener(this);

		btnMem07 = (Button) findViewById(R.id.btnMem07);
		btnMem07.setOnClickListener(this);
		btnMem07.setOnLongClickListener(this);

		btnMem08 = (Button) findViewById(R.id.btnMem08);
		btnMem08.setOnClickListener(this);
		btnMem08.setOnLongClickListener(this);
	}

	/**
	 * Save the entered presets in Preferences, based on the page title (service
	 * or last service)
	 */
	private void savePrefs() {
		SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
				MODE_PRIVATE);

		SharedPreferences.Editor editor = prefs.edit();

		if (tvKeypadTitle.getText().toString()
				.equals(this.getString(R.string.ServiceDuration))) {
			editor.putString("memServ01", btnMem01.getText().toString());
			editor.putString("memServ02", btnMem02.getText().toString());
			editor.putString("memServ03", btnMem03.getText().toString());
			editor.putString("memServ04", btnMem04.getText().toString());
			editor.putString("memServ05", btnMem05.getText().toString());
			editor.putString("memServ06", btnMem06.getText().toString());
			editor.putString("memServ07", btnMem07.getText().toString());
			editor.putString("memServ08", btnMem08.getText().toString());
		} else if (tvKeypadTitle.getText().toString()
				.equals(this.getString(R.string.LastServiceDuration))) {
			editor.putString("memLastServ01", btnMem01.getText().toString());
			editor.putString("memLastServ02", btnMem02.getText().toString());
			editor.putString("memLastServ03", btnMem03.getText().toString());
			editor.putString("memLastServ04", btnMem04.getText().toString());
			editor.putString("memLastServ05", btnMem05.getText().toString());
			editor.putString("memLastServ06", btnMem06.getText().toString());
			editor.putString("memLastServ07", btnMem07.getText().toString());
			editor.putString("memLastServ08", btnMem08.getText().toString());
		} else if (tvKeypadTitle.getText().toString()
				.equals(this.getString(R.string.enter_x_valeur))) {
			editor.putString("memXValue01", btnMem01.getText().toString());
			editor.putString("memXValue02", btnMem02.getText().toString());
			editor.putString("memXValue03", btnMem03.getText().toString());
			editor.putString("memXValue04", btnMem04.getText().toString());
			editor.putString("memXValue05", btnMem05.getText().toString());
			editor.putString("memXValue06", btnMem06.getText().toString());
			editor.putString("memXValue07", btnMem07.getText().toString());
			editor.putString("memXValue08", btnMem08.getText().toString());
		}
		editor.commit();
	}

	/**
	 * Load the proper memory fields based on the page title (service or last
	 * service)
	 */
	private void loadPrefs() {
		SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREFS_NAME,
				MODE_PRIVATE);
		if (tvKeypadTitle.getText().toString()
				.equals(this.getString(R.string.ServiceDuration))) {
			btnMem01.setText(prefs.getString("memServ01", "Mem"));
			btnMem02.setText(prefs.getString("memServ02", "Mem"));
			btnMem03.setText(prefs.getString("memServ03", "Mem"));
			btnMem04.setText(prefs.getString("memServ04", "Mem"));
			btnMem05.setText(prefs.getString("memServ05", "Mem"));
			btnMem06.setText(prefs.getString("memServ06", "Mem"));
			btnMem07.setText(prefs.getString("memServ07", "Mem"));
			btnMem08.setText(prefs.getString("memServ08", "Mem"));
		} else if (tvKeypadTitle.getText().toString()
				.equals(this.getString(R.string.LastServiceDuration))) {
			btnMem01.setText(prefs.getString("memLastServ01", "Mem"));
			btnMem02.setText(prefs.getString("memLastServ02", "Mem"));
			btnMem03.setText(prefs.getString("memLastServ03", "Mem"));
			btnMem04.setText(prefs.getString("memLastServ04", "Mem"));
			btnMem05.setText(prefs.getString("memLastServ05", "Mem"));
			btnMem06.setText(prefs.getString("memLastServ06", "Mem"));
			btnMem07.setText(prefs.getString("memLastServ07", "Mem"));
			btnMem08.setText(prefs.getString("memLastServ08", "Mem"));
		} else if (tvKeypadTitle.getText().toString()
				.equals(this.getString(R.string.enter_x_valeur))) {
			btnMem01.setText(prefs.getString("memXValue01", "Mem"));
			btnMem02.setText(prefs.getString("memXValue02", "Mem"));
			btnMem03.setText(prefs.getString("memXValue03", "Mem"));
			btnMem04.setText(prefs.getString("memXValue04", "Mem"));
			btnMem05.setText(prefs.getString("memXValue05", "Mem"));
			btnMem06.setText(prefs.getString("memXValue06", "Mem"));
			btnMem07.setText(prefs.getString("memXValue07", "Mem"));
			btnMem08.setText(prefs.getString("memXValue08", "Mem"));
		}

		// now update buttons font size and color if their text
		// is not the default "Mem"
		if (!btnMem01.getText().toString()
				.equals(getResources().getString(R.string.btnMem))) {
			btnMem01.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			btnMem01.setTextColor(getResources()
					.getColor(R.color.colorLightGrey));
		}

		if (!btnMem02.getText().toString()
				.equals(getResources().getString(R.string.btnMem))) {
			btnMem02.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			btnMem02.setTextColor(getResources()
					.getColor(R.color.colorLightGrey));
		}

		if (!btnMem03.getText().toString()
				.equals(getResources().getString(R.string.btnMem))) {
			btnMem03.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			btnMem03.setTextColor(getResources()
					.getColor(R.color.colorLightGrey));
		}

		if (!btnMem04.getText().toString()
				.equals(getResources().getString(R.string.btnMem))) {
			btnMem04.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			btnMem04.setTextColor(getResources()
					.getColor(R.color.colorLightGrey));
		}

		if (!btnMem05.getText().toString()
				.equals(getResources().getString(R.string.btnMem))) {
			btnMem05.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			btnMem05.setTextColor(getResources()
					.getColor(R.color.colorLightGrey));
		}

		if (!btnMem06.getText().toString()
				.equals(getResources().getString(R.string.btnMem))) {
			btnMem06.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			btnMem06.setTextColor(getResources()
					.getColor(R.color.colorLightGrey));
		}

		if (!btnMem07.getText().toString()
				.equals(getResources().getString(R.string.btnMem))) {
			btnMem07.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			btnMem07.setTextColor(getResources()
					.getColor(R.color.colorLightGrey));
		}

		if (!btnMem08.getText().toString()
				.equals(getResources().getString(R.string.btnMem))) {
			btnMem08.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			btnMem08.setTextColor(getResources()
					.getColor(R.color.colorLightGrey));
		}
	}

	@Override
	public void onClick(View v) {

		// Buttons action
		if (v == btn01) {
			updateNumberField("1");
		}

		if (v == btn02) {
			updateNumberField("2");
		}

		if (v == btn03) {
			updateNumberField("3");
		}

		if (v == btn04) {
			updateNumberField("4");
		}

		if (v == btn05) {
			updateNumberField("5");
		}

		if (v == btn06) {
			updateNumberField("6");
		}

		if (v == btn07) {
			updateNumberField("7");
		}

		if (v == btn08) {
			updateNumberField("8");
		}

		if (v == btn09) {
			updateNumberField("9");
		}

		if (v == btn0) {
			updateNumberField("0");
		}

		if (v == btnBKSP) {
			etTime.setText(fieldBackspace(etTime.getText().toString()));
		}

		if (v == btnClear) {
			etTime.setText("");
		}

		if (v == btnOK) {
			if (updateCalTime(etTime.getText().toString()) == true) {
				// save all memory button values and send
				// the data back
				sendDataBack();
			}
		}

		if (v == btnMem01) {
			if (updateCalTime(((Button) v).getText().toString())) {
				// save all memory button values and send
				// the data back
				sendDataBack();
			}
		}

		if (v == btnMem02) {
			if (updateCalTime(((Button) v).getText().toString())) {
				sendDataBack();
			}
		}

		if (v == btnMem03) {
			if (updateCalTime(((Button) v).getText().toString())) {
				sendDataBack();
			}
		}

		if (v == btnMem04) {
			if (updateCalTime(((Button) v).getText().toString())) {
				sendDataBack();
			}
		}

		if (v == btnMem05) {
			if (updateCalTime(((Button) v).getText().toString())) {
				sendDataBack();
			}
		}

		if (v == btnMem06) {
			if (updateCalTime(((Button) v).getText().toString())) {
				sendDataBack();
			}
		}

		if (v == btnMem07) {
			if (updateCalTime(((Button) v).getText().toString())) {
				sendDataBack();
			}
		}

		if (v == btnMem08) {
			if (updateCalTime(((Button) v).getText().toString())) {
				sendDataBack();
			}
		}
	}

	@Override
	public boolean onLongClick(View v) {
		// if time is valid, copy its string representation to the button (and
		// blank the edittext. Otherwise, if edittext is blank then reset the
		// mem button. Prefs are saved each time a change is made

		if (etTime.getText().toString().equals("")) {
			((Button) v).setTextColor(getResources().getColor(
					R.color.colorMediumGrey));
			((Button) v).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			((Button) v).setText("Mem");
			savePrefs();
			return true;
		} else if (updateCalTime(etTime.getText().toString()) == true) {
			sdfTime.applyPattern("H:mm");
			((Button) v).setTextColor(getResources().getColor(
					R.color.colorLightGrey));
			((Button) v).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			((Button) v).setText(sdfTime.format(calTime.getTime()));
			etTime.setText("");
			savePrefs();
			return true;
		}
		return true;
	}
}
