package uk.co.odeon.androidapp.custom;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.provider.FilmContent.FilmColumns;
import uk.co.odeon.androidapp.provider.OfferContent.OfferColumns;

public class FilmHeaderView extends RelativeLayout {
    public FilmHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FilmHeaderView(Context context) {
        super(context);
        init();
    }

    private void init() {
        ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.film_header_row, this);
        setBackgroundColor(-16777216);
        setGravity(16);
        setPadding((int) getResources().getDimension(R.dimen.padding_large), (int) getResources().getDimension(R.dimen.padding_small), (int) getResources().getDimension(R.dimen.padding_large), (int) getResources().getDimension(R.dimen.padding_small));
    }

    public void setDataInView(Activity activity, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            String title = cursor.getString(cursor.getColumnIndex(OfferColumns.TITLE));
            TextView titleView = (TextView) activity.findViewById(R.id.filmHeaderTitle);
            if (titleView != null) {
                titleView.setText(title);
            }
            ((ImageView) activity.findViewById(R.id.filmHeaderCertificate)).setImageResource(ODEONApplication.getInstance().certStringToImageResource(cursor.getString(cursor.getColumnIndex(FilmColumns.CERTIFICATE))));
            RelativeLayout layout = (RelativeLayout) activity.findViewById(R.id.filmHeaderLayout);
            if (layout != null) {
                layout.setVisibility(0);
            }
        }
    }
}
