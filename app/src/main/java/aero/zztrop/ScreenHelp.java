package aero.zztrop;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ScreenHelp extends Activity implements OnClickListener {

    private Button btnHelpOK;
    private Button btnMail;
    private Button btnWeb;
    private TextView tvHelpContent;
    private String content;

    protected void onCreate(Bundle savedInstanceState) {
        Utils.applySharedTheme(this);
        if (!Utils.isTablet(getApplicationContext())) {
            // set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_help);
        initComponents();
    }

    private void initComponents() {
        tvHelpContent = (TextView) findViewById(R.id.tvHelpContent);
        tvHelpContent.setText(Html.fromHtml(readRawFile()));

        btnHelpOK = (Button) findViewById(R.id.btnHelpOK);
        btnHelpOK.setOnClickListener(this);

        btnMail = (Button) findViewById(R.id.btnMail);
        btnMail.setOnClickListener(this);

        btnWeb = (Button) findViewById(R.id.btnWeb);
        btnWeb.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnHelpOK) {
            setResult(RESULT_OK);
            finish();
        }

        if (v == btnMail) {
            final Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL,
                    new String[]{getString(R.string.email_adress)});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                    getString(R.string.email_subject));

            try {
                startActivity(Intent.createChooser(emailIntent,
                        getString(R.string.send_email)));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ScreenHelp.this,
                        getString(R.string.no_email_client), Toast.LENGTH_LONG)
                        .show();
            }
        }

        if (v == btnWeb) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.web_adress)));
            startActivity(browserIntent);
        }
    }

    private String readRawFile() {
        InputStream is = getApplicationContext().getResources().openRawResource(R.raw.help);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while ((line = reader.readLine()) != null) {
                text.append(line);
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }
}
