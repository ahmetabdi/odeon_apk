package uk.co.odeon.androidapp.activity.opc;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.Constants.APP_LOCATION;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.activity.WebviewActivity;

public class OPCChoosePackageActivity extends AbstractODEONOPCBaseActivity {
    protected static final String TAG;
    private TextView loggedInText;
    protected final ODEONApplication odeonApplication;

    public OPCChoosePackageActivity() {
        this.odeonApplication = ODEONApplication.getInstance();
        this.loggedInText = null;
    }

    static {
        TAG = OPCChoosePackageActivity.class.getSimpleName();
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
        setContentView(R.layout.opc_choose_package);
        configureNavigationHeader();
        setTextInView();
        RelativeLayout opcPackage1Layout = (RelativeLayout) findViewById(R.id.opcPackage1Layout);
        if (opcPackage1Layout != null) {
            opcPackage1Layout.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    OPCChoosePackageActivity.this.performChoosePackage(11);
                }
            });
        }
        RelativeLayout opcPackage2Layout = (RelativeLayout) findViewById(R.id.opcPackage2Layout);
        if (opcPackage2Layout != null) {
            if (this.odeonApplication.getChoosenLocation().equals(APP_LOCATION.ire)) {
                opcPackage2Layout.setVisibility(8);
            } else {
                opcPackage2Layout.setVisibility(0);
                opcPackage2Layout.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        OPCChoosePackageActivity.this.performChoosePackage(12);
                    }
                });
            }
        }
        RelativeLayout opcPackage3Layout = (RelativeLayout) findViewById(R.id.opcPackage3Layout);
        if (opcPackage3Layout != null) {
            opcPackage3Layout.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    OPCChoosePackageActivity.this.performChoosePackage(13);
                }
            });
        }
        TextView summaryText = (TextView) findViewById(R.id.summaryText);
        if (summaryText != null) {
            summaryText.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    OPCChoosePackageActivity.this.startWebviewActivity(OPCChoosePackageActivity.this.getString(R.string.opc_header_title_webview), OPCChoosePackageActivity.this.getString(R.string.opc_summary), Constants.formatLocationUrl(Constants.URL_OPC_SUMMARY));
                }
            });
        }
        TextView privacyText = (TextView) findViewById(R.id.privacyText);
        if (privacyText != null) {
            privacyText.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    OPCChoosePackageActivity.this.startWebviewActivity(OPCChoosePackageActivity.this.getString(R.string.opc_header_title_webview), OPCChoosePackageActivity.this.getString(R.string.opc_privacy_policy), Constants.formatLocationUrl(Constants.URL_PRIVACY_POLICY));
                }
            });
        }
        TextView tAndcText = (TextView) findViewById(R.id.tAndcText);
        if (tAndcText != null) {
            tAndcText.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    OPCChoosePackageActivity.this.startWebviewActivity(OPCChoosePackageActivity.this.getString(R.string.opc_header_title_webview), OPCChoosePackageActivity.this.getString(R.string.opc_t_and_c_long), Constants.formatLocationUrl(Constants.URL_OPC_TERMS_AND_CONDITIONS));
                }
            });
        }
    }

    public void onResume() {
        super.onResume();
        if (getIntent().getAction() != null && getIntent().getAction().equals(Constants.ACTION_END_OF_OPC_JOIN)) {
            finish();
        } else if (getIntent().getAction() != null && getIntent().getAction().equals(Constants.ACTION_RESTART_OPC_JOIN)) {
            finish();
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
            navigationHeaderTitle.setText(getString(R.string.opc_header_title));
        }
        Button backButton = (Button) findViewById(R.id.navigationHeaderCancel);
        if (backButton != null) {
            backButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    OPCChoosePackageActivity.this.finish();
                }
            });
        }
    }

    private void setTextInView() {
        TextView package1TitleTextView = (TextView) findViewById(R.id.opcPackage1Title);
        if (this.odeonApplication.getChoosenLocation().equals(APP_LOCATION.ire)) {
            package1TitleTextView.setText(getString(R.string.opc_package_1_title, new Object[]{getString(R.string.opc_package_1_cost_ire)}));
        } else {
            package1TitleTextView.setText(getString(R.string.opc_package_1_title, new Object[]{getString(R.string.opc_package_1_cost)}));
        }
        ((TextView) findViewById(R.id.opcPackage1Description)).setText(getString(R.string.opc_package_1_description, new Object[]{getString(R.string.opc_package_1_name)}));
        TextView package2TitleTextView = (TextView) findViewById(R.id.opcPackage2Title);
        if (this.odeonApplication.getChoosenLocation().equals(APP_LOCATION.ire)) {
            package2TitleTextView.setText(getString(R.string.opc_package_2_title, new Object[]{getString(R.string.opc_package_2_cost_ire)}));
        } else {
            package2TitleTextView.setText(getString(R.string.opc_package_2_title, new Object[]{getString(R.string.opc_package_2_cost)}));
        }
        ((TextView) findViewById(R.id.opcPackage2Description)).setText(getString(R.string.opc_package_2_description, new Object[]{getString(R.string.opc_package_2_name)}));
        TextView package3TitleTextView = (TextView) findViewById(R.id.opcPackage3Title);
        if (this.odeonApplication.getChoosenLocation().equals(APP_LOCATION.ire)) {
            package3TitleTextView.setText(getString(R.string.opc_package_3_title, new Object[]{getString(R.string.opc_package_3_cost_ire)}));
        } else {
            package3TitleTextView.setText(getString(R.string.opc_package_3_title, new Object[]{getString(R.string.opc_package_3_cost)}));
        }
        TextView package3DescriptionTextView = (TextView) findViewById(R.id.opcPackage3Description);
        String package3DescriptionBoldStr = getString(R.string.opc_package_3_description_bold);
        SpannableString package3Text = new SpannableString(getString(R.string.opc_package_3_description, new Object[]{getString(R.string.opc_package_3_name), package3DescriptionBoldStr}));
        int from = package3Text.toString().indexOf(package3DescriptionBoldStr);
        package3Text.setSpan(new StyleSpan(1), from, from + package3DescriptionBoldStr.length(), 0);
        package3DescriptionTextView.setText(package3Text);
    }

    protected void performChoosePackage(int opcPackage) {
        ODEONApplication.getInstance().getCustomerDataPrefs().edit().putInt(Constants.CUSTOMER_PREFS_OPC_PACKAGE, opcPackage).commit();
        startActivity(new Intent(this, OPCNameDetailsActivity.class));
    }
}
