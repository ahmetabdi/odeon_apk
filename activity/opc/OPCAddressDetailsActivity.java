package uk.co.odeon.androidapp.activity.opc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.Constants.APP_LOCATION;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.custom.SpinnerWithHint;
import uk.co.odeon.androidapp.custom.SpinnerWithHint.OnPerformClickListener;
import uk.co.odeon.androidapp.provider.SiteContent.SiteColumns;

public class OPCAddressDetailsActivity extends AbstractODEONOPCBaseActivity {
    protected static final String TAG;
    protected SimpleCursorAdapter cinemaAdapter;
    protected Cursor cinemaCursor;
    protected TextView cinemaError;
    protected ArrayAdapter<CharSequence> cinemaHintAdapter;
    protected int cinemaId;
    protected SpinnerWithHint cinemaSpinner;
    protected String city;
    protected EditText cityText;
    protected final SharedPreferences customerDataPrefs;
    protected String house;
    protected EditText houseText;
    private final OnClickListener onContinueClick;
    private final OnPerformClickListener onPerformClickListener;
    protected String phone;
    protected EditText phoneText;
    protected String postcode;
    protected EditText postcodeText;
    protected String street;
    protected EditText streetText;

    public OPCAddressDetailsActivity() {
        this.customerDataPrefs = ODEONApplication.getInstance().getCustomerDataPrefs();
        this.cinemaHintAdapter = null;
        this.cinemaAdapter = null;
        this.cinemaCursor = null;
        this.house = null;
        this.street = null;
        this.city = null;
        this.postcode = null;
        this.phone = null;
        this.cinemaId = -1;
        this.houseText = null;
        this.streetText = null;
        this.cityText = null;
        this.postcodeText = null;
        this.phoneText = null;
        this.cinemaSpinner = null;
        this.cinemaError = null;
        this.onContinueClick = new OnClickListener() {
            public void onClick(View v) {
                OPCAddressDetailsActivity.this.house = OPCAddressDetailsActivity.this.getHouseTextView().getText().toString().trim();
                OPCAddressDetailsActivity.this.street = OPCAddressDetailsActivity.this.getStreetTextView().getText().toString().trim();
                OPCAddressDetailsActivity.this.city = OPCAddressDetailsActivity.this.getCityTextView().getText().toString().trim();
                OPCAddressDetailsActivity.this.postcode = OPCAddressDetailsActivity.this.getPostcodeTextView().getText().toString().trim();
                OPCAddressDetailsActivity.this.phone = OPCAddressDetailsActivity.this.getPhoneTextView().getText().toString().trim();
                if (OPCAddressDetailsActivity.this.isCinemaSelected()) {
                    OPCAddressDetailsActivity.this.cinemaCursor = (Cursor) OPCAddressDetailsActivity.this.getCinemaSpinnerView().getSelectedItem();
                    OPCAddressDetailsActivity.this.cinemaId = OPCAddressDetailsActivity.this.cinemaCursor.getInt(OPCAddressDetailsActivity.this.cinemaCursor.getColumnIndex("_id"));
                } else {
                    OPCAddressDetailsActivity.this.cinemaId = -1;
                }
                boolean hasError = false;
                if (OPCAddressDetailsActivity.this.house.length() <= 0) {
                    OPCAddressDetailsActivity.this.getHouseTextView().setError(OPCAddressDetailsActivity.this.getString(R.string.error_form_mandantory, new Object[]{OPCAddressDetailsActivity.this.getString(R.string.form_field_house)}));
                    hasError = true;
                }
                if (OPCAddressDetailsActivity.this.street.length() <= 0) {
                    OPCAddressDetailsActivity.this.getStreetTextView().setError(OPCAddressDetailsActivity.this.getString(R.string.error_form_mandantory, new Object[]{OPCAddressDetailsActivity.this.getString(R.string.form_field_street)}));
                    hasError = true;
                }
                if (OPCAddressDetailsActivity.this.city.length() <= 0) {
                    OPCAddressDetailsActivity.this.getCityTextView().setError(OPCAddressDetailsActivity.this.getString(R.string.error_form_mandantory, new Object[]{OPCAddressDetailsActivity.this.getString(R.string.form_field_city)}));
                    hasError = true;
                }
                if (OPCAddressDetailsActivity.this.postcode.length() <= 0 && OPCAddressDetailsActivity.this.getPostcodeTextView().isShown()) {
                    OPCAddressDetailsActivity.this.getPostcodeTextView().setError(OPCAddressDetailsActivity.this.getString(R.string.error_form_mandantory, new Object[]{OPCAddressDetailsActivity.this.getString(R.string.form_field_postcode)}));
                    hasError = true;
                }
                if (OPCAddressDetailsActivity.this.phone.length() <= 0) {
                    OPCAddressDetailsActivity.this.getPhoneTextView().setError(OPCAddressDetailsActivity.this.getString(R.string.error_form_mandantory, new Object[]{OPCAddressDetailsActivity.this.getString(R.string.form_field_phone)}));
                    hasError = true;
                }
                if (OPCAddressDetailsActivity.this.cinemaId <= 0) {
                    if (OPCAddressDetailsActivity.this.getCinemaErrorTextView() != null) {
                        OPCAddressDetailsActivity.this.getCinemaErrorTextView().setError(OPCAddressDetailsActivity.this.getString(R.string.error_form_mandantory, new Object[]{OPCAddressDetailsActivity.this.getString(R.string.form_field_cinema)}));
                    }
                    hasError = true;
                }
                if (!hasError) {
                    OPCAddressDetailsActivity.this.customerDataPrefs.edit().putString(Constants.CUSTOMER_PREFS_HOUSE, OPCAddressDetailsActivity.this.house).putString(Constants.CUSTOMER_PREFS_STREET, OPCAddressDetailsActivity.this.street).putString(Constants.CUSTOMER_PREFS_CITY, OPCAddressDetailsActivity.this.city).putString(Constants.CUSTOMER_PREFS_POSTCODE, OPCAddressDetailsActivity.this.postcode).putString(SiteColumns.PHONE, OPCAddressDetailsActivity.this.phone).putInt(Constants.CUSTOMER_PREFS_CINEMA_ID, OPCAddressDetailsActivity.this.cinemaId).commit();
                    OPCAddressDetailsActivity.this.startActivity(new Intent(v.getContext(), OPCDetailsActivity.class));
                }
            }
        };
        this.onPerformClickListener = new OnPerformClickListener() {
            public void onFirstPerformClick() {
                OPCAddressDetailsActivity.this.setCinemaAdapter(false);
                OPCAddressDetailsActivity.this.getCinemaSpinnerView().performClick();
            }

            public void onPerformClick() {
                if (OPCAddressDetailsActivity.this.getCinemaErrorTextView() != null) {
                    OPCAddressDetailsActivity.this.getCinemaErrorTextView().setError(null);
                }
            }
        };
    }

