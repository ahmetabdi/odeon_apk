package uk.co.odeon.androidapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import java.io.File;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.custom.AutofitTextView;
import uk.co.odeon.androidapp.custom.CustomExceptionHandler;
import uk.co.odeon.androidapp.task.RewardsTaskCached;

public abstract class AbstractODEONBaseActivity extends Activity {
    protected static final int DIALOG_APP_INIT_FAILED = 30002;
    protected static final int DIALOG_CONNCHECK_NOCONN = 30001;
    protected static final String TAG;
    private ProgressDialog progressDialog;

    /* renamed from: uk.co.odeon.androidapp.activity.AbstractODEONBaseActivity.1 */
    class AnonymousClass1 implements OnClickListener {
        private final /* synthetic */ AlertDialog val$alertDialog;

        AnonymousClass1(AlertDialog alertDialog) {
            this.val$alertDialog = alertDialog;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$alertDialog.hide();
        }
    }

    static {
        TAG = AbstractODEONBaseActivity.class.getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupExceptionHandler();
    }

    protected void onResume() {
        super.onResume();
        requestRewards();
    }

    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    public boolean onSearchRequested() {
        return false;
    }

    private void requestRewards() {
        ODEONApplication a = ODEONApplication.getInstance();
        String e = a.getCustomerLoginEmail();
        String p = a.getCustomerLoginPassword();
        if (hasInternetConnection() && e != null && p != null) {
            new RewardsTaskCached(null).execute(new String[]{e, p, null});
        }
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_CONNCHECK_NOCONN /*30001*/:
                return createSimpleOKDialog(R.string.conncheck_noconn_alert_title, R.string.conncheck_noconn_alert_msg, R.string.conncheck_noconn_alert_ok);
            case DIALOG_APP_INIT_FAILED /*30002*/:
                return createSimpleOKDialog(R.string.appinit_failed_alert_title, R.string.appinit_failed_alert_msg, R.string.appinit_failed_alert_ok);
            default:
                return null;
        }
    }

    protected Dialog createSimpleOKDialog(int titleStrRes, int msgStringRes, int okStringRes) {
        AlertDialog alertDialog = new Builder(getDialogContext()).create();
        alertDialog.setTitle(getResources().getString(titleStrRes));
        alertDialog.setMessage(getResources().getString(msgStringRes));
        alertDialog.setButton(getResources().getString(okStringRes), new AnonymousClass1(alertDialog));
        return alertDialog;
    }

    private void setupExceptionHandler() {
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            File logdir = new File(getFilesDir(), "logs");
            logdir.mkdirs();
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(logdir.toString(), getResources().getString(R.string.exception_report_url), getVersionString()));
        }
    }

    protected String getVersionString() {
        try {
            return getPackageManager().getPackageInfo(getApplicationInfo().packageName, 0).versionName;
        } catch (NameNotFoundException e) {
            return "?";
        }
    }

    protected void showProgress(int msgResource) {
        showProgress(msgResource, false);
    }

    protected void showProgress(int msgResource, boolean ignoreExceptions) {
        if (this.progressDialog != null) {
            return;
        }
        if (ignoreExceptions) {
            try {
                this.progressDialog = ProgressDialog.show(getDialogContext(), null, getResources().getString(msgResource), true);
                return;
            } catch (Throwable e) {
                Log.e("BaseActivity", "Failed to show progress dialog", e);
                return;
            }
        }
        this.progressDialog = ProgressDialog.show(getDialogContext(), null, getResources().getString(msgResource), true);
    }

    protected void showCancelableProgress(int msgResource, OnCancelListener cancelListener) {
        showCancelableProgress(msgResource, cancelListener, false);
    }

    protected void showCancelableProgress(int msgResource, OnCancelListener cancelListener, boolean ignoreExceptions) {
        if (this.progressDialog != null) {
            return;
        }
        if (ignoreExceptions) {
            try {
                this.progressDialog = ProgressDialog.show(getDialogContext(), null, getResources().getString(msgResource), true, true, cancelListener);
                return;
            } catch (Throwable e) {
                Log.e("BaseActivity", "Failed to show progress dialog", e);
                return;
            }
        }
        this.progressDialog = ProgressDialog.show(getDialogContext(), null, getResources().getString(msgResource), true, true, cancelListener);
    }

    protected void hideProgress() {
        hideProgress(false);
    }

    protected void hideProgress(boolean ignoreExceptions) {
        if (this.progressDialog != null) {
            if (ignoreExceptions) {
                try {
                    this.progressDialog.dismiss();
                } catch (Throwable e) {
                    Log.e("BaseActivity", "Failed to hide progress dialog", e);
                }
            } else {
                this.progressDialog.dismiss();
            }
            this.progressDialog = null;
        }
    }

    protected void showAlert(String msg) {
        showAlert(msg, null);
    }

    protected void showAlert(String msg, String title) {
        try {
            Builder builder = new Builder(getDialogContext());
            if (msg == null) {
                msg = getResources().getString(R.string.default_error);
            }
            builder.setMessage(msg).setCancelable(false).setNeutralButton(getResources().getString(R.string.default_error_button_label), new OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            if (title != null) {
                builder.setTitle(title);
            }
            builder.create().show();
        } catch (Exception e) {
            Log.w(TAG, "Couldn't show booking error dialog: " + e.toString(), e);
        }
    }

    protected Context getDialogContext() {
        if (getParent() != null) {
            return getParent();
        }
        return this;
    }

    protected boolean hasInternetConnection() {
        NetworkInfo activeNetwork = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getState() == State.CONNECTED;
    }

    protected boolean checkConnectivityOrPreCachedInitDataAvailable() {
        if (hasInternetConnection()) {
            return true;
        }
        if (hasRelativelyFreshAppInitContent()) {
            showDialog(DIALOG_CONNCHECK_NOCONN);
            return true;
        }
        startActivity(new Intent(getDialogContext(), ConnectivityWaitActivity.class));
        finish();
        return false;
    }

    protected boolean hasRelativelyFreshAppInitContent() {
        return System.currentTimeMillis() - getLastAppInitTS() < Constants.APPINIT_DELAY_REQUIRED_IF_NO_CONNECTIVITY;
    }

    protected long getLastAppInitTS() {
        return ODEONApplication.getInstance().getPrefs().getLong(Constants.PREF_LASTINIT_TS, 0);
    }

    protected void configureNavigationHeaderTitle(int titleRessourceId) {
        configureNavigationHeaderTitle(null, titleRessourceId);
    }

    protected void configureNavigationHeaderTitle(View view, int titleRessourceId) {
        TextView navigationHeaderTitle = view != null ? (TextView) view.findViewById(R.id.navigationHeaderTitle) : (TextView) findViewById(R.id.navigationHeaderTitle);
        if (navigationHeaderTitle != null) {
            navigationHeaderTitle.setText(titleRessourceId);
        }
    }

    protected void configureNavigationHeaderTitle(String title) {
        TextView navigationHeaderTitle = (TextView) findViewById(R.id.navigationHeaderTitle);
        if (navigationHeaderTitle != null) {
            navigationHeaderTitle.setText(title);
        }
    }

    protected void configureNavigationHeaderCancel(View.OnClickListener onClickListener) {
        configureNavigationHeaderCancel(null, null, -1, onClickListener);
    }

    protected void configureNavigationHeaderCancel(String label, View.OnClickListener onClickListener) {
        configureNavigationHeaderCancel(null, label, -1, onClickListener);
    }

    protected void configureNavigationHeaderCancel(String label, int drawableRessourceId, View.OnClickListener onClickListener) {
        configureNavigationHeaderCancel(null, label, drawableRessourceId, onClickListener);
    }

    protected void configureNavigationHeaderCancel(View view, String label, int drawableRessourceId, View.OnClickListener onClickListener) {
        Button backButton = view != null ? (Button) view.findViewById(R.id.navigationHeaderCancel) : (Button) findViewById(R.id.navigationHeaderCancel);
        if (backButton != null) {
            if (label != null) {
                backButton.setText(label);
            }
            if (drawableRessourceId > 0) {
                backButton.setBackgroundDrawable(getResources().getDrawable(drawableRessourceId));
            }
            if (onClickListener != null) {
                backButton.setOnClickListener(onClickListener);
            }
        }
    }

    protected void configureNavigationHeaderNext(View.OnClickListener onClickListener) {
        configureNavigationHeaderNext(null, null, -1, onClickListener);
    }

    protected void configureNavigationHeaderNext(String label, View.OnClickListener onClickListener) {
        configureNavigationHeaderNext(null, label, -1, onClickListener);
    }

    protected void configureNavigationHeaderNext(String label, int drawableRessourceId, View.OnClickListener onClickListener) {
        configureNavigationHeaderNext(null, label, drawableRessourceId, onClickListener);
    }

    protected void configureNavigationHeaderNext(View view, String label, int drawableRessourceId, View.OnClickListener onClickListener) {
        Button nextButton = view != null ? (Button) view.findViewById(R.id.navigationHeaderOk) : (Button) findViewById(R.id.navigationHeaderOk);
        if (nextButton != null) {
            nextButton.setVisibility(0);
            if (label != null) {
                nextButton.setText(label);
            }
            if (drawableRessourceId > 0) {
                nextButton.setBackgroundDrawable(getResources().getDrawable(drawableRessourceId));
            }
            if (onClickListener != null) {
                nextButton.setOnClickListener(onClickListener);
            }
        }
    }

    protected void configureSubHeaderFilm(String row1Text, String row2Text) {
        AutofitTextView infoHeaderRow1 = (AutofitTextView) findViewById(R.id.infoHeaderRow1);
        if (infoHeaderRow1 != null) {
            infoHeaderRow1.setText(row1Text);
        }
        AutofitTextView infoHeaderRow2 = (AutofitTextView) findViewById(R.id.infoHeaderRow2);
        if (infoHeaderRow2 != null) {
            infoHeaderRow2.setText(row2Text);
        }
    }
}
