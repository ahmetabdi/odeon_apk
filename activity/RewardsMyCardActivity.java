package uk.co.odeon.androidapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.util.drawable.DrawableManager;

public class RewardsMyCardActivity extends AbstractODEONBaseActivity {
    protected static final String TAG;

    static {
        TAG = RewardsMyCardActivity.class.getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewards_my_card);
        configureNavigationHeader();
        String opcCardNumber = ODEONApplication.getInstance().getCustomerDataPrefs().getString(Constants.CUSTOMER_PREFS_OPC_CARD, null);
        if (opcCardNumber != null) {
            ImageView imageView = (ImageView) findViewById(R.id.opcCardBarcodeImage);
            DrawableManager dm = DrawableManager.getInstance();
            dm.loadDrawable(Constants.formatLocationUrlWithParam(Constants.API_URL_OPC_CARD_BARCODE, opcCardNumber), imageView, dm.buildImageCacheFileBasedOnCustomName(String.format(Constants.BITMAP_OPC_CARD_BARCODE_FILE_NAME, new Object[]{opcCardNumber})), R.drawable.rewards_my_card_barcode_noimg, R.drawable.rewards_my_card_barcode_noimg);
        }
    }

    public void onResume() {
        super.onResume();
        if (!ODEONApplication.getInstance().hasCustomerLoginInPrefs()) {
            onBackPressed();
        }
    }

    protected void configureNavigationHeader() {
        TextView navigationHeaderTitle = (TextView) findViewById(R.id.navigationHeaderTitle);
        if (navigationHeaderTitle != null) {
            navigationHeaderTitle.setText(getResources().getString(R.string.rewards_mycard_header_title));
        }
        Button backButton = (Button) findViewById(R.id.navigationHeaderCancel);
        if (backButton != null) {
            backButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    RewardsMyCardActivity.this.finish();
                }
            });
        }
        configureNavigationHeaderNext("Help", new OnClickListener() {
            public void onClick(View v) {
                RewardsMyCardActivity.this.startActivity(new Intent(v.getContext(), RewardsMyCardHelpActivity.class));
            }
        });
    }
}
