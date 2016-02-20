package uk.co.odeon.androidapp;

import android.app.Activity;
import android.app.Application;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import com.google.analytics.tracking.android.GoogleAnalytics;
import twitter4j.conf.PropertyConfiguration;
import uk.co.odeon.androidapp.Constants.APP_LOCATION;
import uk.co.odeon.androidapp.activity.AboutActivity;
import uk.co.odeon.androidapp.activity.FilmScheduleActivity;
import uk.co.odeon.androidapp.activity.booking.BookingCheckUserDetailsActivity;
import uk.co.odeon.androidapp.activity.booking.BookingLoginActivity;
import uk.co.odeon.androidapp.activity.opc.OPCChoosePackageActivity;
import uk.co.odeon.androidapp.provider.FilmContent.FilmColumns;
import uk.co.odeon.androidapp.provider.FilmContent.FilmDetailsColumns;
import uk.co.odeon.androidapp.provider.SiteContent.SiteColumns;
import uk.co.odeon.androidapp.provider.SiteContent.SiteFavouriteColumns;
import uk.co.odeon.androidapp.task.FilmListScheduleTaskCached;
import uk.co.odeon.androidapp.task.RewardsTaskCached;
import uk.co.odeon.androidapp.util.drawable.DrawableManager;

public class ODEONApplication extends Application {
    private static ODEONApplication INSTANCE = null;
    private static final String PREFS_CUSTOMER_DATA = "CustomerData";
    private static final String PREFS_NAME = "AppInitPrefs58";
    private SharedPreferences prefs;
    private SharedPreferences prefsCustomerData;

    static {
        INSTANCE = null;
    }

    public ODEONApplication() {
        this.prefs = null;
        this.prefsCustomerData = null;
    }

    public static ODEONApplication getInstance() {
        return INSTANCE;
    }

    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    public SharedPreferences getPrefs() {
        if (this.prefs == null) {
            this.prefs = getSharedPreferences(PREFS_NAME, 0);
        }
        return this.prefs;
    }

    public SharedPreferences getCustomerDataPrefs() {
        if (this.prefsCustomerData == null) {
            this.prefsCustomerData = getSharedPreferences(PREFS_CUSTOMER_DATA, 0);
        }
        return this.prefsCustomerData;
    }

    public boolean saveCustomerLoginInPrefs(String username, String password) {
        return getCustomerDataPrefs().edit().putString(Constants.CUSTOMER_PREFS_USERNAME, username).commit() && getCustomerDataPrefs().edit().putString(PropertyConfiguration.PASSWORD, password).commit();
    }

    public boolean hasCustomerLoginInPrefs() {
        String username = getCustomerDataPrefs().getString(Constants.CUSTOMER_PREFS_USERNAME, null);
        String password = getCustomerDataPrefs().getString(PropertyConfiguration.PASSWORD, null);
        return username != null && password != null && username.trim().length() > 0 && password.trim().length() > 0;
    }

    public String getCustomerLoginEmail() {
        return getCustomerDataPrefs().getString(Constants.CUSTOMER_PREFS_USERNAME, null);
    }

    public String getCustomerLoginPassword() {
        return getCustomerDataPrefs().getString(PropertyConfiguration.PASSWORD, null);
    }

    public boolean clearCustomerLoginInPrefs() {
        return getCustomerDataPrefs().edit().remove(Constants.CUSTOMER_PREFS_USERNAME).remove(PropertyConfiguration.PASSWORD).commit();
    }

    public boolean clearCustomerDataInPrefs() {
        DrawableManager.getInstance().deleteImageCacheFilesByPattern(String.format(Constants.BITMAP_OPC_CARD_BARCODE_FILE_NAME, new Object[]{".*\\"}));
        return getCustomerDataPrefs().edit().clear().commit();
    }

    public boolean clearCacheData() {
        FilmListScheduleTaskCached.clearCache();
        RewardsTaskCached.clearCache();
        Editor prefsEditor = getPrefs().edit();
        for (String siteWithDataHash : this.prefs.getString(Constants.PREFS_DATAHASH_ADD_SCHEDULE_INFO_SITES, "").split(",")) {
            prefsEditor.remove(new StringBuilder(Constants.PREFS_DATAHASH_ADD_SCHEDULE_INFO).append(siteWithDataHash).toString());
        }
        prefsEditor.remove(Constants.PREFS_DATAHASH_ADD_SCHEDULE_INFO_SITES);
        return prefsEditor.remove(FilmDetailsColumns.DATA_HASH).remove(Constants.PREF_LASTINIT_TS).remove(Constants.PREF_SITEDIST_POSTCODE_LASTTS).remove(Constants.PREF_SITEDIST_LASTTS).remove(Constants.PREF_SITEDIST_LAST_POSTCODE_LOC).remove(Constants.PREF_SITEDIST_LASTLOC).commit();
    }

