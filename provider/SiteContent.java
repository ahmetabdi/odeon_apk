package uk.co.odeon.androidapp.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class SiteContent {
    public static final String AUTHORITY = "uk.co.odeon.androidapp.provider.SiteContent";

    public static final class SiteColumns implements BaseColumns {
        public static final String ADDR = "siteAddress1";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.uk.co.odeon.androidapp.site";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.uk.co.odeon.androidapp.site";
        public static final Uri CONTENT_URI;
        public static final String DEFAULT_SORT_ORDER = "name ASC";
        public static final String DISTANCE_FROM_GPS = "distanceFromGPS";
        public static final String DISTANCE_FROM_POSTCODE = "distanceFromPostcode";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String NAME = "name";
        public static final String PHONE = "phone";
        public static final String POSTCODE = "postCode";

        private SiteColumns() {
        }

        static {
            CONTENT_URI = Uri.parse("content://uk.co.odeon.androidapp.provider.SiteContent/sites");
        }
    }

    public static final class SiteFavouriteColumns implements BaseColumns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.uk.co.odeon.androidapp.site.favourite";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.uk.co.odeon.androidapp.site.favourite";
        public static final Uri CONTENT_URI;
        public static final String DEFAULT_SORT_ORDER = "_id ASC";

        private SiteFavouriteColumns() {
        }

        static {
            CONTENT_URI = Uri.parse("content://uk.co.odeon.androidapp.provider.SiteContent/sitefavourites");
        }
    }

    private SiteContent() {
    }
}
