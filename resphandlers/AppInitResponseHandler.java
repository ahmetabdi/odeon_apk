package uk.co.odeon.androidapp.resphandlers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.provider.DatabaseHelper;
import uk.co.odeon.androidapp.provider.FilmContent.FilmColumns;
import uk.co.odeon.androidapp.provider.FilmContent.FilmDetailsColumns;
import uk.co.odeon.androidapp.provider.FilmContent.FilmInSiteColumns;
import uk.co.odeon.androidapp.provider.OfferContent.OfferColumns;
import uk.co.odeon.androidapp.util.drawable.DrawableManager;
import uk.co.odeon.androidapp.util.http.AbstractJSONResponseHandler;

public class AppInitResponseHandler extends AbstractJSONResponseHandler {
    private static final String TAG;
    private ContentResolver contentResolver;
    public String msgHeader;
    public String msgText;
    private SharedPreferences prefs;
    private SimpleDateFormat sdf;
    private SimpleDateFormat sdfOut;

    static {
        TAG = AppInitResponseHandler.class.getSimpleName();
    }

    public AppInitResponseHandler(ContentResolver contentResolver, SharedPreferences prefs) {
        this.contentResolver = null;
        this.sdf = new SimpleDateFormat("yyyyMMdd", Locale.UK);
        this.sdfOut = new SimpleDateFormat("dd/MM/yy", Locale.UK);
        this.msgHeader = null;
        this.msgText = null;
        this.contentResolver = contentResolver;
        this.prefs = prefs;
    }

