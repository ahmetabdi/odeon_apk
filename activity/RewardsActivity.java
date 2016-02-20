package uk.co.odeon.androidapp.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import twitter4j.conf.PropertyConfiguration;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.activity.opc.OPCChoosePackageActivity;
import uk.co.odeon.androidapp.custom.NavigatorBarSubActivity;
import uk.co.odeon.androidapp.json.Rewards;
import uk.co.odeon.androidapp.task.RewardsTask;
import uk.co.odeon.androidapp.task.TaskTarget;

public class RewardsActivity extends NavigatorBarSubActivity implements TaskTarget<Rewards> {
    protected final SharedPreferences customerDataPrefs;
    private Rewards data;
    protected boolean hasValidLogin;
    protected final ODEONApplication odeonApplication;
    protected String password;
    protected EditText passwordText;
    protected String username;
    protected EditText usernameText;

    /* renamed from: uk.co.odeon.androidapp.activity.RewardsActivity.8 */
    class AnonymousClass8 implements OnClickListener {
        private final /* synthetic */ AlertDialog val$alertDialog;

        AnonymousClass8(AlertDialog alertDialog) {
            this.val$alertDialog = alertDialog;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$alertDialog.hide();
        }
    }

    public RewardsActivity() {
        this.odeonApplication = ODEONApplication.getInstance();
        this.customerDataPrefs = ODEONApplication.getInstance().getCustomerDataPrefs();
        this.usernameText = null;
        this.passwordText = null;
        this.username = null;
        this.password = null;
        this.hasValidLogin = false;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.odeonApplication.hasCustomerLoginInPrefs()) {
            this.username = this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_USERNAME, null);
            this.password = this.customerDataPrefs.getString(PropertyConfiguration.PASSWORD, null);
            new RewardsTask(this).execute(new String[]{this.username, this.password, null});
            showProgress(R.string.booking_progress, true);
        }
    }

    protected void onResume() {
        super.onResume();
        if (!this.odeonApplication.hasCustomerLoginInPrefs()) {
            setLoginView();
        } else if (this.hasValidLogin) {
            setLoggedInView();
        }
    }

    public void onRecycled() {
        super.onRecycled();
        if (!this.odeonApplication.hasCustomerLoginInPrefs()) {
            setLoginView();
        } else if (this.hasValidLogin) {
            setLoggedInView();
        }
    }

    private void hideKeyboard(EditText loginField) {
        getWindow().setSoftInputMode(3);
        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(loginField.getWindowToken(), 0);
    }

    private void tryLogin() {
        hideKeyboard(this.passwordText);
        this.username = this.usernameText.getText().toString().trim();
        this.password = this.passwordText.getText().toString().trim();
        boolean hasError = false;
        if (this.username.length() <= 0) {
            this.usernameText.setError(getString(R.string.error_form_mandantory, new Object[]{"Username"}));
            hasError = true;
        }
        if (this.password.length() <= 0) {
            this.passwordText.setError(getString(R.string.error_form_mandantory, new Object[]{"Password"}));
            hasError = true;
        }
        if (!hasError) {
            new RewardsTask(this).execute(new String[]{this.username, this.password, null});
            showProgress(R.string.booking_progress, true);
        }
    }

    private void setLoginView() {
        setLoginView(false);
    }

    private void setLoginView(boolean setDefaults) {
        setContentView(R.layout.login);
        configureNavigationHeader();
        this.usernameText = (EditText) findViewById(R.id.usernameText);
        this.passwordText = (EditText) findViewById(R.id.passwordText);
        if (setDefaults) {
            if (!(this.usernameText == null || this.username == null)) {
                this.usernameText.setText(this.username);
            }
            if (!(this.passwordText == null || this.password == null)) {
                this.passwordText.setText(this.password);
            }
        }
        EditText passwordText = (EditText) findViewById(R.id.passwordText);
        if (passwordText != null) {
            passwordText.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId != 6 && (event == null || event.getAction() != 0 || event.getKeyCode() != 66)) {
                        return false;
                    }
                    RewardsActivity.this.tryLogin();
                    return true;
                }
            });
        }
        Button loginButton = (Button) findViewById(R.id.loginButton);
        if (loginButton != null) {
            loginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ODEONApplication.trackEvent("Account login OPC", "Click", "");
                    RewardsActivity.this.tryLogin();
                }
            });
        }
        ImageView opcSignupView = (ImageView) findViewById(R.id.opcSignupView);
        if (opcSignupView != null) {
            opcSignupView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    RewardsActivity.this.startActivity(new Intent(v.getContext(), OPCChoosePackageActivity.class));
                }
            });
        }
    }

    private void setLoggedInView() {
        setContentView(R.layout.rewards);
        configureNavigationHeader();
        TextView rewardsTopPointsTotal = (TextView) findViewById(R.id.rewardsTopPointsTotal);
        if (rewardsTopPointsTotal != null) {
            rewardsTopPointsTotal.setText(getString(R.string.rewards_points, new Object[]{this.data.getCustomerCardPointsBalance()}));
        }
        ImageButton myFilmsButton = (ImageButton) findViewById(R.id.btnMyFilms);
        if (myFilmsButton != null) {
            myFilmsButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ODEONApplication.trackEvent("OPC My films", "Click", "");
                    Intent i = new Intent(v.getContext(), RewardsMyFilmsActivity.class);
                    i.putExtra("filmsBookedAndRated", RewardsActivity.this.data.getFilmsBookedAndRated().toString());
                    i.putExtra("filmsBookedAndNotRated", RewardsActivity.this.data.getFilmsBookedAndNotRated().toString());
                    i.putExtra("otherRatedFilms", RewardsActivity.this.data.getOtherRatedFilms().toString());
                    RewardsActivity.this.startSubActivity(i, "My Films");
                }
            });
        }
        ImageButton myCardButton = (ImageButton) findViewById(R.id.btnMyCard);
        if (myCardButton != null) {
            myCardButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ODEONApplication.trackEvent("OPC My Card", "Click", "");
                    RewardsActivity.this.startActivity(new Intent(v.getContext(), RewardsMyCardActivity.class));
                }
            });
        }
        ImageButton myTransactionsButton = (ImageButton) findViewById(R.id.btnMyTransactions);
        if (myTransactionsButton != null) {
            myTransactionsButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ODEONApplication.trackEvent("OPC My transactions", "Click", "");
                    Intent i = new Intent(v.getContext(), RewardsMyTransactionsActivity.class);
                    i.putExtra("transactions", RewardsActivity.this.data.transactions);
                    RewardsActivity.this.startSubActivity(i, "My Transactions");
                }
            });
        }
        ImageButton pointsInfo = (ImageButton) findViewById(R.id.btnPointsInfo);
        if (pointsInfo != null) {
            pointsInfo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ODEONApplication.trackEvent("OPC Point Info", "Click", "");
                    Intent webviewIntent = new Intent(v.getContext(), WebviewActivity.class);
                    webviewIntent.putExtra(Constants.EXTRA_WEBVIEW_HEADER_TITLE, RewardsActivity.this.getString(R.string.rewards_header));
                    webviewIntent.putExtra(Constants.EXTRA_WEBVIEW_TITLE, RewardsActivity.this.getString(R.string.rewards_pointsinfo_text));
                    webviewIntent.putExtra(Constants.EXTRA_WEBVIEW_URL, Constants.formatLocationUrl(Constants.URL_OPC_POINTS_INFO));
                    RewardsActivity.this.startActivity(webviewIntent);
                }
            });
        }
        TextView hello = (TextView) findViewById(R.id.rewardsTopHello);
        if (hello != null) {
            hello.setText(getString(R.string.rewards_hello, new Object[]{this.data.getCustomerFirstName()}));
        }
    }

    public void setTaskResult(Rewards taskResult) {
        hideProgress(true);
        EditText passwordText = (EditText) findViewById(R.id.passwordText);
        if (passwordText != null) {
            hideKeyboard(passwordText);
        }
        if (taskResult == null) {
            showAlert(getString(R.string.rewards_error_text));
            this.hasValidLogin = false;
            setLoginView(true);
        } else if (taskResult.hasError()) {
            showAlert(taskResult.getError());
            this.hasValidLogin = false;
            setLoginView(true);
        } else {
            this.odeonApplication.saveCustomerLoginInPrefs(this.username, this.password);
            this.data = taskResult;
            this.hasValidLogin = true;
            setLoggedInView();
        }
    }

    protected void showAlert(String msg) {
        AlertDialog alertDialog = new Builder(getDialogContext()).create();
        alertDialog.setTitle(getString(R.string.rewards_error_title));
        alertDialog.setMessage(msg);
        alertDialog.setButton(getString(R.string.rewards_error_ok), new AnonymousClass8(alertDialog));
        alertDialog.show();
    }

    protected void configureNavigationHeader() {
        View header = getLayoutInflater().inflate(R.layout.header_cancel_ok, null, false);
        configureNavigationHeaderTitle(header, R.string.rewards_header);
        Button backButton = (Button) header.findViewById(R.id.navigationHeaderCancel);
        if (backButton != null) {
            backButton.setVisibility(4);
        }
        if (((Button) header.findViewById(R.id.navigationHeaderOk)) != null) {
            backButton.setVisibility(4);
        }
        inflateCustomHeader(header);
    }
}
