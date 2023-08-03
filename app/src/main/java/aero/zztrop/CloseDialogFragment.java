package aero.zztrop;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

public class CloseDialogFragment extends DialogFragment {
	/**
	 * The system calls this to get the DialogFragment's layout, regardless of
	 * whether it's being displayed as a dialog or an embedded fragment.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout to use as dialog or embedded fragment
		View v = inflater.inflate(R.layout.dialog_application_close, container,
				false);
		Button btnClose = v.findViewById(R.id.btnOK);
		// force some style components as Style attributes do not seem to
		// interact here...
		btnClose.setBackground(getResources().getDrawable(R.drawable.btn_cdu));
		btnClose.setTextColor(Color.WHITE);
		btnClose.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f);
		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});

		Window window = this.getDialog().getWindow();
		// deal with theme id as it might change between app versions
		int sharedThemeResId = Utils.getSharedThemeResId(this.getActivity());
		if (sharedThemeResId == R.style.airbus) {
			window.setBackgroundDrawableResource(R.drawable.dialog_background_airbus);
		} else if (sharedThemeResId == R.style.boeing) {
			window.setBackgroundDrawableResource(R.drawable.dialog_background_boeing);
		} else {
			window.setBackgroundDrawableResource(R.drawable.dialog_background_boeing_grey);
		}
		return v;
	}

	/** The system calls this only when creating the layout in a dialog. */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		/*
		The only reason you might override this method when using
		onCreateView() is
		to modify any dialog characteristics. For example, the dialog
		includes a
		title by default, but your custom layout might not need it. So here
		you can
		remove the dialog title, but you must call the superclass to get the
		Dialog.
		*/
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}
}