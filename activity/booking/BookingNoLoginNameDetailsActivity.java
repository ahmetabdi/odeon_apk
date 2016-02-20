package uk.co.odeon.androidapp.activity.booking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.custom.SpinnerWithHint;
import uk.co.odeon.androidapp.custom.SpinnerWithHint.OnPerformClickListener;
import uk.co.odeon.androidapp.model.BookingProcess;
import uk.co.odeon.androidapp.provider.OfferContent.OfferColumns;

public class BookingNoLoginNameDetailsActivity extends AbstractODEONBookingBaseActivity {
    protected static final String TAG;
    protected final BookingProcess bookingProcess;
    protected int cinemaId;
    protected final SharedPreferences customerDataPrefs;
    protected EditText firstnameText;
    private final OnClickListener onContinueClick;
    private final OnPerformClickListener onPerformClickListener;
    protected String performanceId;
    protected EditText surnameText;
    protected ArrayAdapter<CharSequence> titleAdapter;
    protected TextView titleError;
    protected ArrayAdapter<CharSequence> titleHintAdapter;
    protected SpinnerWithHint titleSpinner;

    public BookingNoLoginNameDetailsActivity() {
        this.customerDataPrefs = ODEONApplication.getInstance().getCustomerDataPrefs();
        this.bookingProcess = BookingProcess.getInstance();
        this.titleHintAdapter = null;
        this.titleAdapter = null;
        this.performanceId = null;
        this.cinemaId = 0;
        this.firstnameText = null;
        this.surnameText = null;
        this.titleSpinner = null;
        this.titleError = null;
        this.onContinueClick = new OnClickListener() {
            public void onClick(View v) {
                String firstname = BookingNoLoginNameDetailsActivity.this.getFirstnameTextView().getText().toString().trim();
                String surname = BookingNoLoginNameDetailsActivity.this.getSurnameTextView().getText().toString().trim();
                String title = BookingNoLoginNameDetailsActivity.this.isTitleSelected() ? BookingNoLoginNameDetailsActivity.this.getTitleSpinnerView().getSelectedItem().toString() : "";
                boolean hasError = false;
                if (firstname.length() <= 0) {
                    BookingNoLoginNameDetailsActivity.this.getFirstnameTextView().setError(BookingNoLoginNameDetailsActivity.this.getString(R.string.error_form_mandantory, new Object[]{BookingNoLoginNameDetailsActivity.this.getString(R.string.form_field_firstname)}));
                    hasError = true;
                }
                if (surname.length() <= 0) {
                    BookingNoLoginNameDetailsActivity.this.getSurnameTextView().setError(BookingNoLoginNameDetailsActivity.this.getString(R.string.error_form_mandantory, new Object[]{BookingNoLoginNameDetailsActivity.this.getString(R.string.form_field_surname)}));
                    hasError = true;
                }
                if (title.length() <= 0) {
                    if (BookingNoLoginNameDetailsActivity.this.getTitleErrorTextView() != null) {
                        BookingNoLoginNameDetailsActivity.this.getTitleErrorTextView().setError(BookingNoLoginNameDetailsActivity.this.getString(R.string.error_form_mandantory, new Object[]{BookingNoLoginNameDetailsActivity.this.getString(R.string.form_field_title)}));
                    }
                    hasError = true;
                }
                if (!hasError) {
                    ODEONApplication.trackEvent("Showtimes-book now Without Logging In Continue", "Click", "");
                    BookingNoLoginNameDetailsActivity.this.bookingProcess.firstname = firstname;
                    BookingNoLoginNameDetailsActivity.this.bookingProcess.lastname = surname;
                    BookingNoLoginNameDetailsActivity.this.bookingProcess.title = title;
                    BookingNoLoginNameDetailsActivity.this.customerDataPrefs.edit().putString(Constants.CUSTOMER_PREFS_FIRSTNAME, BookingNoLoginNameDetailsActivity.this.bookingProcess.firstname).putString(Constants.CUSTOMER_PREFS_LASTNAME, BookingNoLoginNameDetailsActivity.this.bookingProcess.lastname).putString(OfferColumns.TITLE, BookingNoLoginNameDetailsActivity.this.bookingProcess.title).commit();
                    Intent noLoginMailDetails = new Intent(v.getContext(), BookingNoLoginMailDetailsActivity.class);
                    noLoginMailDetails.putExtra(Constants.EXTRA_PERFORMANCE_ID, BookingNoLoginNameDetailsActivity.this.performanceId);
                    noLoginMailDetails.putExtra(Constants.EXTRA_CINEMA_ID, BookingNoLoginNameDetailsActivity.this.cinemaId);
                    BookingNoLoginNameDetailsActivity.this.startActivity(noLoginMailDetails);
                }
            }
        };
        this.onPerformClickListener = new OnPerformClickListener() {
            public void onFirstPerformClick() {
                BookingNoLoginNameDetailsActivity.this.setTitleAdapter(false);
                BookingNoLoginNameDetailsActivity.this.getTitleSpinnerView().performClick();
            }

            public void onPerformClick() {
                if (BookingNoLoginNameDetailsActivity.this.getTitleErrorTextView() != null) {
                    BookingNoLoginNameDetailsActivity.this.getTitleErrorTextView().setError(null);
                }
            }
        };
    }

