package uk.co.odeon.androidapp.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.adapters.FilmListCursorAdapter;
import uk.co.odeon.androidapp.adapters.FilmListCursorAdapter.IndexerType;
import uk.co.odeon.androidapp.custom.CinemaHeaderView;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity.RootActivity;
import uk.co.odeon.androidapp.custom.NavigatorBarSubActivity;
import uk.co.odeon.androidapp.indexers.FilmListSingleSectionIndexer;
import uk.co.odeon.androidapp.json.FilterFilms;
import uk.co.odeon.androidapp.provider.FilmContent.FilmColumns;
import uk.co.odeon.androidapp.provider.FilmContent.FilmDetailsColumns;
import uk.co.odeon.androidapp.provider.SiteContent.SiteColumns;
import uk.co.odeon.androidapp.task.FilmListScheduleTaskCached;
import uk.co.odeon.androidapp.task.TaskTarget;
import uk.co.odeon.androidapp.updateservice.AbstractUpdateService.UpdateServiceSimpleStatus;
import uk.co.odeon.androidapp.updateservice.AppInitUpdateService;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;

public class FilmListActivity extends NavigatorBarSubActivity implements TaskTarget<FilterFilms> {
    private static final String TAG;
    private IntentFilter appInitBroadCastFilter;
    private BroadcastReceiver appInitBroadCastReceiver;
    protected Cursor cinemaData;
    private CinemaHeaderView cinemaHeader;
    protected int cinemaId;
    protected ToggleButton filmListFilterAdvanced;
    protected ToggleButton filmListFilterCurrent;
    protected ToggleButton filmListFilterNext;
    private FilmListScheduleTaskCached filmListScheduleTask;
    private FilmListCursorAdapter mAdapter;

    /* renamed from: uk.co.odeon.androidapp.activity.FilmListActivity.4 */
    class AnonymousClass4 implements OnClickListener {
        private final /* synthetic */ FilterFilms val$filmListScheduleData;

        AnonymousClass4(FilterFilms filterFilms) {
            this.val$filmListScheduleData = filterFilms;
        }

        public void onClick(View v) {
            ODEONApplication.trackEvent("CinemaDetail-today-films", "Show", "");
            ((ToggleButton) FilmListActivity.this.findViewById(R.id.filmListFilterCurrent)).setChecked(true);
            ((ToggleButton) FilmListActivity.this.findViewById(R.id.filmListFilterNext)).setChecked(false);
            ((ToggleButton) FilmListActivity.this.findViewById(R.id.filmListFilterAdvanced)).setChecked(false);
            if (FilmListActivity.this.mAdapter != null) {
                FilmListActivity.this.mAdapter.setSingleSectionIndexerSectionName(FilmListSingleSectionIndexer.SECTIONNAME_CINEMA_FILMS_CURRENT);
                FilmListActivity.this.mAdapter.setAccessibleData(this.val$filmListScheduleData.filmAccessibleCurrent);
                FilmListActivity.this.mAdapter.changeCursor(FilmListActivity.this.getFilmCursorFilter(this.val$filmListScheduleData.filmIdsCurrent));
            }
        }
    }

    /* renamed from: uk.co.odeon.androidapp.activity.FilmListActivity.5 */
    class AnonymousClass5 implements OnClickListener {
        private final /* synthetic */ FilterFilms val$filmListScheduleData;

        AnonymousClass5(FilterFilms filterFilms) {
            this.val$filmListScheduleData = filterFilms;
        }

        public void onClick(View v) {
            ODEONApplication.trackEvent("CinemaDetail-7days-films", "Show", "");
            ((ToggleButton) FilmListActivity.this.findViewById(R.id.filmListFilterCurrent)).setChecked(false);
            ((ToggleButton) FilmListActivity.this.findViewById(R.id.filmListFilterNext)).setChecked(true);
            ((ToggleButton) FilmListActivity.this.findViewById(R.id.filmListFilterAdvanced)).setChecked(false);
            if (FilmListActivity.this.mAdapter != null) {
                FilmListActivity.this.mAdapter.setSingleSectionIndexerSectionName(FilmListSingleSectionIndexer.SECTIONNAME_CINEMA_FILMS_NEXT);
                FilmListActivity.this.mAdapter.setAccessibleData(this.val$filmListScheduleData.filmAccessibleNext);
                FilmListActivity.this.mAdapter.changeCursor(FilmListActivity.this.getFilmCursorFilter(this.val$filmListScheduleData.filmIdsNext));
            }
        }
    }

