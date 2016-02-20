package uk.co.odeon.androidapp.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.provider.OfferContent.OfferColumns;
import uk.co.odeon.androidapp.provider.SiteContent.SiteFavouriteColumns;

public class OfferContentProvider extends ContentProvider {
    private static final int OFFERS = 1;
    private static final int OFFER_ID = 2;
    protected static final String TAG;
    private static final UriMatcher sUriMatcher;
    private DatabaseHelper dbHelper;

    static {
        TAG = OfferContentProvider.class.getSimpleName();
        sUriMatcher = new UriMatcher(-1);
        sUriMatcher.addURI(OfferContent.AUTHORITY, Constants.CUSTOMER_PREFS_OFFERS, OFFERS);
        sUriMatcher.addURI(OfferContent.AUTHORITY, "offers/#", OFFER_ID);
    }

    public OfferContentProvider(Context context) {
    }

    public boolean onCreate() {
        this.dbHelper = DatabaseHelper.getInstance(getContext());
        return true;
    }

    protected SQLiteDatabase getDatabase() {
        return this.dbHelper.getReadableDatabase();
    }

    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case OFFERS /*1*/:
                return OfferColumns.CONTENT_TYPE;
            case OFFER_ID /*2*/:
                return OfferColumns.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String orderBy = null;
        switch (sUriMatcher.match(uri)) {
            case OFFERS /*1*/:
                orderBy = TextUtils.isEmpty(sortOrder) ? SiteFavouriteColumns.DEFAULT_SORT_ORDER : sortOrder;
                qb.setTables(Constants.DBTABLE_OFFER);
                break;
            case OFFER_ID /*2*/:
                qb.setTables(Constants.DBTABLE_OFFER);
                qb.appendWhere("_id=" + ((String) uri.getPathSegments().get(OFFERS)));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        try {
            Cursor c = qb.query(getDatabase(), projection, selection, selectionArgs, null, null, orderBy);
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        } catch (SQLiteException e) {
            Log.w(TAG, "Failed to access database: " + e.getMessage(), e);
            return null;
        }
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (sUriMatcher.match(uri) != OFFER_ID) {
            throw new IllegalArgumentException("Unknown URI for update " + uri);
        }
        String rowIdStr = (String) uri.getPathSegments().get(OFFERS);
        Integer rowId = Integer.valueOf(rowIdStr);
        String[] whereArgs = new String[OFFERS];
        whereArgs[0] = rowIdStr;
        try {
            int affectedRows = getDatabase().delete(Constants.DBTABLE_OFFER, "_id= ?", whereArgs);
            if (affectedRows <= 0) {
                return affectedRows;
            }
            getContext().getContentResolver().notifyChange(ContentUris.withAppendedId(OfferColumns.CONTENT_URI, (long) rowId.intValue()), null);
            return affectedRows;
        } catch (SQLiteException e) {
            Log.w(TAG, "Failed to access database: " + e.getMessage(), e);
            return 0;
        }
    }

    public int update(Uri uri, ContentValues cv, String selection, String[] selectionArgs) {
        if (sUriMatcher.match(uri) != OFFER_ID) {
            throw new IllegalArgumentException("Unknown URI for update " + uri);
        } else if (cv == null) {
            throw new SQLException("Can't insert NULL row");
        } else {
            Integer rowId = Integer.valueOf((String) uri.getPathSegments().get(OFFERS));
            try {
                int affectedRows = getDatabase().update(Constants.DBTABLE_OFFER, cv, "_id=" + rowId, null);
                if (affectedRows <= 0) {
                    return affectedRows;
                }
                getContext().getContentResolver().notifyChange(ContentUris.withAppendedId(OfferColumns.CONTENT_URI, (long) rowId.intValue()), null);
                return affectedRows;
            } catch (SQLiteException e) {
                Log.w(TAG, "Failed to access database: " + e.getMessage(), e);
                return 0;
            }
        }
    }

    public Uri insert(Uri uri, ContentValues cv) {
        int type = sUriMatcher.match(uri);
        if (type != OFFER_ID && type != OFFERS) {
            throw new IllegalArgumentException("Unknown URI for insert " + uri);
        } else if (cv == null) {
            throw new SQLException("Can't insert NULL row");
        } else {
            try {
                long rowId = getDatabase().replace(Constants.DBTABLE_OFFER, "_id", cv);
                if (rowId > 0) {
                    Uri offerUri = ContentUris.withAppendedId(OfferColumns.CONTENT_URI, rowId);
                    getContext().getContentResolver().notifyChange(offerUri, null);
                    return offerUri;
                }
            } catch (SQLiteException e) {
                Log.w(TAG, "Failed to access database: " + e.getMessage(), e);
            }
            throw new SQLException("Failed to insert row into " + uri);
        }
    }
}
