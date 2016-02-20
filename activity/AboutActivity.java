package uk.co.odeon.androidapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.R;

public class AboutActivity extends AbstractODEONBaseActivity {
    protected static final String TAG;

    static {
        TAG = AboutActivity.class.getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        configureNavigationHeader();
        TextView tAndCText = (TextView) findViewById(R.id.tAndCText);
        if (tAndCText != null) {
            tAndCText.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    AboutActivity.this.startWebviewActivity(AboutActivity.this.getString(R.string.about_header_title), AboutActivity.this.getString(R.string.about_t_and_c), Constants.formatLocationUrl(Constants.URL_TERMS_AND_CONDITIONS));
                }
            });
        }
        TextView policiesText = (TextView) findViewById(R.id.policiesText);
        if (policiesText != null) {
            policiesText.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    AboutActivity.this.startWebviewActivity(AboutActivity.this.getString(R.string.about_header_title), AboutActivity.this.getString(R.string.about_other_policies), Constants.formatLocationUrl(Constants.URL_OTHER_POLICIES));
                }
            });
        }
        TextView contactText = (TextView) findViewById(R.id.contactText);
        if (contactText != null) {
            contactText.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    AboutActivity.this.startWebviewActivity(AboutActivity.this.getString(R.string.about_header_title), AboutActivity.this.getString(R.string.about_contact), Constants.formatLocationUrl(Constants.URL_CONTACT_US));
                }
            });
        }
        TextView versionText = (TextView) findViewById(R.id.versionText);
        if (versionText != null) {
            versionText.setText(getResources().getString(R.string.about_version, new Object[]{getVersionString()}));
        }
    }

    protected void startWebviewActivity(String headerTitle, String title, String url) {
        Intent webviewIntent = new Intent(this, WebviewActivity.class);
        webviewIntent.putExtra(Constants.EXTRA_WEBVIEW_HEADER_TITLE, headerTitle);
        webviewIntent.putExtra(Constants.EXTRA_WEBVIEW_TITLE, title);
        webviewIntent.putExtra(Constants.EXTRA_WEBVIEW_URL, url);
        startActivity(webviewIntent);
    }

    protected void configureNavigationHeader() {
        TextView navigationHeaderTitle = (TextView) findViewById(R.id.navigationHeaderTitle);
        if (navigationHeaderTitle != null) {
            navigationHeaderTitle.setText(getResources().getString(R.string.about_header_title));
        }
        Button backButton = (Button) findViewById(R.id.navigationHeaderCancel);
        if (backButton != null) {
            backButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    AboutActivity.this.finish();
                }
            });
        }
    }
}
