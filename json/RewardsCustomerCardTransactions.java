package uk.co.odeon.androidapp.json;

import org.json.JSONArray;
import org.json.JSONObject;

public class RewardsCustomerCardTransactions extends AbstractArray<RewardsCustomerCardTransaction> {
    protected static final String TAG;

    static {
        TAG = RewardsCustomerCardTransactions.class.getSimpleName();
    }

    public RewardsCustomerCardTransactions(JSONArray transactionArray) {
        super(transactionArray);
    }

    public RewardsCustomerCardTransaction createObject(JSONObject object) {
        return new RewardsCustomerCardTransaction(object);
    }
}
