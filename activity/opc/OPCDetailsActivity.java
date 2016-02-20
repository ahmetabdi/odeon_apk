package uk.co.odeon.androidapp.activity.opc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.activity.WebviewActivity;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity.RootActivity;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;

public class OPCDetailsActivity extends AbstractODEONOPCBaseActivity {
    protected static final String TAG;
    protected final SharedPreferences customerDataPrefs;
    protected CheckBox filmtimesCheckbox;
    protected CheckBox offerCheckbox;
    private final OnClickListener onProceedClick;

    public OPCDetailsActivity() {
        this.customerDataPrefs = ODEONApplication.getInstance().getCustomerDataPrefs();
        this.offerCheckbox = null;
        this.filmtimesCheckbox = null;
        this.onProceedClick = new OnClickListener() {
            public void onClick(View v) {
                OPCDetailsActivity.this.customerDataPrefs.edit().putString(Constants.CUSTOMER_PREFS_OFFERS, OPCDetailsActivity.this.getOfferCheckbox().isChecked() ? "1" : "0").putString(Constants.CUSTOMER_PREFS_FILMTIMES, OPCDetailsActivity.this.getFilmtimesCheckbox().isChecked() ? "1" : "0").commit();
                OPCDetailsActivity.this.startActivity(new Intent(v.getContext(), OPCJoinPaymentActivity.class));
            }
        };
    }

    static {
        TAG = OPCDetailsActivity.class.getSimpleName();
    }

    public CheckBox getOfferCheckbox() {
        if (this.offerCheckbox == null) {
            this.offerCheckbox = (CheckBox) findViewById(R.id.offerCheckbox);
        }
        return this.offerCheckbox;
    }

