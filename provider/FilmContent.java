package uk.co.odeon.androidapp.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class FilmContent {
    public static final String AUTHORITY = "uk.co.odeon.androidapp.provider.FilmContent";

    public static final class FilmColumns implements BaseColumns {
        public static final String BBF = "bbf";
        public static final String CERTIFICATE = "certificate";
        public static final String COMINGSOON = "comingsoon";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.uk.co.odeon.androidapp.film";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.uk.co.odeon.androidapp.film";
        public static final Uri CONTENT_URI;
        public static final String DEFAULT_SORT_ORDER = "title ASC";
        public static final String FAVOURITE = "favourite";
        public static final String FUTURERELEASE = "futurerelease";
        public static final String GENRE = "genre";
        public static final String HALFRATING = "halfRating";
        public static final String HIDDEN = "hidden";
        public static final String IMAGE_URL = "imageURL";
        public static final String NOWBOOKING = "nowBooking";
        public static final String RATEABLE = "rateable";
        public static final String RECOMMENDED = "recommended";
        public static final String RELDATE = "relDate";
        public static final String RELDATESORT = "relDateSort";
        public static final String TITLE = "title";
        public static final String TOP5 = "top5";
        public static final String TRAILER_URL = "trailerURL";

        private FilmColumns() {
        }

        static {
            CONTENT_URI = Uri.parse("content://uk.co.odeon.androidapp.provider.FilmContent/films");
        }
    }

    public static final class FilmDetailsColumns implements BaseColumns {
        public static final String BBFC_RATING = "bbfcRating";
        public static final String CAST = "cast";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.uk.co.odeon.androidapp.filmdetails";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.uk.co.odeon.androidapp.filmdetails";
        public static final Uri CONTENT_URI;
        public static final String COUNTRY = "country";
        public static final String DATA_HASH = "dataHash";
        public static final String DEFAULT_SORT_ORDER = "_id ASC";
        public static final String DIRECTOR = "director";
        public static final String FILM_ATTRIBUTE = "filmAttribute";
        public static final String IMAGE_URL = "imageURL";
        public static final String LANGUAGE = "language";
        public static final String LAST_UPDATE_TS = "lastUpdateTS";
        public static final String PLOT = "plot";
        public static final String RUNNING_TIME = "runningTime";

        private FilmDetailsColumns() {
        }

        static {
            CONTENT_URI = Uri.parse("content://uk.co.odeon.androidapp.provider.FilmContent/filmdetails");
        }
    }

    public static final class FilmInSiteColumns implements BaseColumns {
        public static final Uri CONTENT_URI;
        public static final String FILM_MASTER_ID = "filmMasterID";
        public static final String SITE_ID = "siteID";

        private FilmInSiteColumns() {
        }

        static {
            CONTENT_URI = Uri.parse("content://uk.co.odeon.androidapp.provider.FilmContent/filmsite");
        }
    }

    private FilmContent() {
    }
}
