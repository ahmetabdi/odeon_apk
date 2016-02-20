package uk.co.odeon.androidapp.json;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Rewards {
    private static final String FIELD_CUSTOMERCARDEXPIRYDATE = "customerCardPointsExpirePer";
    private static final String FIELD_CUSTOMERCARDEXPIRYPOINTS = "customerCardPointsToExpire";
    private static final String FIELD_CUSTOMERCARDPOINTSBALANCE = "customerCardPointsBalance";
    private static final String FIELD_CUSTOMERCARDPOINTSBALANCECHANGED = "customerCardPointsBalanceChanged";
    private static final String FIELD_CUSTOMERCARDTRANSACTIONS = "customerCardTransactions";
    private static final String FIELD_CUSTOMERFIRSTNAME = "customerFirstName";
    private static final String FIELD_CUSTOMERID = "customerId";
    private static final String FIELD_FILMSBOOKEDANDNOTRATED = "filmsBookedAndNotRated";
    private static final String FIELD_FILMSBOOKEDANDRATED = "filmsBookedAndRated";
    private static final String FIELD_HALFRATINGS = "halfRatings";
    private static final String FIELD_OTHERRATEDFILMS = "otherRatedFilms";
    private static final String TAG;
    public List<Integer> filmIdsBookedAndNotRated;
    public List<Integer> filmIdsBookedAndRated;
    public List<Integer> filmIdsOtherFilmsRated;
    private final JSONObject jsonObject;
    public final RewardsCustomerCardTransactions transactions;

    static {
        TAG = Rewards.class.getSimpleName();
    }

    public Rewards(JSONObject jsonObject) {
        this.filmIdsBookedAndRated = new ArrayList();
        this.filmIdsBookedAndNotRated = new ArrayList();
        this.filmIdsOtherFilmsRated = new ArrayList();
        this.jsonObject = jsonObject;
        if (hasError()) {
            this.transactions = null;
            return;
        }
        this.transactions = new RewardsCustomerCardTransactions(getRawTransactions());
        this.filmIdsBookedAndNotRated = jsonArrayToIntegerList(jsonObject.optJSONArray(FIELD_FILMSBOOKEDANDNOTRATED));
        this.filmIdsBookedAndRated = jsonArrayToIntegerList(jsonObject.optJSONArray(FIELD_FILMSBOOKEDANDRATED));
        this.filmIdsOtherFilmsRated = jsonArrayToIntegerList(jsonObject.optJSONArray(FIELD_OTHERRATEDFILMS));
    }

    public String getCustomerId() {
        return this.jsonObject.optString(FIELD_CUSTOMERID);
    }

    public String getCustomerFirstName() {
        return this.jsonObject.optString(FIELD_CUSTOMERFIRSTNAME);
    }

    public String getCustomerCardPointsBalanceChanged() {
        return this.jsonObject.optString(FIELD_CUSTOMERCARDPOINTSBALANCECHANGED);
    }

    public String getCustomerCardPointsBalance() {
        return this.jsonObject.optString(FIELD_CUSTOMERCARDPOINTSBALANCE);
    }

    public String getCustomerCardExpiryDate() {
        return this.jsonObject.optString(FIELD_CUSTOMERCARDEXPIRYDATE);
    }

    public String getCustomerCardExpiryPoints() {
        return this.jsonObject.optString(FIELD_CUSTOMERCARDEXPIRYPOINTS);
    }

    public JSONObject getHalfRatings() {
        return this.jsonObject.optJSONObject(FIELD_HALFRATINGS);
    }

    public List<Integer> getFilmsBookedAndRated() {
        return this.filmIdsBookedAndRated;
    }

    public List<Integer> getFilmsBookedAndNotRated() {
        return this.filmIdsBookedAndNotRated;
    }

    public List<Integer> getOtherRatedFilms() {
        return this.filmIdsOtherFilmsRated;
    }

    public boolean hasError() {
        return this.jsonObject.optString("errorText", null) != null;
    }

    public String getError() {
        return this.jsonObject.optString("errorText", "");
    }

    private List<Integer> jsonArrayToIntegerList(JSONArray ja) {
        ArrayList<Integer> ints = new ArrayList();
        for (int i = 0; i < ja.length(); i++) {
            try {
                ints.add(Integer.valueOf(ja.getInt(i)));
            } catch (JSONException e) {
                Log.w(TAG, "Invalid value in JSONArray, supposed to be integer at index #" + i + ": " + e.getMessage(), e);
            }
        }
        return ints;
    }

    private JSONArray getRawTransactions() {
        return this.jsonObject.optJSONArray(FIELD_CUSTOMERCARDTRANSACTIONS);
    }
}
