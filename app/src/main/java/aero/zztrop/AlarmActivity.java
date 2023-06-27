package aero.zztrop;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

public class AlarmActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);	
				
		/** Creating an Alert Dialog Window */
		AlarmDialogFragment alarm = new AlarmDialogFragment();
		
		/** Opening the Alert Dialog Window */
		alarm.show(getSupportFragmentManager(), "alarm");		
	}
}
