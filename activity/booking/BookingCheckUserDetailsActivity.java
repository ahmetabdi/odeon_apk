package uk.co.odeon.androidapp.activity.booking;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.apache.http.util.EncodingUtils;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.model.BookingProcess;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;
import uk.co.odeon.androidapp.util.calendar.CalendarEntry;
import uk.co.odeon.androidapp.util.calendar.CalendarEntryCreator;
import uk.co.odeon.androidapp.util.calendar.CalendarEntryCreatorFactory;

public class BookingCheckUserDetailsActivity extends AbstractODEONBookingBaseActivity {
    protected static final String TAG;
    private final BookingProcess bookingProcess;
    private final WebViewClient checkUserWebViewClient;
    protected boolean finishedBooking;
    protected boolean loadingFinished;
    private WebView mWebView;
    protected int oldOrientation;

    class JavaScriptInterface {
        JavaScriptInterface() {
        }

        public void getFinishedBookingWithError(String value, String msg) {
            if (value != null && value.equalsIgnoreCase(BookingCheckUserDetailsActivity.this.getString(R.string.booking_finished_or_errors_yes))) {
                ODEONApplication.trackEvent("Showtimes-book now Booking Failed", "Response", "");
                BookingCheckUserDetailsActivity.this.showBookingAbortAlert(msg);
            }
        }

