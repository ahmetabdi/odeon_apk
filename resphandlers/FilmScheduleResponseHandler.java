package uk.co.odeon.androidapp.resphandlers;

import android.net.Uri;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import uk.co.odeon.androidapp.json.ScheduleDates;
import uk.co.odeon.androidapp.util.http.AbstractTypedJSONResponseHandler;

public class FilmScheduleResponseHandler extends AbstractTypedJSONResponseHandler<ScheduleDates> {
    private static final String TAG;

    static {
        TAG = FilmScheduleResponseHandler.class.getSimpleName();
    }

    public boolean handleJSONRepsonse(JSONObject jsonObj, Uri uri) {
        JSONObject config = jsonObj.optJSONObject("config");
        JSONArray dataArray = jsonObj.optJSONArray("data");
        JSONObject dataObject = jsonObj.optJSONObject("data");
        Log.i(TAG, "Received config: " + config);
        if (dataArray != null) {
            Log.i(TAG, "Received data: " + dataArray);
        }
        if (dataObject != null) {
            Log.i(TAG, "Received data: " + dataObject);
        }
        if (dataArray != null && dataArray.length() > 0) {
            setResult(new ScheduleDates(dataArray));
        }
        if (dataObject != null) {
            setResult(new ScheduleDates(dataObject.optString("errorText")));
        }
        return true;
    }
}
