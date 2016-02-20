package uk.co.odeon.androidapp.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "data.db";
    public static final int DATABASE_VERSION = 58;
    public static DatabaseHelper INSTANCE;
    public static final String TAG;

    static {
        INSTANCE = null;
        TAG = DatabaseHelper.class.getSimpleName();
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseHelper(context);
        }
        return INSTANCE;
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTables(sqLiteDatabase);
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldv, int newv) {
        createTables(sqLiteDatabase);
    }

    private void createTables(SQLiteDatabase sqLiteDatabase) {
        Log.i(TAG, "Dropping old tables (if available), except site favourites table");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Film;");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS FilmDetails;");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS FilmInSite;");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Site;");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Offer;");
        Log.i(TAG, "Creating Table (if not already exists) Film");
        sqLiteDatabase.execSQL("CREATE TABLE Film (_id INTEGER PRIMARY KEY, title TEXT, certificate TEXT, imageURL TEXT, trailerURL TEXT, top5 INTEGER, nowBooking INTEGER, comingsoon INTEGER, futurerelease INTEGER, recommended INTEGER, halfRating INTEGER, rateable INTEGER, genre TEXT, relDate TEXT, relDateSort INTEGER, favourite INTEGER, bbf INTEGER, hidden INTEGER );");
        Log.i(TAG, "Creating Table (if not already exists) FilmDetails");
        sqLiteDatabase.execSQL("CREATE TABLE FilmDetails (_id INTEGER PRIMARY KEY, plot TEXT, language TEXT, imageURL TEXT, country TEXT, runningTime INTEGER, bbfcRating TEXT, cast TEXT, director TEXT, dataHash TEXT, lastUpdateTS INTEGER );");
        Log.i(TAG, "Creating Table (if not already exists) FilmInSite");
        sqLiteDatabase.execSQL("CREATE TABLE FilmInSite (siteID INTEGER, filmMasterID INTEGER,  PRIMARY KEY (siteID, filmMasterID));");
        Log.i(TAG, "Creating Table (if not already exists) Site");
        sqLiteDatabase.execSQL("CREATE TABLE Site (_id INTEGER PRIMARY KEY, name TEXT, siteAddress1 TEXT, postCode TEXT, phone TEXT, longitude TEXT, latitude TEXT, distanceFromGPS REAL, distanceFromPostcode REAL);");
        Log.i(TAG, "Creating Table (if not already exists) Offer");
        sqLiteDatabase.execSQL("CREATE TABLE Offer (_id INTEGER PRIMARY KEY, title TEXT, text TEXT, imageURL TEXT );");
        Log.i(TAG, "Creating Table (if not already exists) SiteFavourite");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS SiteFavourite (_id INTEGER PRIMARY KEY );");
    }
}
