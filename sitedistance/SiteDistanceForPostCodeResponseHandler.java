package uk.co.odeon.androidapp.sitedistance;

import android.location.Location;
import android.net.Uri;
import android.util.Log;
import org.json.JSONObject;
import uk.co.odeon.androidapp.util.http.AbstractTypedJSONResponseHandler;

public class SiteDistanceForPostCodeResponseHandler extends AbstractTypedJSONResponseHandler<SiteDistanceResult> {
    private static final String TAG;
    private String postCode;

    public SiteDistanceForPostCodeResponseHandler(String postCode) {
        this.postCode = postCode;
    }

    static {
        TAG = SiteDistanceForPostCodeResponseHandler.class.getSimpleName();
    }

    public boolean handleJSONRepsonse(JSONObject jsonObj, Uri uri) {
        JSONObject data = jsonObj.optJSONObject("data");
        Log.i(TAG, "Received data: " + data);
        if (data == null || data.length() <= 1) {
            setResult(new SiteDistanceResult(null, this.postCode, true));
        } else {
            double lat = data.optDouble("lat");
            double lng = data.optDouble("lng");
            Log.i(TAG, String.format("Lat %f Lon %f", new Object[]{Double.valueOf(lat), Double.valueOf(lng)}));
            Location loc = new Location("odeonApi");
            loc.setLatitude(lat);
            loc.setLongitude(lng);
            setResult(SiteDistance.getInstance().calculateSiteDistancesForPostCode(this.postCode, loc));
        }
        return true;
    }
}
