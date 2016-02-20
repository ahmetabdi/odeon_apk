package uk.co.odeon.androidapp.activity.booking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.model.BookingProcess;
import uk.co.odeon.androidapp.provider.SiteContent.SiteColumns;
import uk.co.odeon.androidapp.task.BookingInitTask;
import uk.co.odeon.androidapp.task.TaskTarget;

public class BookingNoLoginMailDetailsActivity extends AbstractODEONBookingBaseActivity implements TaskTarget<Boolean> {
    protected static final String TAG;
    protected BookingInitTask bookingInitTask;
    protected final BookingProcess bookingProcess;
    protected int cinemaId;
    protected EditText confirmEmailText;
    protected final SharedPreferences customerDataPrefs;
    protected EditText emailText;
    private final OnClickListener onContinueClick;
    protected String performanceId;
    protected EditText phoneText;

    public BookingNoLoginMailDetailsActivity() {
        this.bookingProcess = BookingProcess.getInstance();
        this.customerDataPrefs = ODEONApplication.getInstance().getCustomerDataPrefs();
        this.bookingInitTask = null;
        this.performanceId = null;
        this.cinemaId = 0;
        this.emailText = null;
        this.confirmEmailText = null;
        this.phoneText = null;
        this.onContinueClick = new OnClickListener() {
            public void onClick(View v) {
                String email = BookingNoLoginMailDetailsActivity.this.getEmailTextView().getText().toString().trim();
                String confirmEmail = BookingNoLoginMailDetailsActivity.this.getConfirmEmailTextView().getText().toString().trim();
                String phone = BookingNoLoginMailDetailsActivity.this.getPhoneTextView().getText().toString().trim();
                boolean hasError = false;
                if (email.length() <= 0) {
                    BookingNoLoginMailDetailsActivity.this.getEmailTextView().setError(BookingNoLoginMailDetailsActivity.this.getString(R.string.error_form_mandantory, new Object[]{BookingNoLoginMailDetailsActivity.this.getString(R.string.form_field_email)}));
                    hasError = true;
                } else if (!ODEONApplication.isValidEmail(email)) {
                    BookingNoLoginMailDetailsActivity.this.getEmailTextView().setError(BookingNoLoginMailDetailsActivity.this.getString(R.string.error_form_invalid, new Object[]{BookingNoLoginMailDetailsActivity.this.getString(R.string.form_field_email)}));
                    hasError = true;
                }
                if (confirmEmail.length() <= 0) {
                    BookingNoLoginMailDetailsActivity.this.getConfirmEmailTextView().setError(BookingNoLoginMailDetailsActivity.this.getString(R.string.error_form_mandantory, new Object[]{BookingNoLoginMailDetailsActivity.this.getString(R.string.form_field_confirm_email)}));
                    hasError = true;
                } else if (!confirmEmail.equals(email)) {
                    BookingNoLoginMailDetailsActivity.this.getConfirmEmailTextView().setError(BookingNoLoginMailDetailsActivity.this.getString(R.string.error_form_match, new Object[]{BookingNoLoginMailDetailsActivity.this.getString(R.string.form_field_confirm_email)}));
                    hasError = true;
                }
                if (!hasError) {
                    ODEONApplication.trackEvent("Showtimes-book now Without Logging In Details", "Click", "");
                    BookingNoLoginMailDetailsActivity.this.bookingProcess.email = email;
                    BookingNoLoginMailDetailsActivity.this.bookingProcess.contactNumber = phone;
                    BookingNoLoginMailDetailsActivity.this.customerDataPrefs.edit().putString(Constants.CUSTOMER_PREFS_EMAIL, email).putString(Constants.CUSTOMER_PREFS_EMAIL_CONFIRM, confirmEmail).putString(SiteColumns.PHONE, phone).commit();
                    BookingNoLoginMailDetailsActivity.this.bookingInitTask = new BookingInitTask(BookingNoLoginMailDetailsActivity.this);
                    BookingNoLoginMailDetailsActivity.this.bookingInitTask.execute(new String[]{BookingNoLoginMailDetailsActivity.this.performanceId, String.valueOf(BookingNoLoginMailDetailsActivity.this.cinemaId), null, null});
                    BookingNoLoginMailDetailsActivity.this.showProgress(R.string.booking_progress, true);
                }
            }
        };
    }

