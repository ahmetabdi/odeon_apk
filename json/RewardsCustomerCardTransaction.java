package uk.co.odeon.androidapp.json;

import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.json.JSONObject;

public class RewardsCustomerCardTransaction {
    private static final String FIELD_DATE = "CRMCustomerTransactionDate";
    private static final String FIELD_DETAIL = "CRMCustomerTransactionDetail";
    private static final String FIELD_POINTSEARNED = "CRMCustomerTransactionPointsEarned";
    private static final String FIELD_POINTSREDEEMED = "CRMCustomerTransactionPointsRedeemed";
    private static final String FIELD_SORT = "CRMCustomerTransactionSort";
    protected static final String TAG;
    private final JSONObject jsonObject;

    static {
        TAG = RewardsCustomerCardTransaction.class.getSimpleName();
    }

    public RewardsCustomerCardTransaction(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getSort() {
        return this.jsonObject.optString(FIELD_SORT);
    }

    public String getPointsEarned() {
        return this.jsonObject.optString(FIELD_POINTSEARNED);
    }

    public String getPointsRedeemed() {
        return this.jsonObject.optString(FIELD_POINTSREDEEMED);
    }

    public String getDetail() {
        return this.jsonObject.optString(FIELD_DETAIL);
    }

    public String getDate() {
        return this.jsonObject.optString(FIELD_DATE);
    }

    public String getExpiryDate() {
        String dateString = getDate();
        if (Integer.parseInt(getPointsEarned()) <= 0 || dateString == null || dateString.length() <= 0) {
            return "-";
        }
        SimpleDateFormat sdfInput = new SimpleDateFormat("dd/MM/yy", Locale.UK);
        SimpleDateFormat sdfOutput = new SimpleDateFormat("MMM yyyy", Locale.UK);
        GregorianCalendar expiryDate = new GregorianCalendar();
        try {
            expiryDate.setTime(sdfInput.parse(dateString));
            expiryDate.add(1, 2);
            return sdfOutput.format(expiryDate.getTime());
        } catch (ParseException e) {
            Log.w(TAG, "Couldn't parse expiry date from rewards transaction: " + e.toString(), e);
            return "";
        }
    }
}
