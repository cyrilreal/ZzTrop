package aero.zztrop;

import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.WindowManager.LayoutParams;

public class AlarmDialogFragment extends DialogFragment {

	private MediaPlayer mMediaPlayer;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		playSound();

		// Turn Screen On and Unlock the keypad
		getActivity().getWindow().addFlags(
				LayoutParams.FLAG_TURN_SCREEN_ON
						| LayoutParams.FLAG_SHOW_WHEN_LOCKED);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.alarm);
		builder.setMessage(R.string.it_is_time);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				getActivity().finish();
			}
		});

		return builder.create();
	}

	/** The application should be exit, if the user presses the back button */
	@Override
	public void onDestroy() {
		super.onDestroy();
		mMediaPlayer.stop();
		getActivity().finish();
	}

	private void playSound() {
		Uri alert = RingtoneManager.getDefaultUri(
				RingtoneManager.TYPE_ALARM);
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(getActivity().getApplicationContext(),
					alert);
			final AudioManager audioManager = (AudioManager) getActivity()
					.getApplicationContext()
					.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			}
		} catch (IOException e) {
		}
	}
}
