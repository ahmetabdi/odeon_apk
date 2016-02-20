package uk.co.odeon.androidapp.util.calendar;

import android.content.Context;
import android.os.Build.VERSION;

public class CalendarEntryCreatorFactory {
    public CalendarEntryCreator getCalendarEntryCreator(Context ctx) {
        if (VERSION.SDK_INT >= 14) {
            return new CalendarEntryCreatorImplSDK14(ctx);
        }
        return new CalendarEntryCreatorImpl(ctx);
    }
}
