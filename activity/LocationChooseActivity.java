package uk.co.odeon.androidapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.Constants.APP_LOCATION;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;

public class LocationChooseActivity extends AbstractODEONBaseActivity {
    protected static final String TAG;

    static {
        TAG = LocationChooseActivity.class.getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_choose);
        ImageButton locationUkButton = (ImageButton) findViewById(R.id.locationUkButton);
        if (locationUkButton != null) {
            locationUkButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ODEONApplication.trackEvent("Location UK", "Click", "");
                    ODEONApplication.getInstance().setChoosenLocation(APP_LOCATION.uk);
                    LocationChooseActivity.this.finish();
                }
            });
        }
        ImageButton locationIreButton = (ImageButton) findViewById(R.id.locationIreButton);
        if (locationIreButton != null) {
            locationIreButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ODEONApplication.trackEvent("Location IRELAND", "Click", "");
                    ODEONApplication.getInstance().setChoosenLocation(APP_LOCATION.ire);
                    LocationChooseActivity.this.finish();
                }
            });
        }
    }

    public void onBackPressed() {
        if (ODEONApplication.getInstance().hasChoosenLocation()) {
            super.onBackPressed();
            return;
        }
        setResult(Constants.TICKET_LIST_RELOAD);
        finish();
    }
}