    static {
        TAG = OPCAddressDetailsActivity.class.getSimpleName();
    }

    public EditText getHouseTextView() {
        if (this.houseText == null) {
            this.houseText = (EditText) findViewById(R.id.houseText);
        }
        return this.houseText;
    }

    public EditText getStreetTextView() {
        if (this.streetText == null) {
            this.streetText = (EditText) findViewById(R.id.streetText);
        }
        return this.streetText;
    }

    public EditText getCityTextView() {
        if (this.cityText == null) {
            this.cityText = (EditText) findViewById(R.id.cityText);
        }
        return this.cityText;
    }

    public EditText getPostcodeTextView() {
        if (this.postcodeText == null) {
            this.postcodeText = (EditText) findViewById(R.id.postcodeText);
        }
        return this.postcodeText;
    }

    public EditText getPhoneTextView() {
        if (this.phoneText == null) {
            this.phoneText = (EditText) findViewById(R.id.phoneText);
        }
        return this.phoneText;
    }

    public SpinnerWithHint getCinemaSpinnerView() {
        if (this.cinemaSpinner == null) {
            this.cinemaSpinner = (SpinnerWithHint) findViewById(R.id.cinemaSpinner);
        }
        return this.cinemaSpinner;
    }

    public TextView getCinemaErrorTextView() {
        if (this.cinemaError == null) {
            this.cinemaError = (TextView) findViewById(R.id.cinemaError);
        }
        return this.cinemaError;
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opc_address_details);
        getWindow().setSoftInputMode(3);
        if (savedInstanceState == null || savedInstanceState.getInt(Constants.CUSTOMER_PREFS_CINEMA_ID) <= 0) {
            setCinemaAdapter(true);
        } else {
            setCinemaAdapter(false);
        }
        if (!(savedInstanceState == null || savedInstanceState.getString(Constants.CUSTOMER_PREFS_HOUSE) == null)) {
            getHouseTextView().setText(savedInstanceState.getString(Constants.CUSTOMER_PREFS_HOUSE));
        }
        if (!(savedInstanceState == null || savedInstanceState.getString(Constants.CUSTOMER_PREFS_STREET) == null)) {
            getStreetTextView().setText(savedInstanceState.getString(Constants.CUSTOMER_PREFS_STREET));
        }
        if (!(savedInstanceState == null || savedInstanceState.getString(Constants.CUSTOMER_PREFS_CITY) == null)) {
            getCityTextView().setText(savedInstanceState.getString(Constants.CUSTOMER_PREFS_CITY));
        }
        if (!(savedInstanceState == null || savedInstanceState.getString(Constants.CUSTOMER_PREFS_POSTCODE) == null)) {
            getPostcodeTextView().setText(savedInstanceState.getString(Constants.CUSTOMER_PREFS_POSTCODE));
        }
        if (!(savedInstanceState == null || savedInstanceState.getString(SiteColumns.PHONE) == null)) {
            getPhoneTextView().setText(savedInstanceState.getString(SiteColumns.PHONE));
        }
        if (savedInstanceState != null && savedInstanceState.getInt(Constants.CUSTOMER_PREFS_CINEMA_ID) > 0) {
            initializeSavedCinemaInSpinner(savedInstanceState.getInt(Constants.CUSTOMER_PREFS_CINEMA_ID));
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
                    OPCAddressDetailsActivity.this.performRestartOfJoin();
                }
            });
        }
        Button phoneHelpfulHint = (Button) findViewById(R.id.phoneHelpfulHint);
        if (phoneHelpfulHint != null) {
            phoneHelpfulHint.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    OPCAddressDetailsActivity.this.showHelpfulHint(OPCAddressDetailsActivity.this.getString(R.string.opc_helpful_hint_phone));
                }
            });
        }
        Button cinemaHelpfulHint = (Button) findViewById(R.id.cinemaHelpfulHint);
        if (cinemaHelpfulHint != null) {
            cinemaHelpfulHint.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    OPCAddressDetailsActivity.this.showHelpfulHint(OPCAddressDetailsActivity.this.getString(R.string.opc_helpful_hint_cinema));
                }
            });
        }
        if (ODEONApplication.getInstance().getChoosenLocation().equals(APP_LOCATION.ire)) {
            getPostcodeTextView().setVisibility(8);
        } else {
            getPostcodeTextView().setVisibility(0);
        }
    }

    public void onResume() {
        super.onResume();
        this.house = this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_HOUSE, null);
        this.street = this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_STREET, null);
        this.city = this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_CITY, null);
        this.postcode = this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_POSTCODE, null);
        this.phone = this.customerDataPrefs.getString(SiteColumns.PHONE, null);
        this.cinemaId = this.customerDataPrefs.getInt(Constants.CUSTOMER_PREFS_CINEMA_ID, -1);
        if (this.house != null) {
            getHouseTextView().setText(this.house);
        }
        if (this.street != null) {
            getStreetTextView().setText(this.street);
        }
        if (this.city != null) {
            getCityTextView().setText(this.city);
        }
        if (this.postcode != null) {
            getPostcodeTextView().setText(this.postcode);
        }
        if (this.phone != null) {
            getPhoneTextView().setText(this.phone);
        }
        if (this.cinemaId > 0) {
            if (!isCinemaSelected()) {
                setCinemaAdapter(false);
            }
            initializeSavedCinemaInSpinner(this.cinemaId);
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Constants.CUSTOMER_PREFS_HOUSE, getHouseTextView().getText().toString());
        savedInstanceState.putString(Constants.CUSTOMER_PREFS_STREET, getStreetTextView().getText().toString());
        savedInstanceState.putString(Constants.CUSTOMER_PREFS_CITY, getCityTextView().getText().toString());
        savedInstanceState.putString(Constants.CUSTOMER_PREFS_POSTCODE, getPostcodeTextView().getText().toString());
        savedInstanceState.putString(SiteColumns.PHONE, getPhoneTextView().getText().toString());
        if (isCinemaSelected()) {
            this.cinemaCursor = (Cursor) getCinemaSpinnerView().getSelectedItem();
            savedInstanceState.putInt(Constants.CUSTOMER_PREFS_CINEMA_ID, this.cinemaCursor.getInt(this.cinemaCursor.getColumnIndex("_id")));
        }
    }

    protected void configureNavigationHeader() {
        configureNavigationHeaderTitle((int) R.string.opc_header_title);
        configureNavigationHeaderCancel(new OnClickListener() {
            public void onClick(View v) {
                OPCAddressDetailsActivity.this.finish();
            }
        });
    }

    private void setCinemaAdapter(boolean isHint) {
        if (isHint && this.cinemaHintAdapter == null) {
            this.cinemaHintAdapter = new ArrayAdapter(getCinemaSpinnerView().getContext(), 17367048, new String[]{getString(R.string.form_field_cinema_prompt)});
            this.cinemaHintAdapter.setDropDownViewResource(17367049);
            getCinemaSpinnerView().onPerformClickListener = this.onPerformClickListener;
        } else if (!isHint && this.cinemaAdapter == null) {
            this.cinemaCursor = managedQuery(SiteColumns.CONTENT_URI, null, null, null, SiteColumns.DEFAULT_SORT_ORDER);
            this.cinemaAdapter = new SimpleCursorAdapter(this, 17367048, this.cinemaCursor, new String[]{SiteColumns.NAME}, new int[]{16908308});
            this.cinemaAdapter.setDropDownViewResource(17367049);
            getCinemaSpinnerView().onPerformClickListener = null;
        }
        getCinemaSpinnerView().setAdapter(isHint ? this.cinemaHintAdapter : this.cinemaAdapter);
    }

    private boolean isCinemaSelected() {
        return getCinemaSpinnerView().getAdapter().equals(this.cinemaAdapter);
    }

    private void initializeSavedCinemaInSpinner(int cinemaId) {
        for (int i = 0; i < getCinemaSpinnerView().getCount(); i++) {
            Cursor cursor = (Cursor) getCinemaSpinnerView().getItemAtPosition(i);
            if (cinemaId == cursor.getInt(cursor.getColumnIndex("_id"))) {
                getCinemaSpinnerView().setSelection(i);
                return;
            }
        }
    }
}
