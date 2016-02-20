package uk.co.odeon.androidapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.json.RewardsCustomerCardTransaction;
import uk.co.odeon.androidapp.json.RewardsCustomerCardTransactions;

public class RewardsTransactionsListAdapter extends BaseAdapter {
    protected static final String TAG = "RewardsTransactionsListAdapter";
    private Context mContext;
    private RewardsCustomerCardTransactions mRewardsTransactions;

    public RewardsTransactionsListAdapter(Context context, RewardsCustomerCardTransactions rewardsTransactions) {
        this.mContext = context;
        this.mRewardsTransactions = rewardsTransactions;
    }

    public int getCount() {
        return this.mRewardsTransactions.size();
    }

    public Object getItem(int position) {
        return this.mRewardsTransactions.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout itemLayout;
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
        if (convertView == null) {
            itemLayout = (LinearLayout) inflater.inflate(R.layout.rewards_transactions_list_item, null);
        } else {
            itemLayout = (LinearLayout) convertView;
        }
        RewardsCustomerCardTransaction transaction = (RewardsCustomerCardTransaction) this.mRewardsTransactions.get(position);
        ((TextView) itemLayout.findViewById(R.id.rewards_transaction_date_text)).setText(transaction.getDate());
        ((TextView) itemLayout.findViewById(R.id.rewards_transaction_desc_text)).setText(transaction.getDetail());
        ((TextView) itemLayout.findViewById(R.id.rewards_transaction_earned_text)).setText(transaction.getPointsEarned());
        ((TextView) itemLayout.findViewById(R.id.rewards_transaction_redeemed_text)).setText(transaction.getPointsRedeemed());
        ((TextView) itemLayout.findViewById(R.id.rewards_transaction_expiry_text)).setText(transaction.getExpiryDate());
        return itemLayout;
    }
}