    static {
        TAG = BookingNoLoginNameDetailsActivity.class.getSimpleName();
    }

    public EditText getFirstnameTextView() {
        if (this.firstnameText == null) {
            this.firstnameText = (EditText) findViewById(R.id.firstnameText);
        }
        return this.firstnameText;
    }

    public EditText getSurnameTextView() {
        if (this.surnameText == null) {
            this.surnameText = (EditText) findViewById(R.id.surnameText);
        }
        return this.surnameText;
    }

    public SpinnerWithHint getTitleSpinnerView() {
        if (this.titleSpinner == null) {
            this.titleSpinner = (SpinnerWithHint) findViewById(R.id.titleSpinner);
        }
        return this.titleSpinner;
    }

    public TextView getTitleErrorTextView() {
        if (this.titleError == null) {
            this.titleError = (TextView) findViewById(R.id.titleError);
        }
        return this.titleError;
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_nologin_name_details);
        getWindow().setSoftInputMode(3);
        if (savedInstanceState == null || savedInstanceState.getString(OfferColumns.TITLE) == null) {
            setTitleAdapter(true);
        } else {
            setTitleAdapter(false);
        }
        if (!(savedInstanceState == null || savedInstanceState.getString(OfferColumns.TITLE) == null)) {
            getTitleSpinnerView().setSelection(this.titleAdapter.getPosition(savedInstanceState.getString(OfferColumns.TITLE)));
        }
        if (!(savedInstanceState == null || savedInstanceState.getString(Constants.CUSTOMER_PREFS_FIRSTNAME) == null)) {
            getFirstnameTextView().setText(savedInstanceState.getString(Constants.CUSTOMER_PREFS_FIRSTNAME));
        }
        if (!(savedInstanceState == null || savedInstanceState.getString(Constants.CUSTOMER_PREFS_LASTNAME) == null)) {
            getSurnameTextView().setText(savedInstanceState.getString(Constants.CUSTOMER_PREFS_LASTNAME));
        }
        this.performanceId = getIntent().getStringExtra(Constants.EXTRA_PERFORMANCE_ID);
        this.cinemaId = getIntent().getIntExtra(Constants.EXTRA_CINEMA_ID, 0);
        configureNavigationHeader();
        Button continueButton = (Button) findViewById(R.id.continueButton);
        if (continueButton != null) {
            continueButton.setOnClickListener(this.onContinueClick);
        }
    }

    public void onResume() {
        super.onResume();
        this.bookingProcess.firstname = this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_FIRSTNAME, null);
        this.bookingProcess.lastname = this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_LASTNAME, null);
        if (this.bookingProcess.firstname != null) {
            getFirstnameTextView().setText(this.bookingProcess.firstname);
        }
        if (this.bookingProcess.lastname != null) {
            getSurnameTextView().setText(this.bookingProcess.lastname);
        }
        this.bookingProcess.title = this.customerDataPrefs.getString(OfferColumns.TITLE, null);
        if (this.bookingProcess.title != null && this.bookingProcess.title.trim().length() > 0) {
            if (!isTitleSelected()) {
                setTitleAdapter(false);
            }
            int titlePosition = ((ArrayAdapter) getTitleSpinnerView().getAdapter()).getPosition(this.bookingProcess.title);
            if (titlePosition >= 0) {
                getTitleSpinnerView().setSelection(titlePosition);
            }
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (isTitleSelected()) {
            savedInstanceState.putString(OfferColumns.TITLE, getTitleSpinnerView().getSelectedItem().toString());
        }
        savedInstanceState.putString(Constants.CUSTOMER_PREFS_FIRSTNAME, getFirstnameTextView().getText().toString());
        savedInstanceState.putString(Constants.CUSTOMER_PREFS_LASTNAME, getSurnameTextView().getText().toString());
    }

    protected void configureNavigationHeader() {
        configureNavigationHeaderTitle((int) R.string.booking_header_details);
        configureNavigationHeaderCancel(new OnClickListener() {
            public void onClick(View v) {
                BookingNoLoginNameDetailsActivity.this.finish();
            }
        });
    }

    private void setTitleAdapter(boolean isHint) {
        if (isHint && this.titleHintAdapter == null) {
            this.titleHintAdapter = new ArrayAdapter(getTitleSpinnerView().getContext(), 17367048, new String[]{getString(R.string.form_field_title_prompt)});
            this.titleHintAdapter.setDropDownViewResource(17367049);
            getTitleSpinnerView().onPerformClickListener = this.onPerformClickListener;
        } else if (!isHint && this.titleAdapter == null) {
            this.titleAdapter = ArrayAdapter.createFromResource(getTitleSpinnerView().getContext(), R.array.user_titles, 17367048);
            this.titleAdapter.setDropDownViewResource(17367049);
            getTitleSpinnerView().onPerformClickListener = null;
        }
        getTitleSpinnerView().setAdapter(isHint ? this.titleHintAdapter : this.titleAdapter);
    }

    private boolean isTitleSelected() {
        return getTitleSpinnerView().getAdapter().equals(this.titleAdapter);
    }
}
