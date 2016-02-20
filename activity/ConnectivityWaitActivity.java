package uk.co.odeon.androidapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity.RootActivity;
import uk.co.odeon.androidapp.updateservice.AbstractUpdateService.UpdateServiceSimpleStatus;
import uk.co.odeon.androidapp.updateservice.AppInitUpdateService;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;

public class ConnectivityWaitActivity extends AbstractODEONBaseActivity {
    protected static final String TAG;
    private IntentFilter appInitBroadCastFilter;
    private BroadcastReceiver appInitBroadCastReceiver;
    private Timer timer;

    public ConnectivityWaitActivity() {
        this.appInitBroadCastReceiver = new BroadcastReceiver() {
            private static /* synthetic */ int[] $SWITCH_TABLE$uk$co$odeon$androidapp$updateservice$AbstractUpdateService$UpdateServiceSimpleStatus;

            static /* synthetic */ int[] $SWITCH_TABLE$uk$co$odeon$androidapp$updateservice$AbstractUpdateService$UpdateServiceSimpleStatus() {
                int[] iArr = $SWITCH_TABLE$uk$co$odeon$androidapp$updateservice$AbstractUpdateService$UpdateServiceSimpleStatus;
                if (iArr == null) {
                    iArr = new int[UpdateServiceSimpleStatus.values().length];
                    try {
                        iArr[UpdateServiceSimpleStatus.DONE_NOCHANGES.ordinal()] = 3;
                    } catch (NoSuchFieldError e) {
                    }
                    try {
                        iArr[UpdateServiceSimpleStatus.DONE_NOTREQ.ordinal()] = 2;
                    } catch (NoSuchFieldError e2) {
                    }
                    try {
                        iArr[UpdateServiceSimpleStatus.DONE_UPDATED.ordinal()] = 4;
                    } catch (NoSuchFieldError e3) {
                    }
                    try {
                        iArr[UpdateServiceSimpleStatus.FAILED.ordinal()] = 6;
                    } catch (NoSuchFieldError e4) {
                    }
                    try {
                        iArr[UpdateServiceSimpleStatus.RUNNING.ordinal()] = 1;
                    } catch (NoSuchFieldError e5) {
                    }
                    try {
                        iArr[UpdateServiceSimpleStatus.SLOW.ordinal()] = 5;
                    } catch (NoSuchFieldError e6) {
                    }
                    $SWITCH_TABLE$uk$co$odeon$androidapp$updateservice$AbstractUpdateService$UpdateServiceSimpleStatus = iArr;
                }
                return iArr;
            }

            public void onReceive(Context context, Intent intent) {
                Log.i(ConnectivityWaitActivity.TAG, "AppInit status broadcast received: " + intent);
                UpdateServiceSimpleStatus status = (UpdateServiceSimpleStatus) intent.getExtras().getParcelable(Constants.EXTRA_UPDATESERVICE_STATUS);
                String msgHeader = intent.getExtras().getString(Constants.EXTRA_UPDATESERVICE_MSG_HEADER);
                String msgText = intent.getExtras().getString(Constants.EXTRA_UPDATESERVICE_MSG_TEXT);
                Log.i(ConnectivityWaitActivity.TAG, "AppInit status broadcast received, status is: " + status);
                switch (AnonymousClass1.$SWITCH_TABLE$uk$co$odeon$androidapp$updateservice$AbstractUpdateService$UpdateServiceSimpleStatus()[status.ordinal()]) {
                    case AmazingListView.PINNED_HEADER_VISIBLE /*1*/:
                    case AmazingListView.PINNED_HEADER_PUSHED_UP /*2*/:
                    case RootActivity.TYPE_RIGHT /*3*/:
                    case RootActivity.TYPE_SINGLE /*4*/:
                        if (ConnectivityWaitActivity.this.timer != null) {
                            ConnectivityWaitActivity.this.timer.cancel();
                        }
                        ConnectivityWaitActivity.this.hideProgress(true);
                        ConnectivityWaitActivity.this.startActivity(new Intent(ConnectivityWaitActivity.this.getDialogContext(), MainTabActivity.class));
                    case R.styleable.com_deezapps_widget_PagerControl_useCircles /*5*/:
                        ConnectivityWaitActivity.this.showProgress(R.string.app_init_progress_retry, true);
                    case R.styleable.com_deezapps_widget_PagerControl_circlePadding /*6*/:
                        ConnectivityWaitActivity.this.hideProgress(true);
                        if (msgHeader != null && msgText != null) {
                            ConnectivityWaitActivity.this.showAlert(msgHeader, msgText);
                        }
                    default:
                        Log.w(ConnectivityWaitActivity.TAG, "Unknown AppInit status: " + status + ", just hiding progress bar");
                        ConnectivityWaitActivity.this.hideProgress(true);
                }
            }
        };
    }

    static {
        TAG = ConnectivityWaitActivity.class.getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connectivity_wait);
        this.appInitBroadCastFilter = new IntentFilter(Constants.ACTION_APPINIT_STATUS);
        registerReceiver(this.appInitBroadCastReceiver, this.appInitBroadCastFilter);
        setupTimer();
        ((TextView) findViewById(R.id.connWaitText)).setText(getResources().getString(hasInternetConnection() ? R.string.connwait_text_failedconn : R.string.connwait_text_noconn));
    }

    private void setupTimer() {
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (ConnectivityWaitActivity.this.hasInternetConnection()) {
                    ConnectivityWaitActivity.this.startService(new Intent(ConnectivityWaitActivity.this.getDialogContext(), AppInitUpdateService.class));
                }
            }
        }, 10000, 10000);
    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume " + getIntent());
        this.appInitBroadCastFilter = new IntentFilter(Constants.ACTION_APPINIT_STATUS);
        registerReceiver(this.appInitBroadCastReceiver, this.appInitBroadCastFilter);
        setupTimer();
    }

    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        this.timer.cancel();
        this.timer = null;
        if (this.appInitBroadCastReceiver != null) {
            unregisterReceiver(this.appInitBroadCastReceiver);
        }
        hideProgress(true);
    }
}
