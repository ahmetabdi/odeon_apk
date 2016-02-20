package uk.co.odeon.androidapp.activity.opc;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.net.URLEncoder;
import org.apache.http.util.EncodingUtils;
import twitter4j.conf.PropertyConfiguration;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.provider.SiteContent.SiteColumns;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;

public class OPCJoinPaymentActivity extends AbstractODEONOPCBaseActivity {
    protected static final String TAG;
    protected final SharedPreferences customerDataPrefs;
    private final WebViewClient joinPaymentWebViewClient;
    protected boolean loadingFinished;
    private WebView mWebView;
    protected int oldOrientation;
    protected boolean showCloseButton;

    class JavaScriptInterface {
        JavaScriptInterface() {
        }

        public void getShowCloseButton(String value) {
            if (value != null && value.equalsIgnoreCase(OPCJoinPaymentActivity.this.getString(R.string.opc_show_close_button_yes))) {
                OPCJoinPaymentActivity.this.showCloseButton = true;
                OPCJoinPaymentActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        OPCJoinPaymentActivity.this.configureNavigationHeader();
                    }
                });
            }
        }
    }

    public OPCJoinPaymentActivity() {
        this.customerDataPrefs = ODEONApplication.getInstance().getCustomerDataPrefs();
        this.loadingFinished = true;
        this.showCloseButton = false;
        this.joinPaymentWebViewClient = new WebViewClient() {
            boolean redirect;

            {
                this.redirect = false;
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (OPCJoinPaymentActivity.this.checkContinueWithLogin(url)) {
                    OPCJoinPaymentActivity.this.handleEndOfJoin();
                } else {
                    if (!OPCJoinPaymentActivity.this.loadingFinished) {
                        this.redirect = true;
                    }
                    OPCJoinPaymentActivity.this.loadingFinished = false;
                    view.loadUrl(url);
                }
                return false;
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!OPCJoinPaymentActivity.this.checkContinueWithLogin(url)) {
                    OPCJoinPaymentActivity.this.loadingFinished = false;
                    OPCJoinPaymentActivity.this.suspendOrientation();
                    OPCJoinPaymentActivity.this.showProgress(R.string.opc_progress, true);
                }
            }

            public void onPageFinished(WebView view, String url) {
                if (!this.redirect) {
                    OPCJoinPaymentActivity.this.loadingFinished = true;
                }
                if (!OPCJoinPaymentActivity.this.loadingFinished || this.redirect) {
                    this.redirect = false;
                    return;
                }
                OPCJoinPaymentActivity.this.hideProgress(true);
                OPCJoinPaymentActivity.this.releaseOrientation();
                OPCJoinPaymentActivity.this.mWebView.loadUrl("javascript:(function() { AndroidInterface.getShowCloseButton(showCloseButton); })()");
            }
        };
    }

    static {
        TAG = OPCJoinPaymentActivity.class.getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opc_join_payment);
        this.oldOrientation = getResources().getConfiguration().orientation;
        configureNavigationHeader();
        this.mWebView = (WebView) findViewById(R.id.joinPaymentWebview);
        this.mWebView.getSettings().setBuiltInZoomControls(true);
        this.mWebView.getSettings().setSupportZoom(true);
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.getSettings().setSaveFormData(false);
        this.mWebView.addJavascriptInterface(new JavaScriptInterface(), "AndroidInterface");
        this.mWebView.setWebViewClient(this.joinPaymentWebViewClient);
        this.mWebView.postUrl(Constants.formatLocationUrl(Constants.OPC_URL_JOIN_PAYMENT_DETAILS), buildPostParameters());
    }

    public void onConfigurationChanged(Configuration newConfig) {
        this.oldOrientation = newConfig.orientation;
        super.onConfigurationChanged(newConfig);
    }

    public void onBackPressed() {
        if (this.showCloseButton) {
            ODEONApplication.getInstance().saveCustomerLoginInPrefs(this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_EMAIL, ""), this.customerDataPrefs.getString(PropertyConfiguration.PASSWORD, ""));
            handleEndOfJoin();
        } else if (this.loadingFinished) {
            super.onBackPressed();
        }
    }

    public void suspendOrientation() {
        switch (this.oldOrientation) {
            case AmazingListView.PINNED_HEADER_VISIBLE /*1*/:
                setRequestedOrientation(1);
            case AmazingListView.PINNED_HEADER_PUSHED_UP /*2*/:
                setRequestedOrientation(0);
            default:
                setRequestedOrientation(-1);
        }
    }

    public void releaseOrientation() {
        setRequestedOrientation(-1);
    }

    protected void configureNavigationHeader() {
        if (this.showCloseButton) {
            configureNavigationHeaderTitle((int) R.string.opc_header_title);
            configureNavigationHeaderCancel(getString(R.string.opc_header_button_close), R.drawable.nav_bar_btn_4_round, new OnClickListener() {
                public void onClick(View v) {
                    OPCJoinPaymentActivity.this.handleEndOfJoin();
                }
            });
            return;
        }
        configureNavigationHeaderTitle((int) R.string.opc_header_title);
        configureNavigationHeaderCancel(new OnClickListener() {
            public void onClick(View v) {
                OPCJoinPaymentActivity.this.finish();
            }
        });
    }

    private boolean checkContinueWithLogin(String url) {
        if (url == null || url.toLowerCase().indexOf(getString(R.string.opc_continue_with_login_url_param)) < 0) {
            return false;
        }
        return true;
    }

    private byte[] buildPostParameters() {
        return EncodingUtils.getBytes("apiKey=" + URLEncoder.encode(Constants.API_KEY) + "&customerFirstname=" + URLEncoder.encode(this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_FIRSTNAME, "")) + "&customerSurname=" + URLEncoder.encode(this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_LASTNAME, "")) + "&customerDateOfBirth=" + URLEncoder.encode(this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_DOB, "")) + "&customerEmail=" + URLEncoder.encode(this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_EMAIL, "")) + "&customerHouseNo=" + URLEncoder.encode(this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_HOUSE, "")) + "&customerStreet=" + URLEncoder.encode(this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_STREET, "")) + "&customerTown=" + URLEncoder.encode(this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_CITY, "")) + "&customerPostcode=" + URLEncoder.encode(this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_POSTCODE, "")) + "&customerContactPhone=" + URLEncoder.encode(this.customerDataPrefs.getString(SiteColumns.PHONE, "")) + "&customerSecurityQuestion=" + URLEncoder.encode(getString(R.string.form_field_security_question_label)) + "&customerPassword=" + URLEncoder.encode(this.customerDataPrefs.getString(PropertyConfiguration.PASSWORD, "")) + "&customerFavouriteCinemaID=" + String.valueOf(this.customerDataPrefs.getInt(Constants.CUSTOMER_PREFS_CINEMA_ID, -1)) + "&customerOffersOption=" + this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_OFFERS, "") + "&customerCinemailOption=" + this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_FILMTIMES, "") + "&customerPackage=" + String.valueOf(this.customerDataPrefs.getInt(Constants.CUSTOMER_PREFS_OPC_PACKAGE, -1)), "BASE64");
    }

    private void handleEndOfJoin() {
        ODEONApplication.getInstance().saveCustomerLoginInPrefs(this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_EMAIL, ""), this.customerDataPrefs.getString(PropertyConfiguration.PASSWORD, ""));
        performEndOfJoin();
    }
}
