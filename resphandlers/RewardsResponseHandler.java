package uk.co.odeon.androidapp.resphandlers;

import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.json.Rewards;
import uk.co.odeon.androidapp.util.drawable.DrawableManager;
import uk.co.odeon.androidapp.util.http.AbstractTypedJSONResponseHandler;

public class RewardsResponseHandler extends AbstractTypedJSONResponseHandler<Rewards> {
    private static final String TAG;
    protected final SharedPreferences customerDataPrefs;

    public RewardsResponseHandler() {
        this.customerDataPrefs = ODEONApplication.getInstance().getCustomerDataPrefs();
    }

    static {
        TAG = RewardsResponseHandler.class.getSimpleName();
    }

    public boolean handleJSONRepsonse(JSONObject jsonObj, Uri uri) {
        Log.i(TAG, "Received JSONObject: " + jsonObj.toString());
        JSONObject config = jsonObj.optJSONObject("config");
        JSONObject data = jsonObj.optJSONObject("data");
        Log.i(TAG, "Received config: " + config);
        Log.i(TAG, "Received data: " + data);
        if (data != null) {
            String errorText = data.optString("errorText", null);
            if (errorText == null || errorText.length() <= 0) {
                String opcCardNumber = data.optString(Constants.CUSTOMER_PREFS_OPC_CARD, null);
                if (opcCardNumber == null || !opcCardNumber.equals(this.customerDataPrefs.getString(Constants.CUSTOMER_PREFS_OPC_CARD, ""))) {
                    DrawableManager.getInstance().deleteImageCacheFilesByPattern(String.format(Constants.BITMAP_OPC_CARD_BARCODE_FILE_NAME, new Object[]{".*\\"}));
                }
                this.customerDataPrefs.edit().putString(Constants.CUSTOMER_PREFS_OPC_CARD, opcCardNumber).commit();
                JSONObject halfRatings = data.optJSONObject("halfRatings");
                if (halfRatings != null && halfRatings.length() > 0) {
                    saveCustomerHalfRatings(jsonToHashMap(halfRatings));
                }
            }
        }
        if (data != null && data.length() > 0) {
            setResult(new Rewards(data));
        }
        return true;
    }

    private HashMap<String, Integer> jsonToHashMap(JSONObject jsonObject) {
        HashMap<String, Integer> hm = new HashMap();
        try {
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                hm.put(key, Integer.valueOf(jsonObject.getInt(key) / 2));
            }
        } catch (JSONException e) {
            Log.e(TAG, "halfRatings-JSON not parsable");
        }
        return hm;
    }

    private boolean saveCustomerHalfRatings(HashMap<String, Integer> map) {
        boolean done = false;
        for (String filmId : map.keySet()) {
            done = this.customerDataPrefs.edit().putInt("rating_" + filmId, ((Integer) map.get(filmId)).intValue()).commit();
        }
        return done;
    }
}