    static {
        TAG = BookingNoLoginMailDetailsActivity.class.getSimpleName();
    }

    public EditText getEmailTextView() {
        if (this.emailText == null) {
            this.emailText = (EditText) findViewById(R.id.emailText);
        }
        return this.emailText;
    }

    public EditText getConfirmEmailTextView() {
        if (this.confirmEmailText == null) {
            this.confirmEmailText = (EditText) findViewById(R.id.confirmEmailText);
        }
        return this.confirmEmailText;
    }

    public EditText getPhoneTextView() {
        if (this.phoneText == null) {
            this.phoneText = (EditText) findViewById(R.id.phoneText);
        }
        return this.phoneText;
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_nologin_mail_details);
        getWindow().setSoftInputMode(3);
        if (!(savedInstanceState == null || savedInstanceState.getString(Constants.CUSTOMER_PREFS_EMAIL) == null)) {
            getEmailTextView().setText(savedInstanceState.getString(Constants.CUSTOMER_PREFS_EMAIL));
        }
        if (!(savedInstanceState == null || savedInstanceState.getString(Constants.CUSTOMER_PREFS_EMAIL_CONFIRM) == null)) {
            getConfirmEmailTextView().setText(savedInstanceState.getString(Constants.CUSTOMER_PREFS_EMAIL_CONFIRM));
        }
        if (!(savedInstanceState == null || savedInstanceState.getString(SiteColumns.PHONE) == null)) {
            getPhoneTextView().setText(savedInstanceState.getString(SiteColumns.PHONE));
        }
        this.performanceId = getIntent().getStringExtra(Constants.EXTRA_PERFORMANCE_ID);
        this.cinemaId = getIntent().getIntExtra(Constants.EXTRA_CINEMA_ID, 0);
        configureNavigationHeader();
        Button continueButton = (Button) findViewById(R.id.continueButton);
        if (continueButton != null) {
            continueButton.setOnClickListener(this.onContinueClick);
        }
        this.bookingInitTask = (BookingInitTask) getLastNonConfigurationInstance();
        if (this.bookingInitTask != null) {
            showProgress(R.string.booking_progress, true);
            this.bookingInitTask.attach(this);
        }
    }

    public void onResume() {
        super.onResume();
        this.bookingProcess.email = this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_EMAIL, null);
        String emailConfirm = this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_EMAIL_CONFIRM, null);
        this.bookingProcess.contactNumber = this.customerDataPrefs.getString(SiteColumns.PHONE, null);
        if (this.bookingProcess.email != null) {
            getEmailTextView().setText(this.bookingProcess.email);
        }
        if (emailConfirm != null) {
            getConfirmEmailTextView().setText(emailConfirm);
        }
        if (this.bookingProcess.contactNumber != null) {
            getPhoneTextView().setText(this.bookingProcess.contactNumber);
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Constants.CUSTOMER_PREFS_EMAIL, getEmailTextView().getText().toString());
        savedInstanceState.putString(Constants.CUSTOMER_PREFS_EMAIL_CONFIRM, getConfirmEmailTextView().getText().toString());
        savedInstanceState.putString(SiteColumns.PHONE, getPhoneTextView().getText().toString());
    }

    public Object onRetainNonConfigurationInstance() {
        if (this.bookingInitTask != null) {
            this.bookingInitTask.detach();
        }
        return this.bookingInitTask;
    }

    public void setTaskResult(Boolean taskResult) {
        hideProgress(true);
        if (taskResult != null) {
            startActivity(new Intent(this, BookingSectionSelectionActivity.class));
        } else {
            showBookingAlert(BookingProcess.getInstance().hasError() ? BookingProcess.getInstance().getLastError(true) : null);
        }
    }

    protected void configureNavigationHeader() {
        configureNavigationHeaderTitle((int) R.string.booking_header_details);
        configureNavigationHeaderCancel(new OnClickListener() {
            public void onClick(View v) {
                BookingNoLoginMailDetailsActivity.this.finish();
            }
        });
    }
}