    public void removeSiteSpecificDatahashFromPreferences(String siteId, String dataHashKey, String dataHashSitesKey) {
        this.prefs.edit().remove(new StringBuilder(String.valueOf(dataHashKey)).append(siteId).toString()).commit();
        String sitesWithDataHash = this.prefs.getString(dataHashSitesKey, "");
        if (sitesWithDataHash.equals(siteId)) {
            this.prefs.edit().remove(dataHashSitesKey).commit();
            return;
        }
        String sitesWithDataHashFiltered = "";
        String[] sitesWithDataHashArray = sitesWithDataHash.split(",");
        for (int i = 0; i < sitesWithDataHashArray.length; i++) {
            if (!sitesWithDataHashArray[i].equals(siteId)) {
                if (sitesWithDataHashFiltered.length() <= 0) {
                    sitesWithDataHashFiltered = sitesWithDataHashArray[i];
                } else {
                    sitesWithDataHashFiltered = new StringBuilder(String.valueOf(sitesWithDataHashFiltered)).append(",").append(sitesWithDataHashArray[i]).toString();
                }
            }
        }
        this.prefs.edit().putString(dataHashSitesKey, new StringBuilder(String.valueOf(sitesWithDataHash)).append(",").append(siteId).toString()).commit();
    }

    public APP_LOCATION getChoosenLocation() {
        try {
            return APP_LOCATION.valueOf(getPrefs().getString(Constants.PREFS_LOCATION, APP_LOCATION.uk.toString()));
        } catch (IllegalArgumentException e) {
            clearChoosenLocation();
            return APP_LOCATION.uk;
        }
    }

    public void setChoosenLocation(APP_LOCATION appLocation) {
        getPrefs().edit().putString(Constants.PREFS_LOCATION, appLocation.toString()).commit();
    }

    public boolean hasChoosenLocation() {
        return getPrefs().contains(Constants.PREFS_LOCATION);
    }

    public void clearChoosenLocation() {
        getPrefs().edit().remove(Constants.PREFS_LOCATION).commit();
    }

    public int certStringToImageResource(String certStr) {
        if (getChoosenLocation().equals(APP_LOCATION.ire)) {
            if ("12A".equalsIgnoreCase(certStr)) {
                return R.drawable.cert_ire_12a;
            }
            if ("15A".equals(certStr)) {
                return R.drawable.cert_ire_15a;
            }
            if ("16".equals(certStr)) {
                return R.drawable.cert_ire_16;
            }
            if ("18".equals(certStr)) {
                return R.drawable.cert_ire_18;
            }
            if ("PG".equalsIgnoreCase(certStr)) {
                return R.drawable.cert_ire_pg;
            }
            if ("G".equalsIgnoreCase(certStr)) {
                return R.drawable.cert_ire_g;
            }
            return R.drawable.cert_ire_tbc;
        } else if ("12".equals(certStr)) {
            return R.drawable.cert_12;
        } else {
            if ("12A".equalsIgnoreCase(certStr)) {
                return R.drawable.cert_12a;
            }
            if ("15".equals(certStr)) {
                return R.drawable.cert_15;
            }
            if ("18".equals(certStr)) {
                return R.drawable.cert_18;
            }
            if ("PG".equalsIgnoreCase(certStr)) {
                return R.drawable.cert_pg;
            }
            if ("U".equalsIgnoreCase(certStr)) {
                return R.drawable.cert_u;
            }
            return R.drawable.cert_tbc;
        }
    }

