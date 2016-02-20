package uk.co.odeon.androidapp.activity;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.Constants.APP_LOCATION;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;

public class SettingsActivity extends AbstractODEONBaseActivity {
    protected static final String TAG;
    private TextView loggedInText;
    protected final ODEONApplication odeonApplication;

    public SettingsActivity() {
        this.odeonApplication = ODEONApplication.getInstance();
        this.loggedInText = null;
    }

    static {
        TAG = SettingsActivity.class.getSimpleName();
    }

    protected TextView getLoggedInText() {
        if (this.loggedInText == null) {
            this.loggedInText = (TextView) findViewById(R.id.loggedInText);
        }
        return this.loggedInText;
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        configureNavigationHeader();
        ImageButton locationUkButton = (ImageButton) findViewById(R.id.locationUkButton);
        if (locationUkButton != null) {
            if (APP_LOCATION.ire.equals(this.odeonApplication.getChoosenLocation())) {
                locationUkButton.setImageResource(R.drawable.bt_location_uk_passiv);
            } else {
                locationUkButton.setImageResource(R.drawable.bt_location_uk_activ);
            }
            locationUkButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ODEONApplication.trackEvent("Config Location UK", "Click", "");
                    if (APP_LOCATION.uk.equals(SettingsActivity.this.odeonApplication.getChoosenLocation())) {
                        SettingsActivity.this.finish();
                        return;
                    }
                    SettingsActivity.this.odeonApplication.setChoosenLocation(APP_LOCATION.uk);
                    SettingsActivity.this.refreshAndRestart();
                }
            });
        }
        ImageButton locationIreButton = (ImageButton) findViewById(R.id.locationIreButton);
        if (locationIreButton != null) {
            if (APP_LOCATION.ire.equals(this.odeonApplication.getChoosenLocation())) {
                locationIreButton.setImageResource(R.drawable.bt_location_ireland_activ);
            } else {
                locationIreButton.setImageResource(R.drawable.bt_location_ireland_passiv);
            }
            locationIreButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ODEONApplication.trackEvent("Config Location IRELAND", "Click", "");
                    if (APP_LOCATION.ire.equals(SettingsActivity.this.odeonApplication.getChoosenLocation())) {
                        SettingsActivity.this.finish();
                        return;
                    }
                    SettingsActivity.this.odeonApplication.setChoosenLocation(APP_LOCATION.ire);
                    SettingsActivity.this.refreshAndRestart();
                }
            });
        }
        ImageButton clearUserDetailsButton = (ImageButton) findViewById(R.id.clearUserDetailsButton);
        if (clearUserDetailsButton != null) {
            clearUserDetailsButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (SettingsActivity.this.odeonApplication.clearCustomerDataInPrefs()) {
                        Builder builder = new Builder(SettingsActivity.this);
                        builder.setMessage(SettingsActivity.this.getString(R.string.about_delete_prefs_dialog_msg)).setCancelable(false).setPositiveButton(SettingsActivity.this.getString(R.string.about_delete_prefs_dialog_button_label), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        builder.create().show();
                    }
                    SettingsActivity.this.odeonApplication.clearCacheData();
                    SettingsActivity.this.setLoggedInUserInView();
                }
            });
        }
    }

    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        setLoggedInUserInView();
    }

    private void refreshAndRestart() {
        this.odeonApplication.clearCustomerLoginInPrefs();
        this.odeonApplication.clearCacheData();
        Intent restartIntent = new Intent(this, MainTabActivity.class);
        restartIntent.setFlags(67108864);
        startActivity(restartIntent);
    }

    protected void configureNavigationHeader() {
        TextView navigationHeaderTitle = (TextView) findViewById(R.id.navigationHeaderTitle);
        if (navigationHeaderTitle != null) {
            navigationHeaderTitle.setText(getString(R.string.settings_header_title));
        }
        Button backButton = (Button) findViewById(R.id.navigationHeaderCancel);
        if (backButton != null) {
            backButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    SettingsActivity.this.finish();
                }
            });
        }
    }

    protected void setLoggedInUserInView() {
        String loggedInUsername = this.odeonApplication.getCustomerDataPrefs().getString(Constants.CUSTOMER_PREFS_USERNAME, "");
        if (loggedInUsername == null || loggedInUsername.trim().length() <= 0) {
            getLoggedInText().setText("");
            return;
        }
        getLoggedInText().setText(getString(R.string.about_logged_in_as, new Object[]{loggedInUsername}));
    }
}
