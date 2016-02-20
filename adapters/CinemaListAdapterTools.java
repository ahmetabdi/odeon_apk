package uk.co.odeon.androidapp.adapters;

import android.view.View;
import android.widget.TextView;
import java.util.Locale;
import uk.co.odeon.androidapp.Constants.APP_LOCATION;
import uk.co.odeon.androidapp.R;

public class CinemaListAdapterTools {
    private APP_LOCATION location;
    private CinemaListMode mode;

    public enum CinemaListMode {
        ALPHA,
        ALPHA_WITH_DISTANCE,
        DISTANCE
    }

    public class ViewHolder {
        public TextView addressTextView;
        public TextView cinemaListHeaderText;
        public TextView cinemaNameTextView;
        public TextView distanceTextView;
    }

    public CinemaListAdapterTools(CinemaListMode mode, APP_LOCATION location) {
        this.mode = mode;
        this.location = location;
    }

    public ViewHolder initViewHolder(View v) {
        ViewHolder vh = new ViewHolder();
        vh.cinemaNameTextView = (TextView) v.findViewById(R.id.cinema_list_name);
        vh.distanceTextView = (TextView) v.findViewById(R.id.cinema_list_distance);
        vh.addressTextView = (TextView) v.findViewById(R.id.cinema_list_addr);
        vh.cinemaListHeaderText = (TextView) v.findViewById(R.id.list_row_header_text);
        v.setTag(vh);
        return vh;
    }

    public void bindView(View view, int siteId, String nameText, Float dist, String address, String postCode) {
        ViewHolder vh = (ViewHolder) view.getTag();
        vh.cinemaNameTextView.setText(nameText);
        if ((this.mode == CinemaListMode.ALPHA_WITH_DISTANCE || this.mode == CinemaListMode.DISTANCE) && dist != null) {
            vh.distanceTextView.setText(formatDistanceMiles(dist.floatValue()));
        } else {
            vh.distanceTextView.setText("");
        }
        String fullAddress = "";
        if (address != null) {
            fullAddress = new StringBuilder(String.valueOf(fullAddress)).append(address.replace("\n", ", ")).toString();
        }
        if (postCode != null) {
            StringBuilder stringBuilder = new StringBuilder(String.valueOf(fullAddress));
            if (fullAddress.length() != 0) {
                postCode = ", " + postCode;
            }
            fullAddress = stringBuilder.append(postCode).toString();
        }
        vh.addressTextView.setText(fullAddress);
    }

    private String formatDistanceMiles(float distance) {
        if (APP_LOCATION.ire.equals(this.location)) {
            if (distance < 20.0f) {
                return String.format(Locale.UK, "%.2f km", new Object[]{Float.valueOf(distance)});
            }
            return String.format(Locale.UK, "%d km", new Object[]{Integer.valueOf(Math.round(distance))});
        } else if (distance < 20.0f) {
            return String.format(Locale.UK, "%.2f miles", new Object[]{Float.valueOf(distance)});
        } else {
            return String.format(Locale.UK, "%d miles", new Object[]{Integer.valueOf(Math.round(distance))});
        }
    }
}
