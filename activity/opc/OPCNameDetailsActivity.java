package uk.co.odeon.androidapp.activity.opc;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import twitter4j.conf.PropertyConfiguration;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;

public class OPCNameDetailsActivity extends AbstractODEONOPCBaseActivity {
    private static final int DATE_DIALOG_ID = 1;
    private static final SimpleDateFormat SDF_DISPLAY_DATE;
    private static final SimpleDateFormat SDF_UK_DATE;
    protected static final String TAG;
    protected String confirmEmail;
    protected EditText confirmEmailText;
    protected final SharedPreferences customerDataPrefs;
    private OnDateSetListener dateSetListener;
    protected Button dobButton;
    protected int dobDay;
    protected String dobDisplay;
    protected int dobMonth;
    protected String dobUk;
    protected int dobYear;
    protected String email;
    protected EditText emailText;
    protected String firstname;
    protected EditText firstnameText;
    protected String lastname;
    protected final Calendar minAgeCalendar;
    private final OnClickListener onContinueClick;
    protected String password;
    protected EditText passwordText;
    protected EditText surnameText;

    public OPCNameDetailsActivity() {
        this.customerDataPrefs = ODEONApplication.getInstance().getCustomerDataPrefs();
        this.minAgeCalendar = Calendar.getInstance();
        this.firstname = null;
        this.lastname = null;
        this.email = null;
        this.confirmEmail = null;
        this.password = null;
        this.dobUk = null;
        this.dobDisplay = null;
        this.firstnameText = null;
        this.surnameText = null;
        this.emailText = null;
        this.confirmEmailText = null;
        this.passwordText = null;
        this.dobButton = null;
        this.onContinueClick = new OnClickListener() {
            public void onClick(View v) {
                OPCNameDetailsActivity.this.firstname = OPCNameDetailsActivity.this.getFirstnameTextView().getText().toString().trim();
                OPCNameDetailsActivity.this.lastname = OPCNameDetailsActivity.this.getSurnameTextView().getText().toString().trim();
                OPCNameDetailsActivity.this.email = OPCNameDetailsActivity.this.getEmailTextView().getText().toString().trim();
                OPCNameDetailsActivity.this.confirmEmail = OPCNameDetailsActivity.this.getConfirmEmailTextView().getText().toString().trim();
                OPCNameDetailsActivity.this.password = OPCNameDetailsActivity.this.getPasswordTextView().getText().toString().trim();
                Calendar dobCalendar = OPCNameDetailsActivity.this.getDateUkAsCalendar(OPCNameDetailsActivity.this.dobUk);
                boolean hasError = false;
                if (OPCNameDetailsActivity.this.firstname.length() <= 0) {
                    EditText firstnameTextView = OPCNameDetailsActivity.this.getFirstnameTextView();
                    OPCNameDetailsActivity oPCNameDetailsActivity = OPCNameDetailsActivity.this;
                    Object[] objArr = new Object[OPCNameDetailsActivity.DATE_DIALOG_ID];
                    objArr[0] = OPCNameDetailsActivity.this.getString(R.string.form_field_firstname);
                    firstnameTextView.setError(oPCNameDetailsActivity.getString(R.string.error_form_mandantory, objArr));
                    hasError = true;
                }
                if (OPCNameDetailsActivity.this.lastname.length() <= 0) {
                    firstnameTextView = OPCNameDetailsActivity.this.getSurnameTextView();
                    oPCNameDetailsActivity = OPCNameDetailsActivity.this;
                    objArr = new Object[OPCNameDetailsActivity.DATE_DIALOG_ID];
                    objArr[0] = OPCNameDetailsActivity.this.getString(R.string.form_field_surname);
                    firstnameTextView.setError(oPCNameDetailsActivity.getString(R.string.error_form_mandantory, objArr));
                    hasError = true;
                }
                if (OPCNameDetailsActivity.this.email.length() <= 0) {
                    firstnameTextView = OPCNameDetailsActivity.this.getEmailTextView();
                    oPCNameDetailsActivity = OPCNameDetailsActivity.this;
                    objArr = new Object[OPCNameDetailsActivity.DATE_DIALOG_ID];
                    objArr[0] = OPCNameDetailsActivity.this.getString(R.string.form_field_email);
                    firstnameTextView.setError(oPCNameDetailsActivity.getString(R.string.error_form_mandantory, objArr));
                    hasError = true;
                } else if (!ODEONApplication.isValidEmail(OPCNameDetailsActivity.this.email)) {
                    firstnameTextView = OPCNameDetailsActivity.this.getEmailTextView();
                    oPCNameDetailsActivity = OPCNameDetailsActivity.this;
                    objArr = new Object[OPCNameDetailsActivity.DATE_DIALOG_ID];
                    objArr[0] = OPCNameDetailsActivity.this.getString(R.string.form_field_email);
                    firstnameTextView.setError(oPCNameDetailsActivity.getString(R.string.error_form_invalid, objArr));
                    hasError = true;
                }
                if (OPCNameDetailsActivity.this.confirmEmail.length() <= 0) {
                    firstnameTextView = OPCNameDetailsActivity.this.getConfirmEmailTextView();
                    oPCNameDetailsActivity = OPCNameDetailsActivity.this;
                    objArr = new Object[OPCNameDetailsActivity.DATE_DIALOG_ID];
                    objArr[0] = OPCNameDetailsActivity.this.getString(R.string.form_field_confirm_email);
                    firstnameTextView.setError(oPCNameDetailsActivity.getString(R.string.error_form_mandantory, objArr));
                    hasError = true;
                } else if (!OPCNameDetailsActivity.this.confirmEmail.equals(OPCNameDetailsActivity.this.email)) {
                    firstnameTextView = OPCNameDetailsActivity.this.getConfirmEmailTextView();
                    oPCNameDetailsActivity = OPCNameDetailsActivity.this;
                    Object[] objArr2 = new Object[OPCNameDetailsActivity.DATE_DIALOG_ID];
                    objArr2[0] = OPCNameDetailsActivity.this.getString(R.string.form_field_confirm_email);
                    firstnameTextView.setError(oPCNameDetailsActivity.getString(R.string.error_form_match, objArr2));
                    hasError = true;
                }
                if (OPCNameDetailsActivity.this.password.length() <= 0) {
                    firstnameTextView = OPCNameDetailsActivity.this.getPasswordTextView();
                    oPCNameDetailsActivity = OPCNameDetailsActivity.this;
                    objArr = new Object[OPCNameDetailsActivity.DATE_DIALOG_ID];
                    objArr[0] = OPCNameDetailsActivity.this.getString(R.string.form_field_password);
                    firstnameTextView.setError(oPCNameDetailsActivity.getString(R.string.error_form_mandantory, objArr));
                    hasError = true;
                } else if (!ODEONApplication.isValidPassword(OPCNameDetailsActivity.this.password)) {
                    OPCNameDetailsActivity.this.getPasswordTextView().setError(OPCNameDetailsActivity.this.getString(R.string.error_form_invalid_length, new Object[]{OPCNameDetailsActivity.this.getString(R.string.form_field_password), Integer.valueOf(6), Integer.valueOf(20)}));
                    hasError = true;
                }
                if (OPCNameDetailsActivity.this.dobUk == null) {
                    Button dobButton = OPCNameDetailsActivity.this.getDobButton();
                    oPCNameDetailsActivity = OPCNameDetailsActivity.this;
                    objArr = new Object[OPCNameDetailsActivity.DATE_DIALOG_ID];
                    objArr[0] = OPCNameDetailsActivity.this.getString(R.string.form_field_dob);
                    dobButton.setError(oPCNameDetailsActivity.getString(R.string.error_form_mandantory, objArr));
                    hasError = true;
                }
                if (dobCalendar == null || dobCalendar.after(OPCNameDetailsActivity.this.minAgeCalendar)) {
                    dobButton = OPCNameDetailsActivity.this.getDobButton();
                    oPCNameDetailsActivity = OPCNameDetailsActivity.this;
                    objArr = new Object[OPCNameDetailsActivity.DATE_DIALOG_ID];
                    objArr[0] = OPCNameDetailsActivity.this.getString(R.string.form_field_dob);
                    dobButton.setError(oPCNameDetailsActivity.getString(R.string.error_form_invalid, objArr));
                    hasError = true;
                }
                if (!hasError) {
                    OPCNameDetailsActivity.this.customerDataPrefs.edit().putString(Constants.CUSTOMER_PREFS_FIRSTNAME, OPCNameDetailsActivity.this.firstname).putString(Constants.CUSTOMER_PREFS_LASTNAME, OPCNameDetailsActivity.this.lastname).putString(Constants.CUSTOMER_PREFS_EMAIL, OPCNameDetailsActivity.this.email).putString(Constants.CUSTOMER_PREFS_EMAIL_CONFIRM, OPCNameDetailsActivity.this.confirmEmail).putString(PropertyConfiguration.PASSWORD, OPCNameDetailsActivity.this.password).putString(Constants.CUSTOMER_PREFS_DOB, OPCNameDetailsActivity.this.dobUk).commit();
                    OPCNameDetailsActivity.this.startActivity(new Intent(v.getContext(), OPCAddressDetailsActivity.class));
                }
            }
        };
        this.dateSetListener = new OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(OPCNameDetailsActivity.DATE_DIALOG_ID, year);
                calendar.set(2, monthOfYear);
                calendar.set(5, dayOfMonth);
                OPCNameDetailsActivity.this.handleDate(calendar);
            }
        };
    }

    static {
        TAG = OPCNameDetailsActivity.class.getSimpleName();
        SDF_UK_DATE = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        SDF_DISPLAY_DATE = new SimpleDateFormat("dd. MMM yyyy", Locale.UK);
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

    public EditText getPasswordTextView() {
        if (this.passwordText == null) {
            this.passwordText = (EditText) findViewById(R.id.passwordText);
        }
        return this.passwordText;
    }

    public Button getDobButton() {
        if (this.dobButton == null) {
            this.dobButton = (Button) findViewById(R.id.dobButton);
        }
        return this.dobButton;
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opc_name_details);
        getWindow().setSoftInputMode(3);
        this.minAgeCalendar.add(DATE_DIALOG_ID, -18);
        if (!(savedInstanceState == null || savedInstanceState.getString(Constants.CUSTOMER_PREFS_FIRSTNAME) == null)) {
            getFirstnameTextView().setText(savedInstanceState.getString(Constants.CUSTOMER_PREFS_FIRSTNAME));
        }
        if (!(savedInstanceState == null || savedInstanceState.getString(Constants.CUSTOMER_PREFS_LASTNAME) == null)) {
            getSurnameTextView().setText(savedInstanceState.getString(Constants.CUSTOMER_PREFS_LASTNAME));
        }
        if (!(savedInstanceState == null || savedInstanceState.getString(Constants.CUSTOMER_PREFS_EMAIL) == null)) {
            getEmailTextView().setText(savedInstanceState.getString(Constants.CUSTOMER_PREFS_EMAIL));
        }
        if (!(savedInstanceState == null || savedInstanceState.getString(Constants.CUSTOMER_PREFS_EMAIL_CONFIRM) == null)) {
            getConfirmEmailTextView().setText(savedInstanceState.getString(Constants.CUSTOMER_PREFS_EMAIL_CONFIRM));
        }
        if (!(savedInstanceState == null || savedInstanceState.getString(PropertyConfiguration.PASSWORD) == null)) {
            getPasswordTextView().setText(savedInstanceState.getString(PropertyConfiguration.PASSWORD));
        }
        if (savedInstanceState == null || savedInstanceState.getString(Constants.CUSTOMER_PREFS_DOB) == null) {
            this.dobYear = this.minAgeCalendar.get(DATE_DIALOG_ID);
            this.dobMonth = this.minAgeCalendar.get(2);
            this.dobDay = this.minAgeCalendar.get(5);
        } else {
            handleDateUk(savedInstanceState.getString(Constants.CUSTOMER_PREFS_DOB));
        }
        configureNavigationHeader();
        Button continueButton = (Button) findViewById(R.id.continueButton);
        if (continueButton != null) {
            continueButton.setOnClickListener(this.onContinueClick);
        }
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    OPCNameDetailsActivity.this.performRestartOfJoin();
                }
            });
        }
        Button passwordHelpfulHint = (Button) findViewById(R.id.passwordHelpfulHint);
        if (passwordHelpfulHint != null) {
            passwordHelpfulHint.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    OPCNameDetailsActivity.this.showHelpfulHint(OPCNameDetailsActivity.this.getString(R.string.opc_helpful_hint_password));
                }
            });
        }
        getDobButton().setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                OPCNameDetailsActivity.this.getDobButton().setError(null);
                OPCNameDetailsActivity.this.showDialog(OPCNameDetailsActivity.DATE_DIALOG_ID);
            }
        });
    }

    public void onResume() {
        super.onResume();
        this.firstname = this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_FIRSTNAME, null);
        this.lastname = this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_LASTNAME, null);
        this.email = this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_EMAIL, null);
        this.confirmEmail = this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_EMAIL_CONFIRM, null);
        this.password = this.customerDataPrefs.getString(PropertyConfiguration.PASSWORD, null);
        String savedDobUk = this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_DOB, null);
        if (this.firstname != null) {
            getFirstnameTextView().setText(this.firstname);
        }
        if (this.lastname != null) {
            getSurnameTextView().setText(this.lastname);
        }
        if (this.email != null) {
            getEmailTextView().setText(this.email);
        }
        if (this.confirmEmail != null) {
            getConfirmEmailTextView().setText(this.email);
        }
        if (this.password != null) {
            getPasswordTextView().setText(this.password);
        }
        if (savedDobUk != null) {
            handleDateUk(savedDobUk);
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Constants.CUSTOMER_PREFS_FIRSTNAME, getFirstnameTextView().getText().toString());
        savedInstanceState.putString(Constants.CUSTOMER_PREFS_LASTNAME, getSurnameTextView().getText().toString());
        savedInstanceState.putString(Constants.CUSTOMER_PREFS_EMAIL, getEmailTextView().getText().toString());
        savedInstanceState.putString(Constants.CUSTOMER_PREFS_EMAIL_CONFIRM, getConfirmEmailTextView().getText().toString());
        savedInstanceState.putString(PropertyConfiguration.PASSWORD, getPasswordTextView().getText().toString());
        if (this.dobUk != null) {
            savedInstanceState.putString(Constants.CUSTOMER_PREFS_DOB, this.dobUk);
        }
    }

    protected void configureNavigationHeader() {
        configureNavigationHeaderTitle((int) R.string.opc_header_title);
        configureNavigationHeaderCancel(new OnClickListener() {
            public void onClick(View v) {
                OPCNameDetailsActivity.this.finish();
            }
        });
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID /*1*/:
                return new DatePickerDialog(this, this.dateSetListener, this.dobYear, this.dobMonth, this.dobDay);
            default:
                return null;
        }
    }

    private Calendar getDateUkAsCalendar(String date) {
        if (date == null || date.length() <= 0) {
            return null;
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(SDF_UK_DATE.parse(date));
            return calendar;
        } catch (ParseException pe) {
            Log.w(TAG, "Couldn't parse saved dob: " + pe.toString());
            return null;
        }
    }

    private void handleDateUk(String date) {
        handleDate(getDateUkAsCalendar(date));
    }

    private void handleDate(Calendar calendar) {
        if (calendar != null) {
            this.dobYear = calendar.get(DATE_DIALOG_ID);
            this.dobMonth = calendar.get(2);
            this.dobDay = calendar.get(5);
            this.dobUk = SDF_UK_DATE.format(calendar.getTime());
            this.dobDisplay = SDF_DISPLAY_DATE.format(calendar.getTime());
            getDobButton().setText(this.dobDisplay);
        }
    }
}
