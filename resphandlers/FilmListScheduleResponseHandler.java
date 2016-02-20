package uk.co.odeon.androidapp.resphandlers;

import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import org.json.JSONObject;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.json.FilterFilms;
import uk.co.odeon.androidapp.provider.FilmContent.FilmDetailsColumns;
import uk.co.odeon.androidapp.util.http.AbstractTypedJSONResponseHandler;

public class FilmListScheduleResponseHandler extends AbstractTypedJSONResponseHandler<FilterFilms> {
    private static final String TAG;
    public String siteId;

    static {
        TAG = FilmListScheduleResponseHandler.class.getSimpleName();
    }

    public FilmListScheduleResponseHandler(String siteId) {
        this.siteId = null;
        this.siteId = siteId;
    }

    public boolean handleJSONRepsonse(JSONObject jsonObj, Uri uri) {
        SharedPreferences prefs = ODEONApplication.getInstance().getPrefs();
        JSONObject config = jsonObj.optJSONObject("config");
        JSONObject dataObject = jsonObj.optJSONObject("data");
        Log.i(TAG, "Received config: " + config);
        Log.i(TAG, "Received data: " + dataObject);
        if (dataObject != null) {
            String errorText = dataObject.optString("errorText", null);
            if (errorText == null || errorText.length() <= 0) {
                setResult(new FilterFilms(dataObject.optJSONObject("performancesInTimeFrameByFilm"), dataObject.optJSONObject("accessibleInTimeFrameByFilm")));
            } else {
                setResult(new FilterFilms(errorText));
                ODEONApplication.getInstance().removeSiteSpecificDatahashFromPreferences(this.siteId, Constants.PREFS_DATAHASH_ADD_SCHEDULE_INFO, Constants.PREFS_DATAHASH_ADD_SCHEDULE_INFO_SITES);
            }
        }
        String dataHash = config.optString(FilmDetailsColumns.DATA_HASH);
        if (dataHash != null) {
            addDatahashToPreferences(prefs, dataHash);
        }
        return true;
    }

    private void addDatahashToPreferences(SharedPreferences prefs, String dataHash) {
        prefs.edit().putString(new StringBuilder(Constants.PREFS_DATAHASH_ADD_SCHEDULE_INFO).append(this.siteId).toString(), dataHash).commit();
        String sitesWithDataHash = prefs.getString(Constants.PREFS_DATAHASH_ADD_SCHEDULE_INFO_SITES, null);
        if (sitesWithDataHash == null) {
            prefs.edit().putString(Constants.PREFS_DATAHASH_ADD_SCHEDULE_INFO_SITES, this.siteId).commit();
            return;
        }
        String[] sitesWithDataHashArray = sitesWithDataHash.split(",");
        int length = sitesWithDataHashArray.length;
        int i = 0;
        while (i < length) {
            if (!sitesWithDataHashArray[i].equals(this.siteId)) {
                i++;
            } else {
                return;
            }
        }
        prefs.edit().putString(Constants.PREFS_DATAHASH_ADD_SCHEDULE_INFO_SITES, new StringBuilder(String.valueOf(sitesWithDataHash)).append(",").append(this.siteId).toString()).commit();
    }
}
