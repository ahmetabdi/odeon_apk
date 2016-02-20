package uk.co.odeon.androidapp.util.calendar;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.Log;
import com.google.analytics.tracking.android.ModelFields;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.provider.OfferContent.OfferColumns;

public class CalendarEntryCreatorImpl implements CalendarEntryCreator {
    protected static final String TAG;
    protected Context ctx;
    protected int strIdErrorCode;
    protected int strIdErrorMsg;
    protected int strIdErrorTitle;

    /* renamed from: uk.co.odeon.androidapp.util.calendar.CalendarEntryCreatorImpl.1 */
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
        TAG = CalendarEntryCreatorImpl.class.getSimpleName();
    }

    public CalendarEntryCreatorImpl(Context ctx) {
        this.strIdErrorTitle = R.string.calentry_failed_alert_title;
        this.strIdErrorMsg = R.string.calentry_failed_alert_msg;
        this.strIdErrorCode = R.string.calentry_failed_alert_ok;
        this.ctx = ctx;
    }

    protected String getContentType() {
        return "vnd.android.cursor.item/event";
    }

    protected String getTitleAttribute() {
        return OfferColumns.TITLE;
    }

    protected String getDescriptionAttribute() {
        return ModelFields.DESCRIPTION;
    }

    protected String getBeginTimeAttribute() {
        return "beginTime";
    }

    protected String getEndTimeAttribute() {
        return "endTime";
    }

    protected String getAllDayAttribute() {
        return "allDay";
    }

    protected String getAccessLevelAttribute() {
        return "accessLevel";
    }

    protected String getAvailabilityAttribute() {
        return "availability";
    }

    protected String getLocationAttribute() {
        return "eventLocation";
    }

    protected int getAvailabilityAttributeValueFree() {
        return 1;
    }

    protected int getAccessLevelValuePrivate() {
        return 2;
    }

    public boolean openCalendarEntryDialog(CalendarEntry calEntry, boolean showErrorMessageOnFail) {
        Intent intent = new Intent("android.intent.action.EDIT");
        intent.setType(getContentType());
        intent.putExtra(getTitleAttribute(), calEntry.getTitle());
        if (calEntry.getDescription() != null) {
            intent.putExtra(getDescriptionAttribute(), calEntry.getDescription());
        }
        intent.putExtra(getBeginTimeAttribute(), calEntry.getBeginTimeMillis());
        intent.putExtra(getEndTimeAttribute(), calEntry.getEndTimeMillis());
        if (calEntry.getAllDay() != null) {
            intent.putExtra(getAllDayAttribute(), calEntry.getAllDay());
        }
        if (calEntry.isPrivateEntry()) {
            intent.putExtra(getAccessLevelAttribute(), getAccessLevelValuePrivate());
        }
        if (calEntry.isMarkAsAvailabilityFree()) {
            intent.putExtra(getAvailabilityAttribute(), getAvailabilityAttributeValueFree());
        }
        if (calEntry.getLocation() != null) {
            intent.putExtra(getLocationAttribute(), calEntry.getLocation());
        }
        try {
            this.ctx.startActivity(intent);
            return true;
        } catch (Throwable e) {
            Log.e(TAG, "Failed to open calendar entry " + calEntry, e);
            createAlertDialog().show();
            return false;
        }
    }

    protected Dialog createAlertDialog() {
        AlertDialog alertDialog = new Builder(this.ctx).create();
        alertDialog.setTitle(this.ctx.getResources().getString(this.strIdErrorTitle));
        alertDialog.setMessage(this.ctx.getResources().getString(this.strIdErrorMsg));
        alertDialog.setButton(this.ctx.getResources().getString(this.strIdErrorCode), new AnonymousClass1(alertDialog));
        return alertDialog;
    }
}
