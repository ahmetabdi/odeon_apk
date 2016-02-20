package uk.co.odeon.androidapp.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;

public class RewardsMyCardHelpActivity extends AbstractODEONBaseActivity {
    protected static final String TAG;

    static {
        TAG = RewardsMyCardHelpActivity.class.getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewards_my_card_help);
        configureNavigationHeader();
        TextView helpText = (TextView) findViewById(R.id.myCardHelpText);
        if (helpText != null) {
            helpText.setText(Html.fromHtml("<b>What is this?</b><br /><br />You can now store your ODEON Premi\u00e8re Club card on your iPhone using the ODEON iPhone App. This is in the form of a scannable barcode which can be used at the Box Office in place of your plastic card. That way you\u2019ll always have your ODEON Premi\u00e8re Club card with you so you don\u2019t miss out on earning your ODEON Points.<br /><br /><b>How do I use my card on the App?</b><br /><br />All you need to do is access the ODEON iPhone app next time you are at the Box Office, load your barcode and present your phone to our team members who will scan your phone when you book your tickets or buy your drinks and snacks. Your ODEON Points will then be added to your balance.<br /><b>Don't forget</b>, if you are making a booking via our app, you wont need to load your app card, as we have all of your details already.<br /><br /><b>Problems using your card on the App</b><br /><br /><b>Barcode won\u2019t scan</b><br /><br />If your barcode on the ODEON iPhone App won\u2019t scan, you may need to adjust the brightness settings on your iPhone. Simply access \u2018Settings\u2019 on your phone, and increase the brightness to ensure the barcode is clear and ready for scanning.<br /><br />If you continue to have problems with the barcode, don\u2019t worry, the card on your iPhone also contains your ODEON Premi\u00e8re Club card number, so you can still earn your ODEON Points in cinema. Alternatively, hold on to your ticket stubs and a team member can tell you more about how to claim your points. Plus don\u2019t forget, you can still use your plastic ODEON Premi\u00e8re Club card if you prefer.<br /><br /><b>Barcode won't load</b><br /><br />If your barcode on the ODEON iPhone App won\u2019t load, it may be that you do not have sufficient signal for us to be able to get the relevant information from your account. You can either access an area with a better signal, or switch to a WiFi connection. Alternatively, hold on to your ticket stubs and a team member can tell you more about how to claim your points. Plus don\u2019t forget, you can still use your plastic ODEON Premi\u00e8re Club card if you prefer."));
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
            navigationHeaderTitle.setText(getResources().getString(R.string.rewards_mycard_help_header_title));
        }
        Button backButton = (Button) findViewById(R.id.navigationHeaderCancel);
        if (backButton != null) {
            backButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    RewardsMyCardHelpActivity.this.finish();
                }
            });
        }
    }
}
