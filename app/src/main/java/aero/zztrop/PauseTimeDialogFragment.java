package aero.zztrop;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PauseTimeDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PauseTimeDialogFragment extends DialogFragment implements View.OnClickListener {

    protected EditText etTime;
    // buttons declarations
    protected Button btn01;
    protected Button btn02;
    protected Button btn03;
    protected Button btn04;
    protected Button btn05;
    protected Button btn06;
    protected Button btn07;
    protected Button btn08;
    protected Button btn09;
    protected Button btn0;
    protected Button btnBKSP;
    protected Button btnClear;
    protected Button btnOK;

    public PauseTimeDialogFragment() {
        // Required empty public constructor
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface PauseTimeDialogListener {
        public void onDialogOkClick(CharSequence val);
    }

    // Use this instance of the interface to deliver action events
    PauseTimeDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (PauseTimeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PauseTimeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PauseTimeDialogFragment newInstance() {
        PauseTimeDialogFragment fragment = new PauseTimeDialogFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_time_keypad, null);
        Window window = this.getDialog().getWindow();
        // deal with theme id as it might change between app versions
        switch (Utils.getSharedThemeResId(this.getActivity())) {
            case R.style.airbus:
                window.setBackgroundDrawableResource(R.drawable.dialog_background_airbus);
                break;

            case R.style.boeing:
                window.setBackgroundDrawableResource(R.drawable.dialog_background_boeing);
                break;

            default:
                window.setBackgroundDrawableResource(R.drawable.dialog_background_boeing_grey);
        }
        initComponents(v);
        return v;
    }

    protected void initComponents(View v) {

        etTime = v.findViewById(R.id.etTime);
        etTime.setInputType(0);

        btn01 = (Button) v.findViewById(R.id.Button01);
        btn01.setOnClickListener(this);

        btn02 = (Button) v.findViewById(R.id.Button02);
        btn02.setOnClickListener(this);

        btn03 = (Button) v.findViewById(R.id.Button03);
        btn03.setOnClickListener(this);

        btn04 = (Button) v.findViewById(R.id.Button04);
        btn04.setOnClickListener(this);

        btn05 = (Button) v.findViewById(R.id.Button05);
        btn05.setOnClickListener(this);

        btn05 = (Button) v.findViewById(R.id.Button05);
        btn05.setOnClickListener(this);

        btn06 = (Button) v.findViewById(R.id.Button06);
        btn06.setOnClickListener(this);

        btn07 = (Button) v.findViewById(R.id.Button07);
        btn07.setOnClickListener(this);

        btn08 = (Button) v.findViewById(R.id.Button08);
        btn08.setOnClickListener(this);

        btn09 = (Button) v.findViewById(R.id.Button09);
        btn09.setOnClickListener(this);

        btn0 = (Button) v.findViewById(R.id.Button0);
        btn0.setOnClickListener(this);

        btnBKSP = (Button) v.findViewById(R.id.btnBKSP);
        btnBKSP.setOnClickListener(this);

        btnClear = (Button) v.findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        btnOK = (Button) v.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(this);
    }

    protected void updateNumberField(String s) {
        // first remove the ":" caracter if any
        if (etTime.getText().toString().indexOf(":") != -1) {
            etTime.getText().delete(etTime.getText().toString().indexOf(":"),
                    etTime.getText().toString().indexOf(":") + 1);
        }

        // add the typed number except if they are already 2
        if (etTime.getText().length() < 2) {
            etTime.setText(etTime.getText().toString() + s);
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
            listener.onDialogOkClick(etTime.getText());
            this.dismiss();
        }
    }

    protected String fieldBackspace(String s) {
        // create a stringbuilder to play with our string
        StringBuilder sb = new StringBuilder(s);

        // first remove the ":" caracter if any
        if (sb.indexOf(":") != -1) {
            sb.deleteCharAt(sb.indexOf(":"));
        }

        // delete last number (if exist)
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }
}