    /* renamed from: uk.co.odeon.androidapp.activity.FilmListActivity.6 */
    class AnonymousClass6 implements OnClickListener {
        private final /* synthetic */ FilterFilms val$filmListScheduleData;

        AnonymousClass6(FilterFilms filterFilms) {
            this.val$filmListScheduleData = filterFilms;
        }

        public void onClick(View v) {
            ODEONApplication.trackEvent("CinemaDetail-advanced-films", "Show", "");
            ((ToggleButton) FilmListActivity.this.findViewById(R.id.filmListFilterCurrent)).setChecked(false);
            ((ToggleButton) FilmListActivity.this.findViewById(R.id.filmListFilterNext)).setChecked(false);
            ((ToggleButton) FilmListActivity.this.findViewById(R.id.filmListFilterAdvanced)).setChecked(true);
            if (FilmListActivity.this.mAdapter != null) {
                FilmListActivity.this.mAdapter.setSingleSectionIndexerSectionName(FilmListSingleSectionIndexer.SECTIONNAME_CINEMA_FILMS_ADVANCED);
                FilmListActivity.this.mAdapter.setAccessibleData(this.val$filmListScheduleData.filmAccessibleAdvanced);
                FilmListActivity.this.mAdapter.changeCursor(FilmListActivity.this.getFilmCursorFilter(this.val$filmListScheduleData.filmIdsAdvanced));
            }
        }
    }

    public FilmListActivity() {
        this.cinemaData = null;
        this.cinemaId = 0;
        this.filmListFilterCurrent = null;
        this.filmListFilterNext = null;
        this.filmListFilterAdvanced = null;
        this.cinemaHeader = null;
        this.appInitBroadCastReceiver = new BroadcastReceiver() {
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

            public void onReceive(Context context, Intent intent) {
                Log.i(FilmListActivity.TAG, "AppInit status broadcast received: " + intent);
                UpdateServiceSimpleStatus status = (UpdateServiceSimpleStatus) intent.getExtras().getParcelable(Constants.EXTRA_UPDATESERVICE_STATUS);
                String msgHeader = intent.getExtras().getString(Constants.EXTRA_UPDATESERVICE_MSG_HEADER);
                String msgText = intent.getExtras().getString(Constants.EXTRA_UPDATESERVICE_MSG_TEXT);
                Log.i(FilmListActivity.TAG, "AppInit status broadcast received, status is: " + status);
                switch (AnonymousClass1.$SWITCH_TABLE$uk$co$odeon$androidapp$updateservice$AbstractUpdateService$UpdateServiceSimpleStatus()[status.ordinal()]) {
                    case AmazingListView.PINNED_HEADER_VISIBLE /*1*/:
                    case AmazingListView.PINNED_HEADER_PUSHED_UP /*2*/:
                    case RootActivity.TYPE_RIGHT /*3*/:
                    case RootActivity.TYPE_SINGLE /*4*/:
                        if (!FilmListActivity.this.isFilmListScheduleTaskRunning()) {
                            FilmListActivity.this.hideProgress(true);
                        }
                    case R.styleable.com_deezapps_widget_PagerControl_useCircles /*5*/:
                        FilmListActivity.this.showProgress(R.string.app_init_progress, true);
                    case R.styleable.com_deezapps_widget_PagerControl_circlePadding /*6*/:
                        if (FilmListActivity.this.isFilmListScheduleTaskRunning()) {
                            FilmListActivity.this.filmListScheduleTask.cancel(true);
                        }
                        FilmListActivity.this.hideProgress(true);
                        if (msgHeader != null && msgText != null) {
                            FilmListActivity.this.showAlert(msgHeader, msgText);
                        } else if (FilmListActivity.this.hasRelativelyFreshAppInitContent()) {
                            FilmListActivity.this.showDialog(30002);
                        } else {
                            FilmListActivity.this.startActivity(new Intent(FilmListActivity.this.getDialogContext(), ConnectivityWaitActivity.class));
                            FilmListActivity.this.finish();
                        }
                    default:
                        Log.w(FilmListActivity.TAG, "Unknown AppInit status: " + status + ", just hiding progress bar");
                        if (!FilmListActivity.this.isFilmListScheduleTaskRunning()) {
                            FilmListActivity.this.hideProgress(true);
                        }
                }
            }
        };
    }

