package uk.co.odeon.androidapp.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import uk.co.odeon.androidapp.Constants.APP_LOCATION;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.adapters.RewardsTransactionsListAdapter;
import uk.co.odeon.androidapp.custom.NavigatorBarSubActivity;
import uk.co.odeon.androidapp.json.Rewards;
import uk.co.odeon.androidapp.task.RewardsTaskCached;
import uk.co.odeon.androidapp.task.TaskTarget;

public class RewardsMyTransactionsActivity extends NavigatorBarSubActivity implements TaskTarget<Rewards> {
    protected static final String TAG;

    /* renamed from: uk.co.odeon.androidapp.activity.RewardsMyTransactionsActivity.1 */
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
        TAG = RewardsMyTransactionsActivity.class.getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewards_my_transactions);
    }

    public void onResume() {
        super.onResume();
        if (ODEONApplication.getInstance().hasCustomerLoginInPrefs()) {
            refreshRewardData();
        } else {
            onBackPressed();
        }
    }

    public void onRecycled() {
        super.onRecycled();
        if (ODEONApplication.getInstance().hasCustomerLoginInPrefs()) {
            refreshRewardData();
        } else {
            onBackPressed();
        }
    }

    public void setTaskResult(Rewards taskResult) {
        hideProgress(true);
        if (taskResult == null) {
            showAlert(getString(R.string.rewards_error_text));
        } else if (taskResult.hasError()) {
            showAlert(taskResult.getError());
        } else {
            buildTransactionTable(taskResult);
        }
    }

    protected void showAlert(String msg) {
        AlertDialog alertDialog = new Builder(getDialogContext()).create();
        alertDialog.setTitle(getString(R.string.rewards_error_title));
        alertDialog.setMessage(msg);
        alertDialog.setButton(getString(R.string.rewards_error_ok), new AnonymousClass1(alertDialog));
        alertDialog.show();
    }

    protected void refreshRewardData() {
        ODEONApplication app = ODEONApplication.getInstance();
        new RewardsTaskCached(this).execute(new String[]{app.getCustomerLoginEmail(), app.getCustomerLoginPassword(), null});
        showCancelableProgress(R.string.booking_progress, new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                RewardsMyTransactionsActivity.this.hideProgress(true);
                RewardsMyTransactionsActivity.this.onBackPressed();
            }
        }, true);
    }

    protected void buildTransactionTable(Rewards data) {
        TextView expiryPointsDateTextView = (TextView) findViewById(R.id.expiryPointsDate);
        if (expiryPointsDateTextView != null) {
            String customerCardExpiryDate = data.getCustomerCardExpiryDate();
            if (customerCardExpiryDate == null || customerCardExpiryDate.length() <= 0) {
                expiryPointsDateTextView.setText("");
                expiryPointsDateTextView.setVisibility(8);
            } else {
                expiryPointsDateTextView.setText(getString(R.string.rewards_mytransactions_expiry_points_date, new Object[]{customerCardExpiryDate}));
                expiryPointsDateTextView.setVisibility(0);
            }
        }
        TextView expiryPointsPointsTextView = (TextView) findViewById(R.id.expiryPointsPoints);
        if (expiryPointsPointsTextView != null) {
            String customerCardExpiryPoints = data.getCustomerCardExpiryPoints();
            if (customerCardExpiryPoints == null || customerCardExpiryPoints.length() <= 0) {
                expiryPointsPointsTextView.setText("");
                expiryPointsPointsTextView.setVisibility(8);
            } else {
                expiryPointsPointsTextView.setText(customerCardExpiryPoints);
                expiryPointsPointsTextView.setVisibility(0);
            }
        }
        TextView expiryPointsInfoTextView = (TextView) findViewById(R.id.expiryPointsInfo);
        if (expiryPointsInfoTextView != null) {
            String domain = ODEONApplication.getInstance().getChoosenLocation().equals(APP_LOCATION.ire) ? "odeoncinemas.ie" : "odeon.co.uk";
            expiryPointsInfoTextView.setText(getString(R.string.rewards_mytransactions_expiry_points_info, new Object[]{domain}));
        }
        ListView transactionTable = (ListView) findViewById(R.id.rewardsTransactions);
        if (data.transactions != null && transactionTable != null) {
            transactionTable.setAdapter(new RewardsTransactionsListAdapter(this, data.transactions));
        }
    }
}
