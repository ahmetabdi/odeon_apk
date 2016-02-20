package uk.co.odeon.androidapp.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.provider.FilmContent.FilmInSiteColumns;
import uk.co.odeon.androidapp.provider.SiteContent.SiteColumns;
import uk.co.odeon.androidapp.provider.SiteContent.SiteFavouriteColumns;

public class SiteContentProvider extends ContentProvider {
    private static final int SITES = 1;
    private static final int SITE_FAVS = 3;
    private static final int SITE_FAV_ID = 4;
    private static final int SITE_ID = 2;
    private static final UriMatcher sUriMatcher;
    private DatabaseHelper dbHelper;

    public SiteContentProvider(Context context) {
    }

    static {
        sUriMatcher = new UriMatcher(-1);
        sUriMatcher.addURI(SiteContent.AUTHORITY, "sites", SITES);
        sUriMatcher.addURI(SiteContent.AUTHORITY, "sites/#", SITE_ID);
        sUriMatcher.addURI(SiteContent.AUTHORITY, "sitefavourites", SITE_FAVS);
        sUriMatcher.addURI(SiteContent.AUTHORITY, "sitefavourites/#", SITE_FAV_ID);
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
            case SITES /*1*/:
                return SiteColumns.CONTENT_TYPE;
            case SITE_ID /*2*/:
                return SiteColumns.CONTENT_ITEM_TYPE;
            case SITE_FAVS /*3*/:
                return SiteFavouriteColumns.CONTENT_TYPE;
            case SITE_FAV_ID /*4*/:
                return SiteFavouriteColumns.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    public Cursor query(Uri uri, String[] projectionIn, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String orderBy = null;
        String[] projection = projectionIn;
        switch (sUriMatcher.match(uri)) {
            case SITES /*1*/:
                String fst;
                String ft;
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = SiteColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                String tables = Constants.DBTABLE_SITE;
                if (selection != null) {
                    if (selection.contains(Constants.DBTABLE_FILM_FILMINSITE)) {
                        fst = Constants.DBTABLE_FILM_FILMINSITE;
                        ft = Constants.DBTABLE_SITE;
                        tables = new StringBuilder(String.valueOf(tables)).append(" LEFT JOIN  ").append(Constants.DBTABLE_FILM_FILMINSITE).append(" ON (").append(Constants.DBTABLE_SITE).append("._ID=").append(Constants.DBTABLE_FILM_FILMINSITE).append(".").append(FilmInSiteColumns.SITE_ID).append(")").toString();
                    }
                }
                fst = Constants.DBTABLE_SITE_FAVOURITE;
                ft = Constants.DBTABLE_SITE;
                tables = new StringBuilder(String.valueOf(tables)).append(" LEFT JOIN  ").append(Constants.DBTABLE_SITE_FAVOURITE).append(" ON (").append(Constants.DBTABLE_SITE).append("._ID=").append(Constants.DBTABLE_SITE_FAVOURITE).append(".").append("_id").append(")").toString();
                if (projection == null) {
                    projection = new String[SITE_ID];
                    projection[0] = "Site.*";
                    projection[SITES] = "SiteFavourite._ID AS favID";
                }
                qb.setTables(tables);
                break;
            case SITE_ID /*2*/:
                String itables = Constants.DBTABLE_SITE;
                qb.appendWhere("Site._id=" + ((String) uri.getPathSegments().get(SITES)));
                String ifst = Constants.DBTABLE_SITE_FAVOURITE;
                String ift = Constants.DBTABLE_SITE;
                itables = new StringBuilder(String.valueOf(itables)).append(" LEFT JOIN  ").append(Constants.DBTABLE_SITE_FAVOURITE).append(" ON (").append(Constants.DBTABLE_SITE).append("._ID=").append(Constants.DBTABLE_SITE_FAVOURITE).append(".").append("_id").append(")").toString();
                if (projection == null) {
                    projection = new String[SITE_ID];
                    projection[0] = "Site.*";
                    projection[SITES] = "SiteFavourite._ID AS favID";
                }
                qb.setTables(itables);
                break;
            case SITE_FAVS /*3*/:
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = SiteFavouriteColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                qb.setTables(Constants.DBTABLE_SITE_FAVOURITE);
                break;
            case SITE_FAV_ID /*4*/:
                qb.setTables(Constants.DBTABLE_SITE_FAVOURITE);
                qb.appendWhere("_id=" + ((String) uri.getPathSegments().get(SITES)));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Cursor c = qb.query(getDatabase(), projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table;
        String where;
        Uri contentUri;
        SQLiteDatabase db = getDatabase();
        switch (sUriMatcher.match(uri)) {
            case SITE_ID /*2*/:
                table = Constants.DBTABLE_SITE;
                where = "_id= ?";
                contentUri = SiteColumns.CONTENT_URI;
                break;
            case SITE_FAV_ID /*4*/:
                table = Constants.DBTABLE_SITE_FAVOURITE;
                where = "_id= ?";
                contentUri = SiteFavouriteColumns.CONTENT_URI;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI for update " + uri);
        }
        String rowIdStr = (String) uri.getPathSegments().get(SITES);
        Integer rowId = Integer.valueOf(rowIdStr);
        String[] whereArgs = new String[SITES];
        whereArgs[0] = rowIdStr;
        int affectedRows = db.delete(table, where, whereArgs);
        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(ContentUris.withAppendedId(contentUri, (long) rowId.intValue()), null);
        }
        return affectedRows;
    }

    public int update(Uri uri, ContentValues cv, String selection, String[] selectionArgs) {
        String table;
        String where;
        Uri contentUri;
        SQLiteDatabase db = getDatabase();
        switch (sUriMatcher.match(uri)) {
            case SITE_ID /*2*/:
                table = Constants.DBTABLE_SITE;
                where = "_id= ?";
                contentUri = SiteColumns.CONTENT_URI;
                break;
            case SITE_FAV_ID /*4*/:
                table = Constants.DBTABLE_SITE_FAVOURITE;
                where = "_id= ?";
                contentUri = SiteFavouriteColumns.CONTENT_URI;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI for update " + uri);
        }
        if (cv == null) {
            throw new SQLException("Can't insert NULL row");
        }
        String rowIdStr = (String) uri.getPathSegments().get(SITES);
        Integer rowId = Integer.valueOf(rowIdStr);
        String[] whereArgs = new String[SITES];
        whereArgs[0] = rowIdStr;
        int affectedRows = db.update(table, cv, where, whereArgs);
        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(ContentUris.withAppendedId(contentUri, (long) rowId.intValue()), null);
        }
        return affectedRows;
    }

    public Uri insert(Uri uri, ContentValues cv) {
        String table;
        Uri contentUri;
        String col;
        SQLiteDatabase db = getDatabase();
        switch (sUriMatcher.match(uri)) {
            case SITES /*1*/:
            case SITE_ID /*2*/:
                table = Constants.DBTABLE_SITE;
                contentUri = SiteColumns.CONTENT_URI;
                col = "_id";
                break;
            case SITE_FAVS /*3*/:
            case SITE_FAV_ID /*4*/:
                table = Constants.DBTABLE_SITE_FAVOURITE;
                contentUri = SiteFavouriteColumns.CONTENT_URI;
                col = "_id";
                break;
            default:
                throw new IllegalArgumentException("Unknown URI for update " + uri);
        }
        if (cv == null) {
            throw new SQLException("Can't insert NULL row");
        }
        long rowId = db.replace(table, col, cv);
        if (rowId > 0) {
            Uri siteUri = ContentUris.withAppendedId(contentUri, rowId);
            getContext().getContentResolver().notifyChange(siteUri, null);
            return siteUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }
}
