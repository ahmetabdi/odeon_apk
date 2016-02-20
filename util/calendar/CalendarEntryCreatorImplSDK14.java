package uk.co.odeon.androidapp.util.calendar;

import android.content.Context;
import com.google.analytics.tracking.android.ModelFields;
import uk.co.odeon.androidapp.provider.OfferContent.OfferColumns;

public class CalendarEntryCreatorImplSDK14 extends CalendarEntryCreatorImpl {
    public CalendarEntryCreatorImplSDK14(Context ctx) {
        super(ctx);
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

    protected int getAvailabilityAttributeValueFree() {
        return 1;
    }

    protected int getAccessLevelValuePrivate() {
        return 2;
    }

    protected String getLocationAttribute() {
        return "eventLocation";
    }
}
