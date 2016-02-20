package uk.co.odeon.androidapp;

public final class Constants {
    public static final String ACTION_APPINIT_STATUS = "appinit_status";
    public static final String ACTION_CINEMALIST_AUTOPICK = "autoPick";
    public static final String ACTION_CINEMALIST_AZ = "listAZ";
    public static final String ACTION_CINEMALIST_CLOSETOME = "closeToMe";
    public static final String ACTION_END_OF_BOOKING = "endOfBooking";
    public static final String ACTION_END_OF_OPC_JOIN = "endOfOPCJoin";
    public static final String ACTION_FILMDETAIL_STATUS = "filmdetail_status";
    public static final String ACTION_FILMIMGDOWNLOAD_STATUS = "filmdownload_status";
    public static final String ACTION_RESTART_BOOKING = "restartBooking";
    public static final String ACTION_RESTART_OPC_JOIN = "restartOPCJoin";
    public static final String ACTION_REWARDS = "rewards";
    public static final String ACTION_SITEDIST_STATUS = "sitedist_status";
    public static final String API_BASEURL = "%s/android-2.1/api/";
    private static final String API_HOSTURL = "https://api.odeon.co.uk";
    private static final String API_HOSTURL_IRE = "https://api.odeoncinemas.ie";
    public static final String API_KEY = "0.1";
    private static final String API_ROOTURL = "%s/android-2.1/";
    public static final String API_URL_ALLCINEMAS = "%s/android-2.1/api/all-cinemas";
    public static final String API_URL_APPINIT = "%s/android-2.1/api/app-init";
    public static final String API_URL_FILMDETAILS = "%s/android-2.1/api/film-details";
    public static final String API_URL_FILMLISTSCHEDULE = "%s/android-2.1/api/additional-schedule-information";
    public static final String API_URL_FILMSCHEDULE = "%s/android-2.1/api/film-times";
    public static final String API_URL_GETREWARDS = "%s/android-2.1/api/get-rewards";
    public static final String API_URL_OPC_CARD_BARCODE = "%s/android-2.1/api/get-opc-barcode/cCardId/%s/";
    public static final String API_URL_POSTCODE = "%s/android-2.1/api/post-code";
    public static final String API_URL_RATEAFILM = "%s/android-2.1/api/rate-a-film";
    public static final long APPINIT_DELAY_REQUIRED = 14400000;
    public static final long APPINIT_DELAY_REQUIRED_IF_NO_CONNECTIVITY = 259200000;
    public static final String BITMAP_OPC_CARD_BARCODE_FILE_NAME = "opc_card_barcode_%s.png";
    public static final String BOOKING_BASEURL = "%s/android-2.1/booking_standard/";
    public static final String BOOKING_URL_CHECK_USER_DETAILS = "%s/android-2.1/booking_standard/checkuserdetails";
    public static final String BOOKING_URL_INIT = "%s/android-2.1/booking_standard/booking-init";
    public static final String BOOKING_URL_SELECT_SEATS = "%s/android-2.1/booking_standard/selectseats";
    public static final String BOOKING_URL_UNSELECT_SEATS = "%s/android-2.1/booking_standard/reset-seat-selection";
    public static final String CUSTOMER_PREFS_CINEMA_ID = "cinemaId";
    public static final String CUSTOMER_PREFS_CITY = "cityOrTown";
    public static final String CUSTOMER_PREFS_DOB = "dob";
    public static final String CUSTOMER_PREFS_EMAIL = "email";
    public static final String CUSTOMER_PREFS_EMAIL_CONFIRM = "emailConfirm";
    public static final String CUSTOMER_PREFS_FILMTIMES = "filmtimes";
    public static final String CUSTOMER_PREFS_FIRSTNAME = "firstname";
    public static final String CUSTOMER_PREFS_HOUSE = "houseNoOrName";
    public static final String CUSTOMER_PREFS_LASTNAME = "lastname";
    public static final String CUSTOMER_PREFS_OFFERS = "offers";
    public static final String CUSTOMER_PREFS_OPC_CARD = "customerCardId";
    public static final String CUSTOMER_PREFS_OPC_PACKAGE = "opcPackage";
    public static final String CUSTOMER_PREFS_PASSWORD = "password";
    public static final String CUSTOMER_PREFS_PHONE = "phone";
    public static final String CUSTOMER_PREFS_POSTCODE = "postcode";
    public static final String CUSTOMER_PREFS_STREET = "street";
    public static final String CUSTOMER_PREFS_TITLE = "title";
    public static final String CUSTOMER_PREFS_USERNAME = "username";
    public static final String DBTABLE_FILM = "Film";
    public static final String DBTABLE_FILM_DETAILS = "FilmDetails";
    public static final String DBTABLE_FILM_FILMINSITE = "FilmInSite";
    public static final String DBTABLE_OFFER = "Offer";
    public static final String DBTABLE_SITE = "Site";
    public static final String DBTABLE_SITE_FAVOURITE = "SiteFavourite";
    public static final String EXTRA_CINEMA_ID = "cinemaID";
    public static final String EXTRA_FILM_ID = "filmID";
    public static final String EXTRA_LOCATION = "Location";
    public static final String EXTRA_PERFORMANCE_ID = "performanceID";
    public static final String EXTRA_POSTCODE = "PostCode";
    public static final String EXTRA_TRAILER_URL = "trailerURL";
    public static final String EXTRA_UPDATESERVICE_MSG_HEADER = "MsgHeader";
    public static final String EXTRA_UPDATESERVICE_MSG_TEXT = "MsgText";
    public static final String EXTRA_UPDATESERVICE_ORIGINTENT = "OrigIntent";
    public static final String EXTRA_UPDATESERVICE_STATUS = "Status";
    public static final String EXTRA_WEBVIEW_HEADER_TITLE = "webviewHeaderTitle";
    public static final String EXTRA_WEBVIEW_TITLE = "webviewTitle";
    public static final String EXTRA_WEBVIEW_URL = "webviewURL";
    public static final String FACEBOOK_APP_ID = "124203494340721";
    public static long FILM_DETAIL_DELAY_REFRESH_REQUIRED = 0;
    public static int FILM_LIST_MSG_EMPTY = 0;
    public static int FILM_LIST_MSG_TRAILERCLICK = 0;
    public static final long FILM_LIST_SCHEDULE_TASK_CACHE_TTL = 1800000;
    public static final int MSG_BITMAP_ERROR = 75300;
    public static final int MSG_BITMAP_LOADED = 75301;
    public static final String OPC_BASEURL = "%s/android-2.1/webview/";
    public static final String OPC_URL_JOIN_PAYMENT_DETAILS = "%s/android-2.1/webview/join-opc-payment";
    public static final int PASSWORD_MAX_LENGTH = 20;
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final String PREFS_DATAHASH = "dataHash";
    public static final String PREFS_DATAHASH_ADD_SCHEDULE_INFO = "dataHashAddScheduleInfo_";
    public static final String PREFS_DATAHASH_ADD_SCHEDULE_INFO_SITES = "dataHashAddScheduleInfoSites";
    public static final String PREFS_LOCATION = "appLocation";
    public static final String PREF_LASTINIT_TS = "LastInitTS";
    public static final String PREF_SITEDIST_LASTLOC = "SiteDistLastLoc";
    public static final String PREF_SITEDIST_LASTTS = "SiteDistLastTS";
    public static final String PREF_SITEDIST_LAST_POSTCODE_LOC = "SiteDistLastPostCodeLoc";
    public static final String PREF_SITEDIST_POSTCODE_LASTTS = "SiteDistLastPostCodeTS";
    public static final int REQUEST_CODE_FINISH_CHECK = 1000;
    public static final int RESULT_CODE_FINISH = 1001;
    public static final long REWARDS_TASK_CACHE_TTL = 10800000;
    public static final String SECTION_ALPHA_SELECTED = "FF";
    public static final String SECTION_ALPHA_UNSELECTED = "44";
    public static long SITEDIST_DELAY_REQUIRED = 0;
    public static final float SITEDIST_LOCUPDATE_MINDIST_METERS = 500.0f;
    public static final int SITEDIST_LOCUPDATE_MINTIME_MS = 300000;
    public static final int SITEDIST_MSG_NOTIFY = 9000;
    public static final float SITEDIST_REFRESH_MINDIST_METERS = 250.0f;
    public static final int TICKET_LIST_RELOAD = 1001;
    public static final int TICKET_MAXIMUM = 9;
    public static final String URL_BOOKING_TERMS_AND_CONDITIONS = "%s//webviews/booking-terms-and-conditions.html";
    public static final String URL_CONTACT_US = "%s//webviews/contact-us.html";
    public static final String URL_HELP = "%s//webviews/help-android.html";
    public static final String URL_OPC_POINTS_INFO = "%s//webviews/points-info.html";
    public static final String URL_OPC_SUMMARY = "%s//webviews/opc-summary.html";
    public static final String URL_OPC_TERMS_AND_CONDITIONS = "%s//webviews/opc-tandc.html";
    public static final String URL_OTHER_POLICIES = "%s//webviews/other-odeon-policies.html";
    public static final String URL_PRIVACY_POLICY = "%s//webviews/privacy-policy.html";
    public static final String URL_TERMS_AND_CONDITIONS = "%s//webviews/terms-of-use.html";
    public static final String WEBVIEW_BASEURL = "%s//webviews/";
    private static final String WEBVIEW_ROOTURL = "%s/";

    public enum APP_LOCATION {
        uk,
        ire
    }

    public static String formatLocationUrl(String url) {
        Object[] objArr = new Object[1];
        objArr[0] = ODEONApplication.getInstance().getChoosenLocation().equals(APP_LOCATION.ire) ? API_HOSTURL_IRE : API_HOSTURL;
        return String.format(url, objArr);
    }

    public static String formatLocationUrlWithParam(String url, String param) {
        Object[] objArr = new Object[2];
        objArr[0] = ODEONApplication.getInstance().getChoosenLocation().equals(APP_LOCATION.ire) ? API_HOSTURL_IRE : API_HOSTURL;
        objArr[1] = param;
        return String.format(url, objArr);
    }

    static {
        FILM_LIST_MSG_TRAILERCLICK = 9233;
        FILM_LIST_MSG_EMPTY = 9234;
        FILM_DETAIL_DELAY_REFRESH_REQUIRED = APPINIT_DELAY_REQUIRED;
        SITEDIST_DELAY_REQUIRED = 1200000;
    }
}
