package uk.co.odeon.androidapp.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.util.drawable.DrawableManager;

public class OfferListAdapterTools {

    public class ViewHolder {
        public TextView descriptionTextView;
        public TextView offerListHeaderText;
        public ImageView posterImageView;
        public TextView titleTextView;
    }

    public ViewHolder initViewHolder(View v) {
        ViewHolder vh = new ViewHolder();
        vh.titleTextView = (TextView) v.findViewById(R.id.offer_list_title);
        vh.descriptionTextView = (TextView) v.findViewById(R.id.offer_list_description);
        vh.posterImageView = (ImageView) v.findViewById(R.id.offer_list_image);
        vh.offerListHeaderText = (TextView) v.findViewById(R.id.list_row_header_text);
        v.setTag(vh);
        return vh;
    }

    public void bindView(View view, String title, String description, String imageURL) {
        ViewHolder vh = (ViewHolder) view.getTag();
        vh.titleTextView.setText(title);
        vh.descriptionTextView.setText(description);
        DrawableManager dm = DrawableManager.getInstance();
        dm.loadDrawable(imageURL, vh.posterImageView, dm.buildImageCacheFileBasedOnURLFilename(imageURL), R.drawable.film_info_noimg, R.drawable.film_info_noimg);
    }
}
