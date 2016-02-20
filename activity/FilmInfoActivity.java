package uk.co.odeon.androidapp.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.Constants.APP_LOCATION;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity.RootActivity;
import uk.co.odeon.androidapp.custom.NavigatorBarSubActivity;
import uk.co.odeon.androidapp.dialog.FilmRatingDialog;
import uk.co.odeon.androidapp.dialog.FilmRatingDialog.OnRatingListener;
import uk.co.odeon.androidapp.dialog.TellFriendAboutFilmDialog;
import uk.co.odeon.androidapp.provider.FilmContent.FilmColumns;
import uk.co.odeon.androidapp.provider.FilmContent.FilmDetailsColumns;
import uk.co.odeon.androidapp.provider.FilmContent.FilmInSiteColumns;
import uk.co.odeon.androidapp.provider.OfferContent.OfferColumns;
import uk.co.odeon.androidapp.updateservice.AbstractUpdateService.UpdateServiceSimpleStatus;
import uk.co.odeon.androidapp.updateservice.FilmDetailService;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;
import uk.co.odeon.androidapp.util.calendar.CalendarEntry;
import uk.co.odeon.androidapp.util.calendar.CalendarEntryCreator;
import uk.co.odeon.androidapp.util.calendar.CalendarEntryCreatorFactory;
import uk.co.odeon.androidapp.util.drawable.DrawableManager;

public class FilmInfoActivity extends NavigatorBarSubActivity {
    protected static final int DIALOG_RATE = 71002;
    protected static final int DIALOG_TELLFRIEND = 71001;
    protected static final String TAG;
    public static final int loadingImageRes = 2130837555;
    public static final int unavailableImageRes = 2130837555;
    protected int cinemaId;
    protected Cursor filmData;
    private IntentFilter filmDetailBroadCastFilter;
    protected Cursor filmDetails;
    protected ContentObserver filmDetailsObserver;
    protected int filmId;
    private IntentFilter filmImageBroadCastFilter;
    protected Cursor filmInSiteData;
    private final OnRatingListener onRatingListener;
    protected ProgressDialog progressDialog;
    private OnClickListener ratingClickListener;
    private OnClickListener remindMeClickListener;
    private OnClickListener seeLaterClickListener;
    private OnClickListener showtimesClickListener;
    private BroadcastReceiver statusBroadcastReceiver;
    private OnClickListener tellAFriendClickListener;
    private OnClickListener trailerClickListener;
    protected String trailerURL;

    /* renamed from: uk.co.odeon.androidapp.activity.FilmInfoActivity.8 */
    class AnonymousClass8 extends Handler {
        private final /* synthetic */ Dialog val$dt;