    public Cursor getFilmDataCursor(Activity activity, int id) {
        if (id <= 0) {
            return null;
        }
        Cursor cursor = activity.managedQuery(ContentUris.withAppendedId(FilmColumns.CONTENT_URI, (long) id), null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getCinemaDataCursor(Activity activity, Uri siteUri) {
        if (siteUri == null) {
            return null;
        }
        Cursor cursor = activity.managedQuery(siteUri, null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getCinemaDataCursor(Activity activity, int id) {
        if (id > 0) {
            return getCinemaDataCursor(activity, ContentUris.withAppendedId(SiteColumns.CONTENT_URI, (long) id));
        }
        return null;
    }

    public boolean checkAndPerformEntryPointOverwrite() {
        if (getResources().getInteger(R.integer.dev:entry_point) == getResources().getInteger(R.integer.dev:entry_point_type_schedule)) {
            Intent filmSchedule = new Intent(this, FilmScheduleActivity.class);
            filmSchedule.addFlags(268435456);
            filmSchedule.putExtra(Constants.EXTRA_FILM_ID, getResources().getInteger(R.integer.dev:film_id));
            filmSchedule.putExtra(Constants.EXTRA_CINEMA_ID, getResources().getInteger(R.integer.dev:cinema_id));
            startActivity(filmSchedule);
            return true;
        } else if (getResources().getInteger(R.integer.dev:entry_point) == getResources().getInteger(R.integer.dev:entry_point_type_booking)) {
            Intent bookingLogin = new Intent(this, BookingLoginActivity.class);
            bookingLogin.addFlags(268435456);
            bookingLogin.putExtra(Constants.EXTRA_PERFORMANCE_ID, getResources().getString(R.string.dev:performance_id));
            bookingLogin.putExtra(Constants.EXTRA_CINEMA_ID, getResources().getInteger(R.integer.dev:cinema_id));
            startActivity(bookingLogin);
            return true;
        } else if (getResources().getInteger(R.integer.dev:entry_point) == getResources().getInteger(R.integer.dev:entry_point_type_about)) {
            Intent about = new Intent(this, AboutActivity.class);
            about.addFlags(268435456);
            startActivity(about);
            return true;
        } else if (getResources().getInteger(R.integer.dev:entry_point) == getResources().getInteger(R.integer.dev:entry_point_type_opc)) {
            Intent opc = new Intent(this, OPCChoosePackageActivity.class);
            opc.addFlags(268435456);
            startActivity(opc);
            return true;
        } else if (getResources().getInteger(R.integer.dev:entry_point) != getResources().getInteger(R.integer.dev:entry_point_type_booking_confirm)) {
            return false;
        } else {
            Intent bookingConf = new Intent(this, BookingCheckUserDetailsActivity.class);
            bookingConf.addFlags(268435456);
            bookingConf.putExtra("url", getString(R.string.dev:booking_confirm_url));
            startActivity(bookingConf);
            return true;
        }
    }

    public void changeFilmFavouriteInDatabase(int filmId, boolean isFavourite) {
        if (filmId > 0) {
            ContentValues cv = new ContentValues();
            cv.put(FilmColumns.FAVOURITE, isFavourite ? Integer.valueOf(1) : Integer.valueOf(0));
            getContentResolver().update(ContentUris.withAppendedId(FilmColumns.CONTENT_URI, (long) filmId), cv, null, null);
        }
    }

    public void changeCinemaFavouriteInDatabase(int cinemaId, boolean isFavourite) {
        if (cinemaId > 0) {
            Uri uri = ContentUris.withAppendedId(SiteFavouriteColumns.CONTENT_URI, (long) cinemaId);
            if (isFavourite) {
                ContentValues cv = new ContentValues();
                cv.put("_id", Integer.valueOf(cinemaId));
                getContentResolver().insert(uri, cv);
                return;
            }
            getContentResolver().delete(uri, null, null);
        }
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.length() <= 0) {
            return false;
        }
        return email.matches("(?:[a-z0-9!#$%\\&'*+/=?\\^_`{|}~-]+(?:\\.[a-z0-9!#$%\\&'*+/=?\\^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() <= 0 || password.length() < 6 || password.length() > 20) {
            return false;
        }
        return true;
    }

    public static void trackEvent(String category, String action, String label) {
        GoogleAnalytics.getInstance(getInstance().getApplicationContext()).getDefaultTracker().trackEvent(category, action, label, Long.valueOf(0));
    }

    public static boolean hasSystemSharedLibraryInstalled(Context ctx, String libraryName) {
        if (TextUtils.isEmpty(libraryName)) {
            return false;
        }
        String[] installedLibraries = ctx.getPackageManager().getSystemSharedLibraryNames();
        if (installedLibraries == null) {
            return false;
        }
        for (String s : installedLibraries) {
            if (libraryName.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasSystemGoogleMapsInstalled(Context ctx) {
        return hasSystemSharedLibraryInstalled(ctx, "com.google.android.maps");
    }
}
