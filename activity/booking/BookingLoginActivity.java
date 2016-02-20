package uk.co.odeon.androidapp.activity.booking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import twitter4j.conf.PropertyConfiguration;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.model.BookingProcess;
import uk.co.odeon.androidapp.task.BookingInitTask;
import uk.co.odeon.androidapp.task.TaskTarget;

public class BookingLoginActivity extends AbstractODEONBookingBaseActivity implements TaskTarget<Boolean> {
    protected static final String TAG;
    protected BookingInitTask bookingInitTask;
    protected int cinemaId;
    protected final SharedPreferences customerDataPrefs;
    protected final ODEONApplication odeonApplication;
    private final OnClickListener onLoginClick;
    private final OnClickListener onNoLoginClick;
    protected String password;
    protected EditText passwordText;
    protected String performanceId;
    protected String username;
    protected EditText usernameText;

    public BookingLoginActivity() {
        this.odeonApplication = ODEONApplication.getInstance();
        this.customerDataPrefs = ODEONApplication.getInstance().getCustomerDataPrefs();
        this.bookingInitTask = null;
        this.performanceId = null;
        this.cinemaId = 0;
        this.usernameText = null;
        this.passwordText = null;
        this.username = null;
        this.password = null;
        this.onNoLoginClick = new OnClickListener() {
            public void onClick(View v) {
                ODEONApplication.trackEvent("Showtimes-book now Without Logging In", "Click", "");
                Intent noLoginNameDetails = new Intent(v.getContext(), BookingNoLoginNameDetailsActivity.class);
                noLoginNameDetails.putExtra(Constants.EXTRA_PERFORMANCE_ID, BookingLoginActivity.this.performanceId);
                noLoginNameDetails.putExtra(Constants.EXTRA_CINEMA_ID, BookingLoginActivity.this.cinemaId);
                BookingLoginActivity.this.startActivity(noLoginNameDetails);
            }
        };
        this.onLoginClick = new OnClickListener() {
            public void onClick(View v) {
                ODEONApplication.trackEvent("Showtimes-book now Login", "Click", "");
                BookingLoginActivity.this.performLogin();
            }
        };
    }

    static {
        TAG = BookingLoginActivity.class.getSimpleName();
    }

    public EditText getUsernameTextView() {
        if (this.usernameText == null) {
            this.usernameText = (EditText) findViewById(R.id.usernameText);
        }
        return this.usernameText;
    }

    public EditText getPasswordTextView() {
        if (this.passwordText == null) {
            this.passwordText = (EditText) findViewById(R.id.passwordText);
        }
        return this.passwordText;
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_login);
        getWindow().setSoftInputMode(3);
        if (!(savedInstanceState == null || savedInstanceState.getString(Constants.CUSTOMER_PREFS_USERNAME) == null)) {
            getUsernameTextView().setText(savedInstanceState.getString(Constants.CUSTOMER_PREFS_USERNAME));
        }
        if (!(savedInstanceState == null || savedInstanceState.getString(PropertyConfiguration.PASSWORD) == null)) {
            getPasswordTextView().setText(savedInstanceState.getString(PropertyConfiguration.PASSWORD));
        }
        this.performanceId = getIntent().getStringExtra(Constants.EXTRA_PERFORMANCE_ID);
        this.cinemaId = getIntent().getIntExtra(Constants.EXTRA_CINEMA_ID, 0);
        configureNavigationHeader();
        Button noLoginButton = (Button) findViewById(R.id.noLoginButton);
        if (noLoginButton != null) {
            noLoginButton.setOnClickListener(this.onNoLoginClick);
        }
        Button loginButton = (Button) findViewById(R.id.loginButton);
        if (loginButton != null) {
            loginButton.setOnClickListener(this.onLoginClick);
        }
        this.bookingInitTask = (BookingInitTask) getLastNonConfigurationInstance();
        if (this.bookingInitTask != null) {
            showProgress(R.string.booking_progress, true);
            this.bookingInitTask.attach(this);
        }
    }

    public void onResume() {
        super.onResume();
        if (getIntent().getAction() == null || !getIntent().getAction().equals(Constants.ACTION_END_OF_BOOKING)) {
            this.username = this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_USERNAME, null);
            this.password = this.customerDataPrefs.getString(PropertyConfiguration.PASSWORD, null);
            if (this.username != null) {
                getUsernameTextView().setText(this.username);
            }
            if (this.username != null && this.password != null) {
                getPasswordTextView().setText(this.password);
                performLogin();
                return;
            }
            return;
        }
        finish();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Constants.CUSTOMER_PREFS_USERNAME, getUsernameTextView().getText().toString());
        savedInstanceState.putString(PropertyConfiguration.PASSWORD, getPasswordTextView().getText().toString());
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
            this.odeonApplication.saveCustomerLoginInPrefs(this.username, this.password);
            startActivity(new Intent(this, BookingSectionSelectionActivity.class));
            return;
        }
        showBookingAlert(BookingProcess.getInstance().hasError() ? BookingProcess.getInstance().getLastError(true) : null);
    }

    protected void configureNavigationHeader() {
        configureNavigationHeaderTitle((int) R.string.booking_header_login);
        configureNavigationHeaderCancel(getString(R.string.booking_header_button_cancel), R.drawable.nav_bar_btn_4_round, new OnClickListener() {
            public void onClick(View v) {
                BookingLoginActivity.this.finish();
            }
        });
    }

    protected void performLogin() {
        this.username = getUsernameTextView().getText().toString().trim();
        this.password = getPasswordTextView().getText().toString().trim();
        boolean hasError = false;
        if (this.username.length() <= 0) {
            getUsernameTextView().setError(getString(R.string.error_form_mandantory, new Object[]{getString(R.string.form_field_username)}));
            hasError = true;
        }
        if (this.password.length() <= 0) {
            getPasswordTextView().setError(getString(R.string.error_form_mandantory, new Object[]{getString(R.string.form_field_password)}));
            hasError = true;
        }
        if (!hasError) {
            this.bookingInitTask = new BookingInitTask(this);
            this.bookingInitTask.execute(new String[]{this.performanceId, String.valueOf(this.cinemaId), this.username, this.password});
            showProgress(R.string.booking_progress, true);
        }
    }
}
