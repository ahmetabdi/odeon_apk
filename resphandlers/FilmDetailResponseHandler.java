package uk.co.odeon.androidapp.resphandlers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import uk.co.odeon.androidapp.provider.FilmContent.FilmDetailsColumns;
import uk.co.odeon.androidapp.provider.OfferContent.OfferColumns;
import uk.co.odeon.androidapp.util.http.AbstractJSONResponseHandler;

public class FilmDetailResponseHandler extends AbstractJSONResponseHandler {
    private static final String TAG;
    private ContentResolver contentResolver;
    private Uri filmDetailsUri;

    static {
        TAG = FilmDetailResponseHandler.class.getSimpleName();
    }

    public FilmDetailResponseHandler(ContentResolver contentResolver, Uri filmDetailsUri) {
        this.contentResolver = null;
        this.contentResolver = contentResolver;
        this.filmDetailsUri = filmDetailsUri;
    }

    public boolean handleJSONRepsonse(JSONObject jsonObj, Uri uri) {
        try {
            String dataHash = jsonObj.getJSONObject("config").getString(FilmDetailsColumns.DATA_HASH);
            Integer id = Integer.valueOf((String) this.filmDetailsUri.getPathSegments().get(1));
            JSONObject data = jsonObj.optJSONObject("data");
            ContentValues cv;
            if (data == null || data.length() <= 1) {
                Log.i(TAG, "No new film details for film: " + id);
                cv = new ContentValues();
                cv.put(FilmDetailsColumns.LAST_UPDATE_TS, new Long(System.currentTimeMillis()));
                this.contentResolver.update(this.filmDetailsUri, cv, null, null);
                return false;
            }
            Log.d(TAG, "Converting film details " + id);
            cv = jsonFilmDetailsToContentValues(data, dataHash, id.intValue());
            Log.d(TAG, "Inserting film details " + id);
            this.contentResolver.insert(FilmDetailsColumns.CONTENT_URI, cv);
            return true;
        } catch (JSONException e) {
            Log.e(TAG, "Failed to store films details in DB", e);
            return false;
        }
    }

    protected ContentValues jsonFilmDetailsToContentValues(JSONObject film, String dataHash, int id) throws JSONException {
        ContentValues cv = new ContentValues();
        cv.put("_id", Integer.valueOf(id));
        cv.put(FilmDetailsColumns.DATA_HASH, dataHash);
        cv.put(FilmDetailsColumns.PLOT, film.optString(FilmDetailsColumns.PLOT));
        String imageURL = film.optString("imageUrl");
        if (imageURL != null && imageURL.equals("null")) {
            imageURL = null;
        }
        cv.put(OfferColumns.IMAGE_URL, imageURL);
        cv.put(FilmDetailsColumns.COUNTRY, film.optString(FilmDetailsColumns.COUNTRY));
        cv.put(FilmDetailsColumns.RUNNING_TIME, Integer.valueOf(film.optInt(FilmDetailsColumns.RUNNING_TIME)));
        cv.put(FilmDetailsColumns.BBFC_RATING, film.optString("customerAdvice"));
        cv.put(FilmDetailsColumns.CAST, film.optString("casts"));
        cv.put(FilmDetailsColumns.DIRECTOR, film.optString(FilmDetailsColumns.DIRECTOR));
        cv.put(FilmDetailsColumns.LAST_UPDATE_TS, new Long(System.currentTimeMillis()));
        return cv;
    }
}