    public boolean handleJSONRepsonse(JSONObject jsonObj, Uri uri) {
        Cursor existingFilmsCursor = null;
        JSONObject config = jsonObj.getJSONObject("config");
        String dataHash = config.getString(FilmDetailsColumns.DATA_HASH);
        boolean forceUpdate = config.optString("action", "").equals("update");
        if (config.optString("msgBody", "").trim().length() > 0 && config.optString("msgHeader", "").trim().length() > 0) {
            this.msgHeader = config.getString("msgHeader");
            this.msgText = config.getString("msgBody");
        }
        JSONObject data = jsonObj.optJSONObject("data");
        if (data != null || forceUpdate) {
            if (data == null && forceUpdate) {
                Log.i(TAG, "Empty data in AppInit JSON response, but force update");
                data = new JSONObject();
            }
            JSONArray films = data.optJSONArray("films");
            if (films == null) {
                films = new JSONArray();
            }
            if (films.length() > 0 || forceUpdate) {
                int id;
                existingFilmsCursor = this.contentResolver.query(FilmColumns.CONTENT_URI, new String[]{"_id", FilmColumns.HIDDEN, FilmColumns.FAVOURITE, OfferColumns.IMAGE_URL}, null, null, null);
                HashSet<Integer> filmIdsToBeDeleted = new HashSet();
                SparseArray<File> filmImageFileNames = new SparseArray();
                HashSet<Integer> filmIdsToKeepFavourite = new HashSet();
                if (existingFilmsCursor != null) {
                    int idIndex = existingFilmsCursor.getColumnIndex("_id");
                    int favIndex = existingFilmsCursor.getColumnIndex(FilmColumns.FAVOURITE);
                    int imgIndex = existingFilmsCursor.getColumnIndex(OfferColumns.IMAGE_URL);
                    DrawableManager dm = DrawableManager.getInstance();
                    existingFilmsCursor.moveToFirst();
                    while (!existingFilmsCursor.isAfterLast()) {
                        try {
                            id = existingFilmsCursor.getInt(idIndex);
                            if (existingFilmsCursor.getInt(favIndex) == 1) {
                                filmIdsToKeepFavourite.add(Integer.valueOf(id));
                            }
                            filmIdsToBeDeleted.add(Integer.valueOf(id));
                            filmImageFileNames.put(id, dm.buildImageCacheFileBasedOnURLFilename(existingFilmsCursor.getString(imgIndex)));
                            existingFilmsCursor.moveToNext();
                        } catch (Throwable e) {
                            Log.e(TAG, "Failed to store appinit response in DB", e);
                            this.prefs.edit().remove(FilmDetailsColumns.DATA_HASH).commit();
                            if (!(existingFilmsCursor == null || existingFilmsCursor.isClosed())) {
                                existingFilmsCursor.close();
                            }
                            return false;
                        } catch (Throwable th) {
                            if (!(existingFilmsCursor == null || existingFilmsCursor.isClosed())) {
                                existingFilmsCursor.close();
                            }
                        }
                    }
                }
                Set<ContentValues> cvs = new HashSet();
                Set<ContentValues> cvsSite = new HashSet();
                for (int f = 0; f < films.length(); f++) {
                    JSONObject film = (JSONObject) films.get(f);
                    id = film.getInt("filmMasterId");
                    boolean isFav = filmIdsToKeepFavourite.contains(Integer.valueOf(id));
                    if (isFav) {
                        filmIdsToKeepFavourite.remove(Integer.valueOf(id));
                    }
                    Log.d(TAG, "Converting film " + id);
                    cvs.add(jsonFilmToContentValues(film, id, isFav));
                    cvsSite.addAll(jsonFilmSitesToContentValues(film, id));
                    filmIdsToBeDeleted.remove(Integer.valueOf(id));
                }
                boolean hasNewFilmRows = films.length() > 0;
                SQLiteDatabase wdb = DatabaseHelper.getInstance(ODEONApplication.getInstance()).getWritableDatabase();
                wdb.beginTransaction();
                if (hasNewFilmRows) {
                    Log.i(TAG, "Inserting " + cvs.size() + " new/updated films");
                    wdb.delete(Constants.DBTABLE_FILM_FILMINSITE, null, null);
                    this.contentResolver.bulkInsert(FilmColumns.CONTENT_URI, (ContentValues[]) cvs.toArray(new ContentValues[0]));
                    this.contentResolver.bulkInsert(FilmInSiteColumns.CONTENT_URI, (ContentValues[]) cvsSite.toArray(new ContentValues[0]));
                }
                if (!filmIdsToBeDeleted.isEmpty()) {
                    Iterator it = filmIdsToBeDeleted.iterator();
                    while (it.hasNext()) {
                        deleteFilmInDB((Integer) it.next(), filmIdsToKeepFavourite, filmImageFileNames);
                    }
                }
                wdb.setTransactionSuccessful();
                if (wdb.inTransaction()) {
                    wdb.endTransaction();
                }
                JSONArray offers = data.optJSONArray(Constants.CUSTOMER_PREFS_OFFERS);
                List<ContentValues> cvsOffers = null;
                boolean hasNewOffers = false;
                if (offers == null || offers.length() <= 0) {
                    Log.i(TAG, "No offers/news items found in response");
                } else {
                    cvsOffers = jsonOffersToContentValues(offers);
                    hasNewOffers = true;
                }
                Log.i(TAG, "Deleting offers/news");
                wdb.beginTransaction();
                wdb.delete(Constants.DBTABLE_OFFER, null, null);
                if (cvsOffers != null) {
                    Log.i(TAG, "Inserting " + cvsOffers.size() + " new/updated offers/news");
                    this.contentResolver.bulkInsert(OfferColumns.CONTENT_URI, (ContentValues[]) cvsOffers.toArray(new ContentValues[0]));
                }
                wdb.setTransactionSuccessful();
                wdb.endTransaction();
                boolean hasChanges = hasNewFilmRows || !filmIdsToBeDeleted.isEmpty() || hasNewOffers;
                if (hasChanges) {
                    this.prefs.edit().putString(FilmDetailsColumns.DATA_HASH, dataHash).commit();
                }
                if (existingFilmsCursor == null || existingFilmsCursor.isClosed()) {
                    return hasChanges;
                }
                existingFilmsCursor.close();
                return hasChanges;
            }
            Log.i(TAG, "Empty film list in AppInit JSON response");
            if (!(existingFilmsCursor == null || existingFilmsCursor.isClosed())) {
                existingFilmsCursor.close();
            }
            return false;
        }
        Log.i(TAG, "Empty data in AppInit JSON response");
        if (!(existingFilmsCursor == null || existingFilmsCursor.isClosed())) {
            existingFilmsCursor.close();
        }
        return false;
    }

    protected List<ContentValues> jsonOffersToContentValues(JSONArray offersArr) throws JSONException {
        ArrayList<ContentValues> cvs = new ArrayList();
        for (int i = 0; i < offersArr.length(); i++) {
            cvs.add(jsonOfferToContentValues(offersArr.getJSONObject(i)));
        }
        return cvs;
    }

    private ContentValues jsonOfferToContentValues(JSONObject offer) throws JSONException {
        ContentValues cv = new ContentValues();
        cv.put("_id", Integer.valueOf(offer.getInt("offerId")));
        cv.put(OfferColumns.TITLE, offer.getString("offerTitle"));
        cv.put(OfferColumns.TEXT, offer.optString("offerText"));
        cv.put(OfferColumns.IMAGE_URL, offer.optString("offerImage"));
        return cv;
    }