    static {
        TAG = FilmListActivity.class.getSimpleName();
    }

    public CinemaHeaderView getCinemaHeader() {
        if (this.cinemaHeader == null) {
            this.cinemaHeader = (CinemaHeaderView) findViewById(R.id.cinemaHeaderLayout);
        }
        return this.cinemaHeader;
    }

    public boolean isFilmListScheduleTaskRunning() {
        return (this.filmListScheduleTask == null || Status.FINISHED.equals(this.filmListScheduleTask.getStatus())) ? false : true;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkConnectivityOrPreCachedInitDataAvailable()) {
            Cursor filmsCursor;
            Log.i(TAG, "Checked conn");
            this.appInitBroadCastFilter = new IntentFilter(Constants.ACTION_APPINIT_STATUS);
            registerReceiver(this.appInitBroadCastReceiver, this.appInitBroadCastFilter);
            setContentView(R.layout.film_list);
            this.filmListFilterCurrent = (ToggleButton) findViewById(R.id.filmListFilterCurrent);
            this.filmListFilterNext = (ToggleButton) findViewById(R.id.filmListFilterNext);
            this.filmListFilterAdvanced = (ToggleButton) findViewById(R.id.filmListFilterAdvanced);
            Intent intent = getIntent();
            Log.i(TAG, intent.toString());
            this.cinemaId = getIntent().getIntExtra(Constants.EXTRA_CINEMA_ID, 0);
            this.cinemaData = ODEONApplication.getInstance().getCinemaDataCursor((Activity) this, this.cinemaId);
            String selection = "hidden=0";
            String[] selectionArgs = null;
            if (this.cinemaId > 0) {
                selection = new StringBuilder(String.valueOf(selection)).append(" AND FilmInSite.siteID=?").toString();
                selectionArgs = new String[]{String.valueOf(this.cinemaId)};
            }
            IndexerType indexerType = IndexerType.ALPHA;
            String singleSectionIndexerSectionName = null;
            boolean adapterFilterMode = false;
            boolean fastScrollEnabled = false;
            if ("top5nowBooking".equals(intent.getAction())) {
                indexerType = IndexerType.SINGLE_SECTION;
                singleSectionIndexerSectionName = FilmListSingleSectionIndexer.SECTIONNAME_TOP_FILMS;
                filmsCursor = managedQuery(FilmColumns.CONTENT_URI, null, new StringBuilder(String.valueOf(selection)).append(" AND ( ").append(FilmColumns.TOP5).append(">0 OR ").append(FilmColumns.NOWBOOKING).append(">0 )").toString(), selectionArgs, FilmColumns.DEFAULT_SORT_ORDER);
            } else if ("comingSoon".equals(intent.getAction())) {
                indexerType = IndexerType.COMMING_SOON;
                filmsCursor = managedQuery(FilmColumns.CONTENT_URI, null, new StringBuilder(String.valueOf(selection)).append(" AND (").append(FilmColumns.COMINGSOON).append("=1 OR ").append(FilmColumns.FUTURERELEASE).append("=1 )").toString(), selectionArgs, "comingsoon DESC, relDateSort ASC");
            } else if (this.cinemaId > 0) {
                indexerType = IndexerType.SINGLE_SECTION;
                singleSectionIndexerSectionName = "";
                adapterFilterMode = true;
                LinearLayout filmListFilter = (LinearLayout) findViewById(R.id.filmListFilter);
                if (filmListFilter != null) {
                    filmListFilter.setVisibility(0);
                }
                filmsCursor = managedQuery(FilmColumns.CONTENT_URI, null, "0 = 1", null, null);
                this.filmListScheduleTask = new FilmListScheduleTaskCached(this);
                this.filmListScheduleTask.execute(new String[]{String.valueOf(this.cinemaId)});
                showProgress(R.string.default_progress, true);
            } else {
                fastScrollEnabled = true;
                filmsCursor = managedQuery(FilmColumns.CONTENT_URI, null, new StringBuilder(String.valueOf(selection)).append(" AND ").append(FilmColumns.COMINGSOON).append("!=1 AND ").append(FilmColumns.FUTURERELEASE).append("!=1").toString(), selectionArgs, FilmColumns.DEFAULT_SORT_ORDER);
            }
            if (filmsCursor != null) {
                Log.d(TAG, "Found " + filmsCursor.getCount() + " films");
            }
            ViewGroup filmList = (AmazingListView) findViewById(R.id.film_list);
            filmList.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Log.i(FilmListActivity.TAG, "Item clicked: pos" + position + "/id" + id + " viewId: " + view.getId());
                    FilmListActivity.this.openFilmInfo(id);
                }
            });
            if (filmList.getAdapter() == null) {
                filmList.addFooterView(getLayoutInflater().inflate(R.layout.logo_footer, null), null, false);
                filmList.setFooterDividersEnabled(false);
                this.mAdapter = new FilmListCursorAdapter(indexerType, this, filmsCursor, new Handler() {
                    public void handleMessage(Message msg) {
                        if (msg.what == Constants.FILM_LIST_MSG_TRAILERCLICK) {
                            if (msg.obj == null) {
                                FilmListActivity.this.openFilmInfo((long) msg.arg1);
                            } else {
                                FilmListActivity.this.openTrailer(msg.arg1, (String) msg.obj);
                            }
                        }
                    }
                });
                if (singleSectionIndexerSectionName != null) {
                    this.mAdapter.setSingleSectionIndexerSectionName(singleSectionIndexerSectionName);
                }
                this.mAdapter.setFilterMode(adapterFilterMode);
                filmList.setAdapter(this.mAdapter);
            }
            filmList.setFastScrollEnabled(fastScrollEnabled);
            filmList.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.list_row_header, filmList, false));
        }
    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume " + getIntent());
        if (checkConnectivityOrPreCachedInitDataAvailable()) {
            Log.i(TAG, "Checked conn");
            this.appInitBroadCastFilter = new IntentFilter(Constants.ACTION_APPINIT_STATUS);
            registerReceiver(this.appInitBroadCastReceiver, this.appInitBroadCastFilter);
            if (hasInternetConnection()) {
                startService(new Intent(this, AppInitUpdateService.class));
            }
            configureExtraNavButton();
            if (getCinemaHeader() != null && this.cinemaData != null) {
                this.cinemaData.requery();
                if (this.cinemaData.moveToFirst()) {
                    getCinemaHeader().setDataInView(this, this.cinemaData);
                }
            }
        }
    }

    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        if (this.appInitBroadCastReceiver != null) {
            try {
                unregisterReceiver(this.appInitBroadCastReceiver);
            } catch (Throwable e) {
                Log.w(TAG, "Failed to unregister appInit broadcast receveiver", e);
            }
        }
        hideProgress(true);
    }

    public void onRecycled() {
        Log.d(TAG, "onRecycled " + getIntent());
        super.onRecycled();
        configureExtraNavButton();
        if (getCinemaHeader() != null && this.cinemaData != null) {
            this.cinemaData.requery();
            if (this.cinemaData.moveToFirst()) {
                getCinemaHeader().setDataInView(this, this.cinemaData);
            }
        }
    }

    public void setTaskResult(FilterFilms taskResult) {
        hideProgress(true);
        if (taskResult == null) {
            showAlert(null);
        } else if (taskResult.errorText != null) {
            showAlert(taskResult.errorText);
        } else {
            initFilter(taskResult);
        }
    }

    private void initFilter(FilterFilms filmListScheduleData) {
        if (this.cinemaId > 0) {
            LinearLayout filmListFilter = (LinearLayout) findViewById(R.id.filmListFilter);
            if (filmListFilter != null && filmListScheduleData != null) {
                filmListFilter.setVisibility(0);
                if (this.filmListFilterCurrent != null) {
                    if (filmListScheduleData.filmIdsCurrent.length > 0) {
                        this.filmListFilterCurrent.setEnabled(true);
                    } else {
                        this.filmListFilterCurrent.setEnabled(false);
                    }
                    this.filmListFilterCurrent.setOnClickListener(new AnonymousClass4(filmListScheduleData));
                }
                if (this.filmListFilterNext != null) {
                    if (filmListScheduleData.filmIdsNext.length > 0) {
                        this.filmListFilterNext.setEnabled(true);
                    } else {
                        this.filmListFilterNext.setEnabled(false);
                    }
                    this.filmListFilterNext.setOnClickListener(new AnonymousClass5(filmListScheduleData));
                }
                if (this.filmListFilterAdvanced != null) {
                    if (filmListScheduleData.filmIdsAdvanced.length > 0) {
                        this.filmListFilterAdvanced.setEnabled(true);
                    } else {
                        this.filmListFilterAdvanced.setEnabled(false);
                    }
                    this.filmListFilterAdvanced.setOnClickListener(new AnonymousClass6(filmListScheduleData));
                }
                if (this.filmListFilterCurrent.isEnabled()) {
                    this.filmListFilterCurrent.performClick();
                } else if (this.filmListFilterNext.isEnabled()) {
                    this.filmListFilterNext.performClick();
                } else if (this.filmListFilterAdvanced.isEnabled()) {
                    this.filmListFilterAdvanced.performClick();
                }
            }
        }
    }

    private void openFilmInfo(long filmId) {
        if (filmId > 0) {
            Intent filmInfo = new Intent(getParent(), FilmInfoActivity.class).setAction("android.intent.action.VIEW").setDataAndType(ContentUris.withAppendedId(FilmDetailsColumns.CONTENT_URI, filmId), FilmDetailsColumns.CONTENT_ITEM_TYPE);
            filmInfo.putExtra(Constants.EXTRA_CINEMA_ID, this.cinemaId);
            startSubActivity(filmInfo, "Film Details");
            return;
        }
        Log.w(TAG, "Clicked item in film list has no valid film id #" + filmId);
    }

    private void openTrailer(int filmId, String trailerURL) {
        Intent filmTrailerIntent = new Intent(getParent(), FilmTrailerActivity.class).setAction("android.intent.action.VIEW").setDataAndType(ContentUris.withAppendedId(FilmDetailsColumns.CONTENT_URI, (long) filmId), FilmDetailsColumns.CONTENT_ITEM_TYPE);
        filmTrailerIntent.putExtra(Constants.EXTRA_CINEMA_ID, this.cinemaId);
        filmTrailerIntent.putExtra(Constants.EXTRA_FILM_ID, filmId);
        filmTrailerIntent.putExtra(FilmColumns.TRAILER_URL, trailerURL);
        startActivity(filmTrailerIntent);
    }

    protected boolean configureExtraNavButton(NavigatorBarActivity navBar) {
        if (!navBar.isRootActivity(getIntent()) || !ODEONApplication.hasSystemGoogleMapsInstalled(this)) {
            return false;
        }
        navBar.registerExtraButton("Map", new OnClickListener() {
            public void onClick(View v) {
                String cinemaName = "";
                if (FilmListActivity.this.cinemaId > 0 && FilmListActivity.this.cinemaData != null) {
                    cinemaName = FilmListActivity.this.cinemaData.getString(FilmListActivity.this.cinemaData.getColumnIndex(SiteColumns.NAME));
                }
                ODEONApplication.trackEvent("Map", "Click", cinemaName);
                FilmListActivity.this.startSubActivity(new Intent(FilmListActivity.this.getParent(), CinemaMapActivity.class).setData(ContentUris.withAppendedId(SiteColumns.CONTENT_URI, (long) FilmListActivity.this.cinemaId)), "Cinema Map");
            }
        });
        return true;
    }

    protected Cursor getFilmCursorFilter(String[] filmIds) {
        String selection = "hidden=0 AND FilmInSite.siteID=?";
        String[] selectionArgs = new String[(filmIds.length + 1)];
        selectionArgs[0] = String.valueOf(this.cinemaId);
        if (filmIds.length > 0) {
            selection = new StringBuilder(String.valueOf(selection)).append(" AND Film._id IN (").append(makePlaceholders(filmIds.length)).append(")").toString();
            System.arraycopy(filmIds, 0, selectionArgs, 1, filmIds.length);
        }
        return managedQuery(FilmColumns.CONTENT_URI, null, selection, selectionArgs, FilmColumns.DEFAULT_SORT_ORDER);
    }

    private String makePlaceholders(int len) {
        if (len < 1) {
            throw new RuntimeException("No placeholders");
        }
        StringBuilder sb = new StringBuilder((len * 2) - 1);
        sb.append("?");
        for (int i = 1; i < len; i++) {
            sb.append(",?");
        }
        return sb.toString();
    }
}