        AnonymousClass8(Dialog dialog) {
            this.val$dt = dialog;
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Intent twitterIntent = new Intent(this.val$dt.getContext(), TwitterActivity.class);
                twitterIntent.putExtra(Constants.EXTRA_FILM_ID, FilmInfoActivity.this.filmId);
                FilmInfoActivity.this.startActivity(twitterIntent);
            }
        }
    }

    protected class FilmDetailContentObserver extends ContentObserver {
        public FilmDetailContentObserver() {
            super(new Handler());
        }

        public void onChange(boolean selfChange) {
            FilmInfoActivity.this.filmDetails.requery();
            FilmInfoActivity.this.filmDetails.moveToFirst();
            FilmInfoActivity.this.setDetailsDataInView();
        }
    }

    protected class FilmInfoBroadcastReceiver extends BroadcastReceiver {
        private static /* synthetic */ int[] $SWITCH_TABLE$uk$co$odeon$androidapp$updateservice$AbstractUpdateService$UpdateServiceSimpleStatus;

        static /* synthetic */ int[] $SWITCH_TABLE$uk$co$odeon$androidapp$updateservice$AbstractUpdateService$UpdateServiceSimpleStatus() {
            int[] iArr = $SWITCH_TABLE$uk$co$odeon$androidapp$updateservice$AbstractUpdateService$UpdateServiceSimpleStatus;
            if (iArr == null) {
                iArr = new int[UpdateServiceSimpleStatus.values().length];
                try {
                    iArr[UpdateServiceSimpleStatus.DONE_NOCHANGES.ordinal()] = 3;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[UpdateServiceSimpleStatus.DONE_NOTREQ.ordinal()] = 2;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[UpdateServiceSimpleStatus.DONE_UPDATED.ordinal()] = 4;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[UpdateServiceSimpleStatus.FAILED.ordinal()] = 6;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[UpdateServiceSimpleStatus.RUNNING.ordinal()] = 1;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[UpdateServiceSimpleStatus.SLOW.ordinal()] = 5;
                } catch (NoSuchFieldError e6) {
                }
                $SWITCH_TABLE$uk$co$odeon$androidapp$updateservice$AbstractUpdateService$UpdateServiceSimpleStatus = iArr;
            }
            return iArr;
        }

        protected FilmInfoBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            Log.i(FilmInfoActivity.TAG, "Broadcast received: " + intent);
            String action = intent.getAction();
            UpdateServiceSimpleStatus status = (UpdateServiceSimpleStatus) intent.getExtras().getParcelable(Constants.EXTRA_UPDATESERVICE_STATUS);
            Log.i(FilmInfoActivity.TAG, "Broadcasted status for action " + action + " is: " + status);
            if (action.equals(Constants.ACTION_FILMDETAIL_STATUS)) {
                switch ($SWITCH_TABLE$uk$co$odeon$androidapp$updateservice$AbstractUpdateService$UpdateServiceSimpleStatus()[status.ordinal()]) {
                    case AmazingListView.PINNED_HEADER_PUSHED_UP /*2*/:
                    case RootActivity.TYPE_RIGHT /*3*/:
                    case RootActivity.TYPE_SINGLE /*4*/:
                        FilmInfoActivity.this.hideProgress(true);
                    case R.styleable.com_deezapps_widget_PagerControl_useCircles /*5*/:
                        FilmInfoActivity.this.showProgress(R.string.film_info_progress, true);
                    default:
                }
            }
        }
    }

    public FilmInfoActivity() {
        this.filmData = null;
        this.filmInSiteData = null;
        this.filmId = 0;
        this.filmDetails = null;
        this.cinemaId = 0;
        this.trailerURL = null;
        this.filmDetailsObserver = null;
        this.showtimesClickListener = new OnClickListener() {
            public void onClick(View v) {
                ODEONApplication.trackEvent("Showtimes-book now", "Click", FilmInfoActivity.this.filmData.getString(FilmInfoActivity.this.filmData.getColumnIndex(OfferColumns.TITLE)));
                if (FilmInfoActivity.this.cinemaId > 0) {
                    Intent filmSchedule = new Intent(FilmInfoActivity.this.getParent(), FilmScheduleActivity.class);
                    filmSchedule.putExtra(Constants.EXTRA_FILM_ID, FilmInfoActivity.this.filmId);
                    filmSchedule.putExtra(Constants.EXTRA_CINEMA_ID, FilmInfoActivity.this.cinemaId);
                    FilmInfoActivity.this.startSubActivity(filmSchedule, "Showtimes");
                    return;
                }
                Intent cinemaList = new Intent(FilmInfoActivity.this.getParent(), CinemaListActivity.class);
                cinemaList.putExtra(Constants.EXTRA_FILM_ID, FilmInfoActivity.this.filmId);
                cinemaList.setAction(Constants.ACTION_CINEMALIST_AUTOPICK);
                FilmInfoActivity.this.startSubActivity(cinemaList, "Cinemas");
            }
        };
        this.trailerClickListener = new OnClickListener() {
            public void onClick(View v) {
                ODEONApplication.trackEvent("View trailer", "Click", FilmInfoActivity.this.trailerURL);
                Intent filmTrailer = new Intent(FilmInfoActivity.this.getParent(), FilmTrailerActivity.class);
                filmTrailer.putExtra(Constants.EXTRA_FILM_ID, FilmInfoActivity.this.filmId);
                filmTrailer.putExtra(FilmColumns.TRAILER_URL, FilmInfoActivity.this.trailerURL);
                FilmInfoActivity.this.startActivity(filmTrailer);
            }
        };
        this.tellAFriendClickListener = new OnClickListener() {
            public void onClick(View v) {
                ODEONApplication.trackEvent("Tell friends about film", "Click", FilmInfoActivity.this.filmData.getString(FilmInfoActivity.this.filmData.getColumnIndex(OfferColumns.TITLE)));
                FilmInfoActivity.this.showDialog(FilmInfoActivity.DIALOG_TELLFRIEND);
            }
        };
        this.ratingClickListener = new OnClickListener() {
            public void onClick(View v) {
                ODEONApplication.trackEvent("Rate this film", "Click", FilmInfoActivity.this.filmData.getString(FilmInfoActivity.this.filmData.getColumnIndex(OfferColumns.TITLE)));
                FilmInfoActivity.this.showDialog(FilmInfoActivity.DIALOG_RATE);
            }
        };
        this.remindMeClickListener = new OnClickListener() {
            public void onClick(View v) {
                ODEONApplication.trackEvent("Set release reminder", "Click", FilmInfoActivity.this.filmData.getString(FilmInfoActivity.this.filmData.getColumnIndex(OfferColumns.TITLE)));
                FilmInfoActivity.this.addCalendarEntry();
            }
        };
        this.seeLaterClickListener = new OnClickListener() {
            public void onClick(View v) {
                boolean isFavourite;
                boolean z;
                boolean z2 = false;
                if (v.getTag() instanceof Boolean) {
                    isFavourite = ((Boolean) v.getTag()).booleanValue();
                } else {
                    isFavourite = false;
                }
                if (isFavourite) {
                    ODEONApplication.trackEvent("Remove to see later", "Click", FilmInfoActivity.this.filmData.getString(FilmInfoActivity.this.filmData.getColumnIndex(OfferColumns.TITLE)));
                } else {
                    ODEONApplication.trackEvent("Save to see later", "Click", FilmInfoActivity.this.filmData.getString(FilmInfoActivity.this.filmData.getColumnIndex(OfferColumns.TITLE)));
                }
                ODEONApplication instance = ODEONApplication.getInstance();
                int i = FilmInfoActivity.this.filmId;
                if (isFavourite) {
                    z = false;
                } else {
                    z = true;
                }
                instance.changeFilmFavouriteInDatabase(i, z);
                FilmInfoActivity filmInfoActivity = FilmInfoActivity.this;
                if (!isFavourite) {
                    z2 = true;
                }
                filmInfoActivity.setSeeLaterDataInView(z2);
            }
        };
        this.onRatingListener = new OnRatingListener() {
            public void onRatingDone(int rating) {
                ODEONApplication.trackEvent("Rate this film Number Of Stars", "Click", String.valueOf(rating));
                FilmInfoActivity.this.setRatingButtonDataInView(true);
            }
        };
    }

    static {
        TAG = FilmInfoActivity.class.getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.film_info);
        Uri filmUri = getIntent().getData();
        findViewById(R.id.filmInfoShowtimesLayout).setOnClickListener(this.showtimesClickListener);
        this.filmId = (int) ContentUris.parseId(filmUri);
        this.cinemaId = getIntent().getIntExtra(Constants.EXTRA_CINEMA_ID, 0);
        this.filmData = ODEONApplication.getInstance().getFilmDataCursor(this, this.filmId);
        this.filmInSiteData = getFilmInSiteCursor();
        this.filmDetails = getFilmDetailsCursor(filmUri);
        setDataInView();
        initObserverAndBroadastReceiver();
        requestFilmDetails(filmUri);
        if (this.filmDetails.getCount() == 0) {
            Log.d(TAG, "Film details data not yet found: " + filmUri);
            return;
        }
        this.filmDetails.moveToFirst();
        setDetailsDataInView();
    }

    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        initObserverAndBroadastReceiver();
        if (this.filmData != null) {
            this.filmData.requery();
            if (this.filmData.moveToFirst()) {
                setSeeLaterDataInView(this.filmData.getString(this.filmData.getColumnIndex(FilmColumns.FAVOURITE)).equals("1"));
            }
        }
    }

    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        hideProgress(true);
        removeObserverAndBroadastReceiver();
    }

    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onPause();
        removeObserverAndBroadastReceiver();
    }

    public void onRecycled() {
        Log.i(TAG, "onRecycled");
        super.onRecycled();
        if (this.filmData != null) {
            this.filmData.requery();
            if (this.filmData.moveToFirst()) {
                setSeeLaterDataInView(this.filmData.getString(this.filmData.getColumnIndex(FilmColumns.FAVOURITE)).equals("1"));
            }
        }
    }

    private Cursor getFilmInSiteCursor() {
        return managedQuery(ContentUris.withAppendedId(Uri.withAppendedPath(FilmInSiteColumns.CONTENT_URI, "/film"), (long) this.filmId), null, null, null, null);
    }

    protected void initObserverAndBroadastReceiver() {
        createObserver();
        registerStatusReceiver();
    }

    protected void removeObserverAndBroadastReceiver() {
        removeObserver();
        unregisterStatusReceiver();
    }

    protected void removeObserver() {
        if (this.filmDetails != null && this.filmDetailsObserver != null) {
            this.filmDetails.unregisterContentObserver(this.filmDetailsObserver);
            this.filmDetailsObserver = null;
        }
    }

    protected void createObserver() {
        if (this.filmDetails != null && this.filmDetailsObserver == null) {
            this.filmDetailsObserver = new FilmDetailContentObserver();
            this.filmDetails.registerContentObserver(this.filmDetailsObserver);
        }
    }

    protected void registerStatusReceiver() {
        if (this.statusBroadcastReceiver == null) {
            this.statusBroadcastReceiver = new FilmInfoBroadcastReceiver();
        }
        if (this.filmDetailBroadCastFilter == null) {
            this.filmDetailBroadCastFilter = new IntentFilter(Constants.ACTION_FILMDETAIL_STATUS);
            registerReceiver(this.statusBroadcastReceiver, this.filmDetailBroadCastFilter);
        }
        if (this.filmImageBroadCastFilter == null) {
            this.filmImageBroadCastFilter = new IntentFilter(Constants.ACTION_FILMIMGDOWNLOAD_STATUS);
            registerReceiver(this.statusBroadcastReceiver, this.filmImageBroadCastFilter);
        }
    }

    protected void unregisterStatusReceiver() {
        if (this.statusBroadcastReceiver != null) {
            try {
                unregisterReceiver(this.statusBroadcastReceiver);
            } catch (Throwable e) {
                Log.w(TAG, "Failed to unregister broadcast receiver.. ", e);
            }
        }
        this.statusBroadcastReceiver = null;
    }

    protected Cursor getFilmDetailsCursor(Uri uri) {
        Log.d(TAG, "Retrieving film details from DB");
        return managedQuery(uri, null, null, null, null);
    }

    protected void requestFilmDetails(Uri filmUri) {
        startService(new Intent(this, FilmDetailService.class).setData(filmUri).putExtra("ts", System.currentTimeMillis()));
    }

    private void setDataInView() {
        if (this.filmInSiteData != null) {
            if (this.filmInSiteData.getCount() == 0) {
                setEnabledRecursive((ViewGroup) findViewById(R.id.filmInfoShowtimesLayout), false);
            }
        }
        if (this.filmData != null) {
            if (this.filmData.getCount() != 0) {
                ((TextView) findViewById(R.id.filmInfoTitle)).setText(this.filmData.getString(this.filmData.getColumnIndex(OfferColumns.TITLE)));
                setTextOrHide(this.filmData.getString(this.filmData.getColumnIndex(FilmColumns.GENRE)), R.id.filmInfoGenre, R.id.filmInfoGenreLabel);
                setTextOrHide(this.filmData.getString(this.filmData.getColumnIndex(FilmColumns.RELDATE)), R.id.filmInfoReleaseDate, R.id.filmInfoReleaseDateLabel);
                boolean comingSoon = this.filmData.getInt(this.filmData.getColumnIndex(FilmColumns.COMINGSOON)) == 1;
                boolean futureRelease = this.filmData.getInt(this.filmData.getColumnIndex(FilmColumns.FUTURERELEASE)) == 1;
                setReminderButtonDataInView(comingSoon, futureRelease);
                boolean rateable = (this.filmData.getInt(this.filmData.getColumnIndex(FilmColumns.RATEABLE)) != 1 || comingSoon || futureRelease) ? false : true;
                RatingBar ratingView = (RatingBar) findViewById(R.id.filmInfoRating);
                if (rateable) {
                    ratingView.setRating(((float) this.filmData.getInt(this.filmData.getColumnIndex(FilmColumns.HALFRATING))) / 2.0f);
                    ratingView.setVisibility(0);
                } else {
                    ratingView.setVisibility(4);
                }
                setRatingButtonDataInView(rateable);
                ((ImageView) findViewById(R.id.filmInfoCertificate)).setImageResource(ODEONApplication.getInstance().certStringToImageResource(this.filmData.getString(this.filmData.getColumnIndex(FilmColumns.CERTIFICATE))));
                setSeeLaterDataInView(this.filmData.getString(this.filmData.getColumnIndex(FilmColumns.FAVOURITE)).equals("1"));
                this.trailerURL = this.filmData.getString(this.filmData.getColumnIndex(FilmColumns.TRAILER_URL));
                boolean hasTrailer = this.trailerURL != null;
                ViewGroup trailerView = (ViewGroup) findViewById(R.id.filmInfoTrailerLayout);
                setEnabledRecursive(trailerView, hasTrailer);
                trailerView.setOnClickListener(this.trailerClickListener);
                if (!hasTrailer) {
                    ((ImageView) trailerView.findViewById(R.id.filmInfoTrailerImage)).setAlpha(127);
                }
                ((ViewGroup) findViewById(R.id.filmInfoTellFriendLayout)).setOnClickListener(this.tellAFriendClickListener);
            }
        }
    }

    private void setDetailsDataInView() {
        setTextAndLabelOrHide(this.filmDetails.getString(this.filmDetails.getColumnIndex(FilmDetailsColumns.DIRECTOR)), R.id.filmInfoDirectedBy, R.string.film_info_label_director, new Integer[0]);
        setTextAndLabelOrHide(this.filmDetails.getString(this.filmDetails.getColumnIndex(FilmDetailsColumns.CAST)), R.id.filmInfoStarring, R.string.film_info_label_starring, new Integer[0]);
        String runningTime = this.filmDetails.getString(this.filmDetails.getColumnIndex(FilmDetailsColumns.RUNNING_TIME));
        if (isValidStr(runningTime)) {
            runningTime = getResources().getString(R.string.film_info_running_time_value, new Object[]{runningTime});
        }
        setTextAndLabelOrHide(runningTime, R.id.filmInfoRunningTime, R.string.film_info_label_running_time, new Integer[0]);
        setTextAndLabelOrHide(this.filmDetails.getString(this.filmDetails.getColumnIndex(FilmDetailsColumns.COUNTRY)), R.id.filmInfoCountry, R.string.film_info_label_country, new Integer[0]);
        setTextAndLabelOrHide(this.filmDetails.getString(this.filmDetails.getColumnIndex(FilmDetailsColumns.LANGUAGE)), R.id.filmInfoLanguage, R.string.film_info_label_language, new Integer[0]);
        String consumerAdvice = this.filmDetails.getString(this.filmDetails.getColumnIndex(FilmDetailsColumns.BBFC_RATING));
        if (ODEONApplication.getInstance().getChoosenLocation().equals(APP_LOCATION.ire)) {
            setTextAndLabelOrHide(consumerAdvice, R.id.filmInfoConsumerAdvice, R.string.film_info_label_bbfc_ire, new Integer[0]);
        } else {
            setTextAndLabelOrHide(consumerAdvice, R.id.filmInfoConsumerAdvice, R.string.film_info_label_bbfc, new Integer[0]);
        }
        setTextAndLabelOrHide(this.filmDetails.getString(this.filmDetails.getColumnIndex(FilmDetailsColumns.PLOT)), R.id.filmInfoPlot, R.string.film_info_label_plot, new Integer[0]);
        String imageURL = this.filmDetails.getString(this.filmDetails.getColumnIndex(OfferColumns.IMAGE_URL));
        ImageView imageView = (ImageView) findViewById(R.id.filmInfoImage);
        DrawableManager dm = DrawableManager.getInstance();
        dm.loadDrawable(imageURL, imageView, dm.buildImageCacheFileBasedOnURLFilename(imageURL), R.drawable.film_info_noimg, R.drawable.film_info_noimg);
        ImageView bbfImageView = (ImageView) findViewById(R.id.filmInfoBBFImage);
        if (bbfImageView == null) {
            return;
        }
        if (this.filmData.getInt(this.filmData.getColumnIndex(FilmColumns.BBF)) == 1) {
            bbfImageView.setVisibility(0);
        } else {
            bbfImageView.setVisibility(8);
        }
    }

    protected void setRatingButtonDataInView(boolean rateable) {
        ViewGroup ratingBtnView = (ViewGroup) findViewById(R.id.filmInfoRatingLayout);
        TextView ratingText = (TextView) findViewById(R.id.filmInfoRatingText);
        if (rateable) {
            ratingBtnView.setOnClickListener(this.ratingClickListener);
            if (ODEONApplication.getInstance().getCustomerDataPrefs().getInt("rating_" + this.filmId, -1) > -1) {
                ratingText.setText(getResources().getString(R.string.film_info_btn_rate_already_rated));
                ((ImageView) findViewById(R.id.filmInfoRatingImage)).setImageResource(R.drawable.icon_tick);
                return;
            }
            return;
        }
        setEnabledRecursive(ratingBtnView, false);
        ratingText.setText(getResources().getString(R.string.film_info_btn_rate_notrateable));
    }

    private void setReminderButtonDataInView(boolean comingSoon, boolean futureRelease) {
        ViewGroup reminderBtnView = (ViewGroup) findViewById(R.id.filmInfoReminderLayout);
        boolean isRemindable = comingSoon || futureRelease;
        if (isRemindable) {
            String releaseDate = this.filmData.getString(this.filmData.getColumnIndex(FilmColumns.RELDATE));
            if (releaseDate == null || "".equals(releaseDate)) {
                isRemindable = false;
            }
        }
        if (isRemindable) {
            reminderBtnView.setVisibility(0);
            reminderBtnView.setOnClickListener(this.remindMeClickListener);
            return;
        }
        reminderBtnView.setVisibility(8);
    }

    protected void setSeeLaterDataInView(boolean isFavourite) {
        ViewGroup seeLaterView = (ViewGroup) findViewById(R.id.filmInfoSeeLaterLayout);
        seeLaterView.setTag(Boolean.valueOf(isFavourite));
        ImageView icon = (ImageView) findViewById(R.id.filmInfoSeeLaterImage);
        TextView text = (TextView) findViewById(R.id.filmInfoSeeLaterText);
        if (isFavourite) {
            if (icon != null) {
                icon.setImageResource(R.drawable.icon_tick);
            }
            if (text != null) {
                text.setText(R.string.film_info_btn_saved_see_later);
            }
        } else {
            if (icon != null) {
                icon.setImageResource(R.drawable.icon_add);
            }
            if (text != null) {
                text.setText(R.string.film_info_btn_save_see_later);
            }
        }
        seeLaterView.setOnClickListener(this.seeLaterClickListener);
    }

    private void setEnabledRecursive(ViewGroup v, boolean enabled) {
        v.setEnabled(enabled);
        for (int i = 0; i < v.getChildCount(); i++) {
            View vc = v.getChildAt(i);
            if (vc instanceof ViewGroup) {
                setEnabledRecursive((ViewGroup) vc, enabled);
            } else {
                vc.setEnabled(enabled);
            }
        }
    }

    private boolean isValidStr(String str) {
        if (str == null || str.trim().length() == 0 || "null".equals(str)) {
            return false;
        }
        return true;
    }

    private void setTextOrHide(String str, int textViewId, int labelViewId) {
        TextView tv = (TextView) findViewById(textViewId);
        View lv = labelViewId > -1 ? findViewById(labelViewId) : null;
        if (isValidStr(str)) {
            tv.setText(str);
            return;
        }
        tv.setVisibility(8);
        if (lv != null) {
            lv.setVisibility(8);
        }
    }

    private void setTextAndLabelOrHide(String str, int textAndLabelViewId, int labelStrId, Integer... additionalLableIds) {
        int i = 0;
        TextView tv = (TextView) findViewById(textAndLabelViewId);
        if (isValidStr(str)) {
            String labelStr = getResources().getString(labelStrId);
            SpannableString nt = new SpannableString(new StringBuilder(String.valueOf(labelStr)).append(" ").append(str).toString());
            nt.setSpan(new StyleSpan(1), 0, labelStr.length(), 0);
            tv.setText(nt);
            return;
        }
        tv.setVisibility(8);
        int length = additionalLableIds.length;
        while (i < length) {
            findViewById(additionalLableIds[i].intValue()).setVisibility(8);
            i++;
        }
    }

    protected boolean isValidFilmImageURL(String imageURL) {
        return imageURL != null && imageURL.length() > 0;
    }

    private void addCalendarEntry() {
        String releaseDateStr = this.filmData.getString(this.filmData.getColumnIndex(FilmColumns.RELDATE));
        if (releaseDateStr == null || (releaseDateStr != null && releaseDateStr.trim().length() == 0)) {
            Toast.makeText(getDialogContext(), "Ooops! No release date known.", 1).show();
            return;
        }
        long releaseDateMillis = parseReleaseDate(releaseDateStr);
        if (releaseDateMillis < 0) {
            Toast.makeText(getDialogContext(), "Ooops! No valid release date known.", 1).show();
            return;
        }
        String filmTitle = this.filmData.getString(this.filmData.getColumnIndex(OfferColumns.TITLE));
        CalendarEntryCreator calCreator = new CalendarEntryCreatorFactory().getCalendarEntryCreator(getDialogContext());
        CalendarEntry calEntry = new CalendarEntry();
        calEntry.setTitle(new StringBuilder(String.valueOf(filmTitle)).append(" ").append(getResources().getString(R.string.film_info_reminder_calendar_title_suffix)).toString());
        calEntry.setBeginTimeMillis(releaseDateMillis);
        calEntry.setEndTimeMillis(releaseDateMillis);
        calEntry.setAllDay(Boolean.valueOf(true));
        calCreator.openCalendarEntryDialog(calEntry, true);
    }

    private long parseReleaseDate(String releaseDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.UK);
        Calendar releaseDateCal = Calendar.getInstance();
        try {
            releaseDateCal.setTime(sdf.parse(releaseDateStr));
            releaseDateCal.set(11, 0);
            releaseDateCal.set(12, 0);
            releaseDateCal.set(13, 0);
            releaseDateCal.set(14, 0);
            int year = releaseDateCal.get(1);
            if (year >= 2012 && year <= 2025) {
                return releaseDateCal.getTimeInMillis();
            }
            throw new ParseException("Year " + year + " is not in valid range", 0);
        } catch (ParseException pe) {
            Log.e(TAG, String.format("Can't parse date '%s': %s", new Object[]{releaseDateStr, pe.getMessage()}), pe);
            return -1;
        }
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_TELLFRIEND /*71001*/:
                Dialog dt = new TellFriendAboutFilmDialog(getDialogContext(), this.filmId);
                ((TellFriendAboutFilmDialog) dt).setHandler(new AnonymousClass8(dt));
                return dt;
            case DIALOG_RATE /*71002*/:
                return new FilmRatingDialog(getDialogContext(), this.filmId, this.onRatingListener);
            default:
                return super.onCreateDialog(id);
        }
    }
}