    protected List<ContentValues> jsonFilmSitesToContentValues(JSONObject film, int id) throws JSONException {
        ArrayList<ContentValues> cvs = new ArrayList();
        if (film.has("sites")) {
            JSONArray sitesArr = film.getJSONArray("sites");
            for (int i = 0; i < sitesArr.length(); i++) {
                ContentValues cvSite = new ContentValues();
                cvSite.put(FilmInSiteColumns.FILM_MASTER_ID, Integer.valueOf(id));
                cvSite.put(FilmInSiteColumns.SITE_ID, Integer.valueOf(sitesArr.getInt(i)));
                cvs.add(cvSite);
            }
        }
        return cvs;
    }

    protected ContentValues jsonFilmToContentValues(JSONObject film, int id, boolean isFavourite) throws JSONException {
        ContentValues cv = new ContentValues();
        cv.put("_id", Integer.valueOf(id));
        cv.put(OfferColumns.TITLE, film.getString(OfferColumns.TITLE));
        cv.put(FilmColumns.CERTIFICATE, film.optString(FilmColumns.CERTIFICATE));
        String imageURL = film.optString("imageUrl");
        if (imageURL != null && (imageURL.equals("null") || imageURL.trim().length() == 0)) {
            imageURL = null;
        }
        cv.put(OfferColumns.IMAGE_URL, imageURL);
        String trailerUrl = film.optString("trailerUrl");
        if (trailerUrl != null && (trailerUrl.equals("null") || trailerUrl.trim().length() == 0)) {
            trailerUrl = null;
        }
        cv.put(FilmColumns.TRAILER_URL, trailerUrl);
        cv.put(FilmColumns.TOP5, Integer.valueOf(film.optInt("topFive")));
        cv.put(FilmColumns.COMINGSOON, Integer.valueOf(film.optInt("comingSoon")));
        cv.put(FilmColumns.FUTURERELEASE, Integer.valueOf(film.optInt("futureRelease")));
        cv.put(FilmColumns.NOWBOOKING, Integer.valueOf(film.optInt(FilmColumns.NOWBOOKING)));
        cv.put(FilmColumns.RECOMMENDED, Integer.valueOf(film.optInt(FilmColumns.RECOMMENDED)));
        cv.put(FilmColumns.HALFRATING, Integer.valueOf(film.optInt(FilmColumns.HALFRATING)));
        cv.put(FilmColumns.RATEABLE, Integer.valueOf(film.getInt("isRateable")));
        cv.put(FilmColumns.GENRE, film.optString(FilmColumns.GENRE));
        cv.put(FilmColumns.FAVOURITE, isFavourite ? Integer.valueOf(1) : Integer.valueOf(0));
        cv.put(FilmColumns.BBF, Integer.valueOf(film.getInt("isBBF")));
        cv.put(FilmColumns.HIDDEN, Integer.valueOf(0));
        String relDate = film.optString("releaseDate");
        String relDateFormatted = relDate;
        if (relDate != null) {
            try {
                relDateFormatted = this.sdfOut.format(this.sdf.parse(relDate));
            } catch (ParseException e) {
                Log.w(TAG, "Failed to parse relDate '" + relDate + "' for film #" + id);
            }
        }
        cv.put(FilmColumns.RELDATE, relDateFormatted);
        cv.put(FilmColumns.RELDATESORT, relDate);
        return cv;
    }

    protected void deleteFilmInDB(Integer filmIdToBeDeleted, HashSet<Integer> filmIdsToKeepFavourite, SparseArray<File> filmImageFileNames) {
        if (filmIdsToKeepFavourite.contains(filmIdToBeDeleted)) {
            Log.i(TAG, "Keeping outdated favourite film, marking as hidden #" + filmIdToBeDeleted);
            ContentValues cv = new ContentValues();
            Uri filmUriToBeHidden = ContentUris.withAppendedId(FilmColumns.CONTENT_URI, (long) filmIdToBeDeleted.intValue());
            cv.put(FilmColumns.HIDDEN, Integer.valueOf(1));
            this.contentResolver.update(filmUriToBeHidden, cv, null, null);
            return;
        }
        Log.i(TAG, "Deleting removed film #" + filmIdToBeDeleted);
        this.contentResolver.delete(ContentUris.withAppendedId(FilmColumns.CONTENT_URI, (long) filmIdToBeDeleted.intValue()), null, null);
        File imageFile = (File) filmImageFileNames.get(filmIdToBeDeleted.intValue());
        if (imageFile != null && imageFile.exists()) {
            imageFile.delete();
        }
    }
}
