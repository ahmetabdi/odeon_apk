package uk.co.odeon.androidapp.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.R;

public class WebviewActivity extends AbstractODEONBaseActivity {
    protected static final String TAG;
    private String headerTitle;
    private WebView mWebView;
    private String title;
    private String url;

    static {
        TAG = WebviewActivity.class.getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        this.headerTitle = getIntent().getStringExtra(Constants.EXTRA_WEBVIEW_HEADER_TITLE);
        this.title = getIntent().getStringExtra(Constants.EXTRA_WEBVIEW_TITLE);
        this.url = getIntent().getStringExtra(Constants.EXTRA_WEBVIEW_URL);
        configureNavigationHeader();
        this.mWebView = (WebView) findViewById(R.id.webview);
        this.mWebView.getSettings().setBuiltInZoomControls(true);
        this.mWebView.getSettings().setSupportZoom(true);
        this.mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(WebviewActivity.TAG, "Full Url: " + url);
                view.loadUrl(url);
                return false;
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                WebviewActivity.this.showProgress(R.string.booking_progress, true);
            }

            public void onPageFinished(WebView view, String url) {
                WebviewActivity.this.hideProgress(true);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                WebviewActivity.this.hideProgress(true);
            }
        });
        this.mWebView.loadUrl(this.url);
    }

    protected void configureNavigationHeader() {
        if (this.headerTitle != null) {
            configureNavigationHeaderTitle(this.headerTitle);
        }
        if (this.title != null) {
            TextView titleText = (TextView) findViewById(R.id.titleText);
            if (titleText != null) {
                titleText.setVisibility(0);
                titleText.setText(this.title);
            }
        }
        configureNavigationHeaderCancel(new OnClickListener() {
            public void onClick(View v) {
                WebviewActivity.this.finish();
            }
        });
    }
}
