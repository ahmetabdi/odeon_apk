package uk.co.odeon.androidapp.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class OfferContent {
    public static final String AUTHORITY = "uk.co.odeon.androidapp.provider.OfferContent";

    public static final class OfferColumns implements BaseColumns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.uk.co.odeon.androidapp.offer";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.uk.co.odeon.androidapp.offer";
        public static final Uri CONTENT_URI;
        public static final String DEFAULT_SORT_ORDER = "_id ASC";
        public static final String IMAGE_URL = "imageURL";
        public static final String TEXT = "text";
        public static final String TITLE = "title";

        private OfferColumns() {
        }

        static {
            CONTENT_URI = Uri.parse("content://uk.co.odeon.androidapp.provider.OfferContent/offers");
        }
    }

    private OfferContent() {
    }
}
