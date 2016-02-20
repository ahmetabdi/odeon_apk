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
import uk.co.odeon.androidapp.provider.FilmContent.FilmColumns;
import uk.co.odeon.androidapp.provider.FilmContent.FilmDetailsColumns;
import uk.co.odeon.androidapp.provider.FilmContent.FilmInSiteColumns;

public class FilmContentProvider extends ContentProvider {
    private static final int FILMS = 1;
    private static final int FILM_DETAILS = 3;
    private static final int FILM_DETAILS_ID = 4;
    private static final int FILM_ID = 2;
    private static final int FILM_SITE = 5;
    private static final int FILM_SITE_FILMID = 6;
    private static final UriMatcher sUriMatcher;
    private DatabaseHelper dbHelper;

    public FilmContentProvider(Context context) {
    }

    static {
        sUriMatcher = new UriMatcher(-1);
        sUriMatcher.addURI(FilmContent.AUTHORITY, "films", FILMS);
        sUriMatcher.addURI(FilmContent.AUTHORITY, "films/#", FILM_ID);
        sUriMatcher.addURI(FilmContent.AUTHORITY, "filmdetails", FILM_DETAILS);
        sUriMatcher.addURI(FilmContent.AUTHORITY, "filmdetails/#", FILM_DETAILS_ID);
        sUriMatcher.addURI(FilmContent.AUTHORITY, "filmsite", FILM_SITE);
        sUriMatcher.addURI(FilmContent.AUTHORITY, "filmsite/film/#", FILM_SITE_FILMID);
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
            case FILMS /*1*/:
                return FilmColumns.CONTENT_TYPE;
            case FILM_ID /*2*/:
                return FilmColumns.CONTENT_ITEM_TYPE;
            case FILM_DETAILS /*3*/:
                return FilmDetailsColumns.CONTENT_TYPE;
            case FILM_DETAILS_ID /*4*/:
                return FilmDetailsColumns.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String orderBy = null;
        switch (sUriMatcher.match(uri)) {
            case FILMS /*1*/:
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = FilmColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                String tables = Constants.DBTABLE_FILM;
                if (selection != null && selection.contains(Constants.DBTABLE_FILM_FILMINSITE)) {
                    String fst = Constants.DBTABLE_FILM_FILMINSITE;
                    String ft = Constants.DBTABLE_FILM;
                    tables = new StringBuilder(String.valueOf(tables)).append(" LEFT JOIN  ").append(Constants.DBTABLE_FILM_FILMINSITE).append(" ON (").append(Constants.DBTABLE_FILM).append("._ID=").append(Constants.DBTABLE_FILM_FILMINSITE).append(".").append(FilmInSiteColumns.FILM_MASTER_ID).append(")").toString();
                }
                qb.setTables(tables);
                break;
            case FILM_ID /*2*/:
                qb.setTables(Constants.DBTABLE_FILM);
                qb.appendWhere("_id=" + ((String) uri.getPathSegments().get(FILMS)));
                break;
            case FILM_DETAILS /*3*/:
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = FilmColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                qb.setTables(Constants.DBTABLE_FILM_DETAILS);
                break;
            case FILM_DETAILS_ID /*4*/:
                qb.setTables(Constants.DBTABLE_FILM_DETAILS);
                qb.appendWhere("_id=" + ((String) uri.getPathSegments().get(FILMS)));
                break;
            case FILM_SITE /*5*/:
                qb.setTables(Constants.DBTABLE_FILM_FILMINSITE);
                break;
            case FILM_SITE_FILMID /*6*/:
                qb.setTables(Constants.DBTABLE_FILM_FILMINSITE);
                qb.appendWhere("filmMasterID=" + ((String) uri.getPathSegments().get(FILM_ID)));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Cursor c = qb.query(getDatabase(), projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName;
        Uri contentBaseUri;
        SQLiteDatabase db = getDatabase();
        if (sUriMatcher.match(uri) == FILM_ID) {
            tableName = Constants.DBTABLE_FILM;
            contentBaseUri = FilmColumns.CONTENT_URI;
        } else if (sUriMatcher.match(uri) == FILM_DETAILS_ID) {
            tableName = Constants.DBTABLE_FILM_DETAILS;
            contentBaseUri = FilmDetailsColumns.CONTENT_URI;
        } else if (sUriMatcher.match(uri) == FILM_SITE) {
            tableName = Constants.DBTABLE_FILM_FILMINSITE;
            contentBaseUri = FilmInSiteColumns.CONTENT_URI;
        } else {
            throw new IllegalArgumentException("Unknown URI for update " + uri);
        }
        String rowIdStr = (String) uri.getPathSegments().get(FILMS);
        Integer rowId = Integer.valueOf(rowIdStr);
        String[] whereArgs = new String[FILMS];
        whereArgs[0] = rowIdStr;
        int affectedRows = db.delete(tableName, "_id= ?", whereArgs);
        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(ContentUris.withAppendedId(contentBaseUri, (long) rowId.intValue()), null);
        }
        return affectedRows;
    }

    public int update(Uri uri, ContentValues cv, String selection, String[] selectionArgs) {
        String tableName;
        Uri contentBaseUri;
        SQLiteDatabase db = getDatabase();
        if (sUriMatcher.match(uri) == FILM_ID) {
            tableName = Constants.DBTABLE_FILM;
            contentBaseUri = FilmColumns.CONTENT_URI;
        } else if (sUriMatcher.match(uri) == FILM_DETAILS_ID) {
            tableName = Constants.DBTABLE_FILM_DETAILS;
            contentBaseUri = FilmDetailsColumns.CONTENT_URI;
        } else if (sUriMatcher.match(uri) == FILM_SITE) {
            tableName = Constants.DBTABLE_FILM_FILMINSITE;
            contentBaseUri = FilmInSiteColumns.CONTENT_URI;
        } else {
            throw new IllegalArgumentException("Unknown URI for update " + uri);
        }
        if (cv == null) {
            throw new SQLException("Can't insert NULL row");
        }
        Integer rowId = Integer.valueOf((String) uri.getPathSegments().get(FILMS));
        int affectedRows = db.update(tableName, cv, "_id=" + rowId, null);
        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(ContentUris.withAppendedId(contentBaseUri, (long) rowId.intValue()), null);
        }
        return affectedRows;
    }

    public Uri insert(Uri uri, ContentValues cv) {
        String tableName;
        Uri baseContentUri;
        SQLiteDatabase db = getDatabase();
        switch (sUriMatcher.match(uri)) {
            case FILMS /*1*/:
            case FILM_ID /*2*/:
                tableName = Constants.DBTABLE_FILM;
                baseContentUri = FilmColumns.CONTENT_URI;
                break;
            case FILM_DETAILS /*3*/:
            case FILM_DETAILS_ID /*4*/:
                tableName = Constants.DBTABLE_FILM_DETAILS;
                baseContentUri = FilmDetailsColumns.CONTENT_URI;
                break;
            case FILM_SITE /*5*/:
                tableName = Constants.DBTABLE_FILM_FILMINSITE;
                baseContentUri = FilmInSiteColumns.CONTENT_URI;
                break;
            default:
                throw new IllegalArgumentException("Can't insert, uri not supported: " + uri);
        }
        if (cv == null) {
            throw new SQLException("Can't insert NULL row");
        }
        long rowId = db.replace(tableName, "_id", cv);
        if (rowId > 0) {
            Uri filmUri = ContentUris.withAppendedId(baseContentUri, rowId);
            getContext().getContentResolver().notifyChange(filmUri, null);
            return filmUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }
}