        public void getFinishedBooking(String value) {
            if (value != null && value.equalsIgnoreCase(BookingCheckUserDetailsActivity.this.getString(R.string.booking_finished_or_errors_yes))) {
                BookingCheckUserDetailsActivity.this.finishedBooking = true;
                BookingCheckUserDetailsActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        ODEONApplication.trackEvent("Showtimes-book now Booking Complete", "Response", "");
                        BookingCheckUserDetailsActivity.this.configureNavigationHeader();
                    }
                });
            }
        }

        public void createCalendarEntry(String filmTitle, String siteName, String dateTimePerfStartStr, String dateTimePerfEndStr) {
            Log.i(BookingCheckUserDetailsActivity.TAG, "Cal: " + filmTitle + " +++ " + siteName + " +++ " + dateTimePerfStartStr + " +++ " + dateTimePerfEndStr);
            CalendarEntryCreator calCreator = new CalendarEntryCreatorFactory().getCalendarEntryCreator(BookingCheckUserDetailsActivity.this.getDialogContext());
            long perfMillisStart = parsePerfDate(dateTimePerfStartStr);
            long perfMillisEnd = parsePerfDate(dateTimePerfEndStr);
            if (perfMillisStart < 0 || perfMillisEnd < 0) {
                Log.w(BookingCheckUserDetailsActivity.TAG, "Invalid performance start/end date " + dateTimePerfStartStr + " --- " + dateTimePerfEndStr);
                Toast.makeText(BookingCheckUserDetailsActivity.this.getDialogContext(), "Ooops! No valid performance date known.", 1).show();
                return;
            }
            CalendarEntry calEntry = new CalendarEntry();
            calEntry.setTitle(filmTitle);
            calEntry.setBeginTimeMillis(perfMillisStart);
            if (perfMillisStart == perfMillisEnd) {
                perfMillisEnd += 7200000;
            }
            calEntry.setEndTimeMillis(perfMillisEnd);
            calEntry.setAllDay(Boolean.valueOf(false));
            calEntry.setLocation(siteName);
            calCreator.openCalendarEntryDialog(calEntry, true);
        }

        private long parsePerfDate(String perfDateTimeStr) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.UK);
            Calendar releaseDateCal = Calendar.getInstance();
            try {
                Date perfDate = sdf.parse(perfDateTimeStr);
                Log.d(BookingCheckUserDetailsActivity.TAG, "Parsed perf date: " + perfDate);
                releaseDateCal.setTime(perfDate);
                releaseDateCal.set(14, 0);
                int year = releaseDateCal.get(1);
                if (year >= 2012 && year <= 2025) {
                    return releaseDateCal.getTimeInMillis();
                }
                throw new ParseException("Year " + year + " is not in valid range", 0);
            } catch (ParseException pe) {
                Log.e(BookingCheckUserDetailsActivity.TAG, String.format("Can't parse date '%s': %s", new Object[]{perfDateTimeStr, pe.getMessage()}), pe);
                return -1;
            }
        }

        public void continueLink() {
            BookingCheckUserDetailsActivity.this.finish();
        }
    }

    public BookingCheckUserDetailsActivity() {
        this.bookingProcess = BookingProcess.getInstance();
        this.loadingFinished = true;
        this.finishedBooking = false;
        this.checkUserWebViewClient = new WebViewClient() {
            boolean redirect;

            {
                this.redirect = false;
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (BookingCheckUserDetailsActivity.this.checkEndOfBooking(url)) {
                    BookingCheckUserDetailsActivity.this.performEndOfBooking();
                } else {
                    if (!BookingCheckUserDetailsActivity.this.loadingFinished) {
                        this.redirect = true;
                    }
                    BookingCheckUserDetailsActivity.this.loadingFinished = false;
                    Log.d(BookingCheckUserDetailsActivity.TAG, "Full Url: " + url);
                    view.loadUrl(url);
                }
                return false;
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!BookingCheckUserDetailsActivity.this.checkEndOfBooking(url)) {
                    BookingCheckUserDetailsActivity.this.loadingFinished = false;
                    BookingCheckUserDetailsActivity.this.suspendOrientation();
                    BookingCheckUserDetailsActivity.this.showProgress(R.string.booking_progress, true);
                }
            }

            public void onPageFinished(WebView view, String url) {
                if (!this.redirect) {
                    BookingCheckUserDetailsActivity.this.loadingFinished = true;
                }
                if (!BookingCheckUserDetailsActivity.this.loadingFinished || this.redirect) {
                    this.redirect = false;
                    return;
                }
                BookingCheckUserDetailsActivity.this.hideProgress(true);
                BookingCheckUserDetailsActivity.this.releaseOrientation();
                BookingCheckUserDetailsActivity.this.mWebView.loadUrl("javascript:(function() { AndroidInterface.getFinishedBookingWithError(finishedBookingWithError, bookingErrorMsg); })()");
                BookingCheckUserDetailsActivity.this.mWebView.loadUrl("javascript:(function() { AndroidInterface.getFinishedBooking(finishedBooking); })()");
            }
        };
    }

    static {
        TAG = BookingCheckUserDetailsActivity.class.getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_check_user_details);
        this.oldOrientation = getResources().getConfiguration().orientation;
        configureNavigationHeader();
        this.mWebView = (WebView) findViewById(R.id.bookingWebview);
        this.mWebView.getSettings().setBuiltInZoomControls(true);
        this.mWebView.getSettings().setSupportZoom(true);
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.getSettings().setSaveFormData(false);
        this.mWebView.addJavascriptInterface(new JavaScriptInterface(), "AndroidInterface");
        this.mWebView.setWebViewClient(this.checkUserWebViewClient);
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("url")) {
            this.mWebView.loadUrl(getIntent().getExtras().getString("url"));
        } else if (this.bookingProcess.bookingSessionHash == null) {
            showBookingAlert(getResources().getString(R.string.booking_error_no_data));
        } else if (buildPostParameters() != null) {
            this.mWebView.postUrl(Constants.formatLocationUrl(Constants.BOOKING_URL_CHECK_USER_DETAILS), buildPostParameters());
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        this.oldOrientation = newConfig.orientation;
        super.onConfigurationChanged(newConfig);
    }

    public void onBackPressed() {
        if (this.finishedBooking) {
            performEndOfBooking();
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
        if (this.finishedBooking) {
            configureNavigationHeaderTitle((int) R.string.booking_header_confirmation);
            configureNavigationHeaderCancel(getString(R.string.booking_header_button_close), R.drawable.nav_bar_btn_4_round, new OnClickListener() {
                public void onClick(View v) {
                    BookingCheckUserDetailsActivity.this.performEndOfBooking();
                }
            });
            return;
        }
        configureNavigationHeaderTitle((int) R.string.booking_header_payment);
        configureNavigationHeaderCancel(getString(R.string.booking_header_button_booking), new OnClickListener() {
            public void onClick(View v) {
                BookingCheckUserDetailsActivity.this.finish();
            }
        });
    }

    private boolean checkEndOfBooking(String url) {
        if (url == null || url.toLowerCase(Locale.UK).indexOf(getString(R.string.booking_finished_url_param)) < 0) {
            return false;
        }
        return true;
    }

    private byte[] buildPostParameters() {
        try {
            return EncodingUtils.getBytes("apiKey=" + URLEncoder.encode(Constants.API_KEY) + "&bookingSessionHash=" + URLEncoder.encode(this.bookingProcess.bookingSessionHash) + "&bookingSessionId=" + URLEncoder.encode(this.bookingProcess.bookingSessionId) + "&firstname=" + URLEncoder.encode(this.bookingProcess.firstname) + "&lastname=" + URLEncoder.encode(this.bookingProcess.lastname) + "&title=" + URLEncoder.encode(this.bookingProcess.title) + "&email=" + URLEncoder.encode(this.bookingProcess.email) + "&emailConfirm=" + URLEncoder.encode(this.bookingProcess.email) + "&contactNumber=" + (this.bookingProcess.contactNumber != null ? URLEncoder.encode(this.bookingProcess.contactNumber) : "") + "&terms=1", "BASE64");
        } catch (Exception e) {
            showBookingAlert(getResources().getString(R.string.booking_error_no_data));
            return null;
        }
    }
}