    public CheckBox getFilmtimesCheckbox() {
        if (this.filmtimesCheckbox == null) {
            this.filmtimesCheckbox = (CheckBox) findViewById(R.id.filmtimesCheckbox);
        }
        return this.filmtimesCheckbox;
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opc_details);
        if (!(savedInstanceState == null || savedInstanceState.getString(Constants.CUSTOMER_PREFS_OFFERS) == null)) {
            getOfferCheckbox().setChecked(savedInstanceState.getString(Constants.CUSTOMER_PREFS_OFFERS).equals("1"));
        }
        if (!(savedInstanceState == null || savedInstanceState.getString(Constants.CUSTOMER_PREFS_FILMTIMES) == null)) {
            getFilmtimesCheckbox().setChecked(savedInstanceState.getString(Constants.CUSTOMER_PREFS_FILMTIMES).equals("1"));
        }
        configureNavigationHeader();
        setTextInView();
        setDataInView();
        Button proceedButton = (Button) findViewById(R.id.proceedButton);
        if (proceedButton != null) {
            proceedButton.setOnClickListener(this.onProceedClick);
        }
    }

    public void onResume() {
        super.onResume();
        String offers = this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_OFFERS, null);
        String filmtimes = this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_FILMTIMES, null);
        if (offers != null) {
            getOfferCheckbox().setChecked(offers.equals("1"));
        }
        if (filmtimes != null) {
            getFilmtimesCheckbox().setChecked(filmtimes.equals("1"));
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Constants.CUSTOMER_PREFS_OFFERS, getOfferCheckbox().isChecked() ? "1" : "0");
        savedInstanceState.putString(Constants.CUSTOMER_PREFS_FILMTIMES, getFilmtimesCheckbox().isChecked() ? "1" : "0");
    }

    protected void configureNavigationHeader() {
        configureNavigationHeaderTitle((int) R.string.opc_header_title);
        configureNavigationHeaderCancel(new OnClickListener() {
            public void onClick(View v) {
                OPCDetailsActivity.this.finish();
            }
        });
    }

    private void setTextInView() {
        TextView footer = (TextView) findViewById(R.id.detailsFooter);
        String tcLinkStr = getResources().getString(R.string.opc_details_t_and_c);
        String ppLinkStr = getResources().getString(R.string.opc_details_privacy_policy);
        SpannableString footerText = new SpannableString(getResources().getString(R.string.opc_details_footer, new Object[]{tcLinkStr, ppLinkStr}));
        ClickableSpan tcClickableSpan = new ClickableSpan() {
            public void onClick(View view) {
                Intent webviewIntent = new Intent(OPCDetailsActivity.this, WebviewActivity.class);
                webviewIntent.putExtra(Constants.EXTRA_WEBVIEW_HEADER_TITLE, OPCDetailsActivity.this.getString(R.string.opc_header_title_webview));
                webviewIntent.putExtra(Constants.EXTRA_WEBVIEW_TITLE, OPCDetailsActivity.this.getString(R.string.opc_t_and_c_long));
                webviewIntent.putExtra(Constants.EXTRA_WEBVIEW_URL, Constants.formatLocationUrl(Constants.URL_OPC_TERMS_AND_CONDITIONS));
                OPCDetailsActivity.this.startActivity(webviewIntent);
            }
        };
        ClickableSpan ppClickableSpan = new ClickableSpan() {
            public void onClick(View view) {
                Intent webviewIntent = new Intent(OPCDetailsActivity.this, WebviewActivity.class);
                webviewIntent.putExtra(Constants.EXTRA_WEBVIEW_HEADER_TITLE, OPCDetailsActivity.this.getString(R.string.opc_header_title_webview));
                webviewIntent.putExtra(Constants.EXTRA_WEBVIEW_TITLE, OPCDetailsActivity.this.getString(R.string.opc_privacy_policy));
                webviewIntent.putExtra(Constants.EXTRA_WEBVIEW_URL, Constants.formatLocationUrl(Constants.URL_PRIVACY_POLICY));
                OPCDetailsActivity.this.startActivity(webviewIntent);
            }
        };
        int tcFrom = footerText.toString().indexOf(tcLinkStr);
        int tcTo = tcFrom + tcLinkStr.length();
        int ppFrom = footerText.toString().indexOf(ppLinkStr);
        int ppTo = ppFrom + ppLinkStr.length();
        footerText.setSpan(tcClickableSpan, tcFrom, tcTo, 0);
        footerText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.t_and_c_link)), tcFrom, tcTo, 0);
        footerText.setSpan(ppClickableSpan, ppFrom, ppTo, 0);
        footerText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.t_and_c_link)), ppFrom, ppTo, 0);
        footer.setMovementMethod(LinkMovementMethod.getInstance());
        footer.setHighlightColor(0);
        footer.setText(footerText);
    }

    private void setDataInView() {
        TextView choosenPackageText = (TextView) findViewById(R.id.chosenPackageText);
        if (choosenPackageText != null) {
            switch (this.customerDataPrefs.getInt(Constants.CUSTOMER_PREFS_OPC_PACKAGE, -1)) {
                case AmazingListView.PINNED_HEADER_VISIBLE /*1*/:
                    choosenPackageText.setText(getString(R.string.opc_package_base_name) + " " + getString(R.string.opc_package_1_name));
                    break;
                case AmazingListView.PINNED_HEADER_PUSHED_UP /*2*/:
                    choosenPackageText.setText(getString(R.string.opc_package_base_name) + " " + getString(R.string.opc_package_2_name));
                    break;
                case RootActivity.TYPE_RIGHT /*3*/:
                    choosenPackageText.setText(getString(R.string.opc_package_base_name) + " " + getString(R.string.opc_package_3_name));
                    break;
            }
        }
        TextView costText = (TextView) findViewById(R.id.costText);
        if (costText != null) {
            switch (this.customerDataPrefs.getInt(Constants.CUSTOMER_PREFS_OPC_PACKAGE, -1)) {
                case AmazingListView.PINNED_HEADER_VISIBLE /*1*/:
                    costText.setText(getString(R.string.opc_package_1_cost));
                    break;
                case AmazingListView.PINNED_HEADER_PUSHED_UP /*2*/:
                    costText.setText(getString(R.string.opc_package_2_cost));
                    break;
                case RootActivity.TYPE_RIGHT /*3*/:
                    costText.setText(getString(R.string.opc_package_3_cost));
                    break;
            }
        }
        TextView nameText = (TextView) findViewById(R.id.nameText);
        if (nameText != null) {
            nameText.setText(new StringBuilder(String.valueOf(this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_FIRSTNAME, ""))).append(" ").append(this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_LASTNAME, "")).toString());
        }
        TextView emailText = (TextView) findViewById(R.id.emailText);
        if (emailText != null) {
            emailText.setText(this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_EMAIL, ""));
        }
    }
}
