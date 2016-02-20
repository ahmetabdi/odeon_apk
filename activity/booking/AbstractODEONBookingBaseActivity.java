package uk.co.odeon.androidapp.activity.booking;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.Log;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.activity.AbstractODEONBaseActivity;

public abstract class AbstractODEONBookingBaseActivity extends AbstractODEONBaseActivity {
    protected static final String TAG;

    protected abstract void configureNavigationHeader();

    static {
        TAG = AbstractODEONBookingBaseActivity.class.getSimpleName();
    }

    protected void showBookingAlert() {
        showBookingAlert(null);
    }

    protected void showBookingAlert(String msg) {
        try {
            Builder builder = new Builder(this);
            if (msg == null) {
                msg = getResources().getString(R.string.booking_error);
            }
            builder.setMessage(msg).setCancelable(false).setNeutralButton(getResources().getString(R.string.booking_error_button_label), new OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.create().show();
        } catch (Exception e) {
            Log.w(TAG, "Couldn't show booking error dialog: " + e.toString(), e);
        }
    }

    protected void showBookingAbortAlert(String msg) {
        try {
            Builder builder = new Builder(this);
            builder.setMessage(msg).setCancelable(false).setNeutralButton(getResources().getString(R.string.booking_error_abort_button_label), new OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    AbstractODEONBookingBaseActivity.this.performEndOfBooking();
                }
            });
            builder.create().show();
        } catch (Exception e) {
            Log.w(TAG, "Couldn't show booking error dialog: " + e.toString(), e);
        }
    }

    protected void showBookingWarning(String msg) {
        showBookingWarning(null, msg);
    }

    protected void showBookingWarning(String title, String msg) {
        try {
            Builder builder = new Builder(this);
            builder.setTitle(title).setMessage(msg).setCancelable(false).setNeutralButton(getResources().getString(R.string.booking_warning_button_label), new OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.create().show();
        } catch (Exception e) {
            Log.w(TAG, "Couldn't show booking error dialog: " + e.toString(), e);
        }
    }

    protected void performRestartOfBooking() {
        Intent bookingSectionSelection = new Intent(this, BookingSectionSelectionActivity.class);
        bookingSectionSelection.setFlags(67108864);
        bookingSectionSelection.setAction(Constants.ACTION_RESTART_BOOKING);
        startActivity(bookingSectionSelection);
    }

    protected void performEndOfBooking() {
        Intent bookingLogin = new Intent(this, BookingLoginActivity.class);
        bookingLogin.setFlags(67108864);
        bookingLogin.setAction(Constants.ACTION_END_OF_BOOKING);
        startActivity(bookingLogin);
    }
}
