package uk.co.odeon.androidapp.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ViewSwitcher;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.Constants.APP_LOCATION;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.adapters.CinemaListAdapterTools.CinemaListMode;
import uk.co.odeon.androidapp.adapters.CinemaListCursorAdapter;
import uk.co.odeon.androidapp.custom.FilmHeaderView;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity;
import uk.co.odeon.androidapp.custom.NavigatorBarSubActivity;
import uk.co.odeon.androidapp.provider.SiteContent.SiteColumns;
import uk.co.odeon.androidapp.sitedistance.SiteDistance;
import uk.co.odeon.androidapp.sitedistance.SiteDistanceForPostCodeTask;
import uk.co.odeon.androidapp.sitedistance.SiteDistanceResult;
import uk.co.odeon.androidapp.sitedistance.SiteDistanceTask;
import uk.co.odeon.androidapp.task.TaskTarget;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;

public class CinemaListActivity extends NavigatorBarSubActivity implements TaskTarget<SiteDistanceResult> {
    protected static final String TAG;
    private OnItemClickListener cinemaClickListener;
    private Data data;
    private boolean justCreated;
    private OnEditorActionListener postCodeSearchListener;
    private Handler siteDistanceNotificationHandler;

    /* renamed from: uk.co.odeon.androidapp.activity.CinemaListActivity.4 */
    class AnonymousClass4 implements OnClickListener {
        private final /* synthetic */ AlertDialog val$alertDialog;

        AnonymousClass4(AlertDialog alertDialog) {
            this.val$alertDialog = alertDialog;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$alertDialog.hide();
        }
    }

    private class Data {
        public boolean distanceMode;
        public Cursor filmData;
        public int filmId;
        public boolean postCodeMode;
        public SiteDistanceForPostCodeTask siteDistanceForPostCodeTask;
        public SiteDistanceTask siteDistanceTask;
        public Cursor sitesCursor;

        private Data() {
            this.filmData = null;
            this.sitesCursor = null;
            this.filmId = 0;
            this.siteDistanceForPostCodeTask = null;
            this.siteDistanceTask = null;
            this.distanceMode = true;
            this.postCodeMode = false;
        }
    }

    public CinemaListActivity() {
        this.justCreated = true;
        this.cinemaClickListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (!(CinemaListActivity.this.getParent() instanceof NavigatorBarActivity)) {
                    return;
                }
                if (CinemaListActivity.this.data.filmId > 0) {
                    Intent filmSchedule = new Intent(CinemaListActivity.this.getParent(), FilmScheduleActivity.class);
                    filmSchedule.putExtra(Constants.EXTRA_FILM_ID, CinemaListActivity.this.data.filmId);
                    filmSchedule.putExtra(Constants.EXTRA_CINEMA_ID, (int) id);
                    CinemaListActivity.this.startSubActivity(filmSchedule, "Showtimes");
                    return;
                }
                Intent cinemaDetails = new Intent(CinemaListActivity.this.getParent(), FilmListActivity.class);
                cinemaDetails.putExtra(Constants.EXTRA_CINEMA_ID, (int) id);
                CinemaListActivity.this.startSubActivity(cinemaDetails, "Cinema Details");
            }
        };
        this.postCodeSearchListener = new OnEditorActionListener() {

            /* renamed from: uk.co.odeon.androidapp.activity.CinemaListActivity.2.1 */
            class AnonymousClass1 implements Runnable {
                private final /* synthetic */ String val$postCode;

                AnonymousClass1(String str) {
                    this.val$postCode = str;
                }

                public void run() {
                    CinemaListActivity.this.data.siteDistanceForPostCodeTask = new SiteDistanceForPostCodeTask(CinemaListActivity.this);
                    CinemaListActivity.this.data.siteDistanceForPostCodeTask.execute(new String[]{this.val$postCode});
                }
            }

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                EditText postCodeEd = (EditText) CinemaListActivity.this.findViewById(R.id.cinema_list_post_code_search_ed);
                CinemaListActivity.this.hideKeyboard(postCodeEd);
                String postCode = postCodeEd.getText().toString();
                if (postCode == null || postCode.trim().length() <= 0) {
                    CinemaListActivity.this.data.distanceMode = true;
                    CinemaListActivity.this.data.postCodeMode = false;
                    CinemaListActivity.this.setupSitesList(true);
                } else {
                    CinemaListActivity.this.runOnUiThread(new AnonymousClass1(postCode));
                    if (ODEONApplication.getInstance().getChoosenLocation().equals(APP_LOCATION.ire)) {
                        CinemaListActivity.this.showProgress(R.string.cinema_list_progress_postcode_lookup_ire, true);
                    } else {
                        CinemaListActivity.this.showProgress(R.string.cinema_list_progress_postcode_lookup, true);
                    }
                }
                return true;
            }
        };
        this.siteDistanceNotificationHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.obj == null) {
                    CinemaListActivity.this.switchViewToNoLocation();
                    CinemaListActivity.this.setupLocationButton(false);
                } else if (SiteDistance.getInstance().isFreshDataAvailableForCurrentLocation()) {
                    CinemaListActivity.this.switchViewToList();
                    CinemaListActivity.this.setupLocationButton(true);
                }
            }
        };
    }

    static {
        TAG = CinemaListActivity.class.getSimpleName();
    }

    public void setTaskResult(SiteDistanceResult taskResult) {
        boolean z = false;
        hideProgress(true);
        if (taskResult == null) {
            AlertDialog alertDialog = new Builder(getDialogContext()).create();
            alertDialog.setTitle(getResources().getString(R.string.cinema_list_postcode_failed_title));
            if (ODEONApplication.getInstance().getChoosenLocation().equals(APP_LOCATION.ire)) {
                alertDialog.setMessage(getResources().getString(R.string.cinema_list_postcode_failed_msg_ire));
            } else {
                alertDialog.setMessage(getResources().getString(R.string.cinema_list_postcode_failed_msg));
            }
            alertDialog.setButton(getResources().getString(R.string.cinema_list_postcode_failed_ok), new AnonymousClass4(alertDialog));
            alertDialog.show();
            ((EditText) findViewById(R.id.cinema_list_post_code_search_ed)).setText("");
            this.data.postCodeMode = false;
            return;
        }
        this.data.distanceMode = true;
        Data data = this.data;
        if (taskResult.postCode != null) {
            z = true;
        }
        data.postCodeMode = z;
        setupSitesList(true);
        switchViewToList();
    }

    private void switchViewToList() {
        ViewSwitcher v = (ViewSwitcher) findViewById(R.id.cinema_list_viewswitcher);
        while (v.getCurrentView().getId() != R.id.cinema_list) {
            v.showPrevious();
        }
    }

    private void switchViewToNoLocation() {
        ViewSwitcher v = (ViewSwitcher) findViewById(R.id.cinema_list_viewswitcher);
        while (v.getCurrentView().getId() != R.id.cinema_list_noloc) {
            v.showNext();
        }
        setupLocationSettingsButton();
    }

    private void hideKeyboard(EditText postCodeEd) {
        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(postCodeEd.getWindowToken(), 0);
    }

    private AmazingListView setupSitesList(boolean forceNew) {
        CinemaListMode listMode;
        AmazingListView siteList = (AmazingListView) findViewById(R.id.cinema_list);
        siteList.setFastScrollEnabled(false);
        String selection = "1";
        String[] selectionArgs = null;
        if (this.data.filmId > 0) {
            selection = "FilmInSite.filmMasterID=?";
            selectionArgs = new String[]{String.valueOf(this.data.filmId)};
        }
        if (forceNew || this.data.sitesCursor == null) {
            String sortField;
            String sortFavs = "IFNULL(SiteFavourite._id,0) DESC";
            if (this.data.distanceMode) {
                sortField = new StringBuilder(String.valueOf(sortFavs)).append(", ").append(this.data.postCodeMode ? "distanceFromPostcode ASC" : "distanceFromGPS ASC").toString();
            } else {
                sortField = new StringBuilder(String.valueOf(sortFavs)).append(", ").append(SiteColumns.NAME).toString();
            }
            this.data.sitesCursor = managedQuery(SiteColumns.CONTENT_URI, null, selection, selectionArgs, sortField);
        } else {
            Log.d(TAG, "Using retained sites list");
        }
        Log.d(TAG, "Found " + this.data.sitesCursor.getCount() + " sites");
        if (this.data.distanceMode) {
            listMode = CinemaListMode.DISTANCE;
        } else {
            listMode = SiteDistance.getInstance().isFreshDataAvailableForCurrentLocation() ? CinemaListMode.ALPHA_WITH_DISTANCE : CinemaListMode.ALPHA;
        }
        CinemaListCursorAdapter sitesAdapter = new CinemaListCursorAdapter(listMode, this.data.postCodeMode, this, this.data.sitesCursor);
        siteList.setOnItemClickListener(this.cinemaClickListener);
        siteList.setAdapter(sitesAdapter);
        siteList.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.list_row_header, siteList, false));
        if (!this.data.distanceMode) {
            siteList.setFastScrollEnabled(true);
        }
        return siteList;
    }

    private void setupPostCodeSearch() {
        EditText postCodeEd = (EditText) findViewById(R.id.cinema_list_post_code_search_ed);
        if (postCodeEd != null) {
            postCodeEd.setEnabled(true);
            postCodeEd.setFocusable(true);
            postCodeEd.setFocusableInTouchMode(true);
            postCodeEd.setOnEditorActionListener(this.postCodeSearchListener);
        }
    }

    private void hidePostCodeSearch() {
        ViewGroup postCodeSearch = (ViewGroup) findViewById(R.id.post_code_search_header);
        postCodeSearch.setVisibility(8);
        postCodeSearch.setEnabled(false);
    }

    private void setupLocationButton(boolean enabled) {
        Button locBtn = (Button) findViewById(R.id.cinema_list_location_btn);
        if (locBtn != null) {
            if (enabled) {
                locBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CinemaListActivity.this.data.siteDistanceTask = new SiteDistanceTask(CinemaListActivity.this);
                        CinemaListActivity.this.data.siteDistanceTask.execute(new Void[0]);
                        EditText postCodeEd = (EditText) CinemaListActivity.this.findViewById(R.id.cinema_list_post_code_search_ed);
                        postCodeEd.setText("");
                        postCodeEd.clearFocus();
                        CinemaListActivity.this.findViewById(R.id.cinema_list_dummyfocusable).requestFocus();
                        CinemaListActivity.this.hideKeyboard(postCodeEd);
                    }
                });
            }
            locBtn.setEnabled(enabled);
        }
    }

    private void setupLocationSettingsButton() {
        Button locBtn = (Button) findViewById(R.id.cinema_list_noloc_gpsettings_btn);
        if (locBtn != null) {
            locBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CinemaListActivity.this.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                }
            });
        }
    }

    protected boolean parseIntentDistanceMode() {
        Intent i = getIntent();
        if (Constants.ACTION_CINEMALIST_CLOSETOME.equals(i.getAction())) {
            return true;
        }
        if (!Constants.ACTION_CINEMALIST_AUTOPICK.equals(i.getAction())) {
            return false;
        }
        boolean distanceMode = SiteDistance.getInstance().isLocationKnown() && SiteDistance.getInstance().isFreshDataAvailableForCurrentLocation();
        return distanceMode;
    }

    public void onRecycled() {
        Log.d(TAG, "************* onRecycled");
        super.onRecycled();
        if (this.data != null) {
            setupSitesList(true);
        }
    }

    public Object onRetainNonConfigurationInstance() {
        if (this.data.siteDistanceForPostCodeTask != null) {
            this.data.siteDistanceForPostCodeTask.detach();
        }
        if (this.data.siteDistanceTask != null) {
            this.data.siteDistanceTask.detach();
        }
        if (this.data.sitesCursor != null) {
            stopManagingCursor(this.data.sitesCursor);
        }
        return this.data;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume " + getIntent() + " distanceMode=" + this.data.distanceMode + " postCodeMode=" + this.data.postCodeMode);
        boolean isLocationKnown = SiteDistance.getInstance().isLocationKnown();
        if (this.data.distanceMode) {
            if (!isLocationKnown) {
                switchViewToNoLocation();
            }
            SiteDistance.getInstance().addNotificationHandler(this.siteDistanceNotificationHandler);
            if (!(!isLocationKnown || this.data.postCodeMode || this.justCreated)) {
                this.data.siteDistanceTask = new SiteDistanceTask(this);
                this.data.siteDistanceTask.execute(new Void[0]);
            }
        }
        setupLocationButton(isLocationKnown);
        this.justCreated = false;
    }

    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        SiteDistance.getInstance().removeNotificationHandler(this.siteDistanceNotificationHandler);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate " + getIntent());
        setContentView(R.layout.cinema_list);
        EditText searchEdit = (EditText) findViewById(R.id.cinema_list_post_code_search_ed);
        if (searchEdit != null) {
            if (ODEONApplication.getInstance().getChoosenLocation().equals(APP_LOCATION.ire)) {
                searchEdit.setHint(R.string.cinema_list_postcode_header_search_hint_ire);
            } else {
                searchEdit.setHint(R.string.cinema_list_postcode_header_search_hint);
            }
        }
        TextView noLoc = (TextView) findViewById(R.id.cinema_list_noloc_blah1);
        if (noLoc != null) {
            if (ODEONApplication.getInstance().getChoosenLocation().equals(APP_LOCATION.ire)) {
                noLoc.setText(R.string.cinema_list_postcode_no_loc_ire);
            } else {
                noLoc.setText(R.string.cinema_list_postcode_no_loc);
            }
        }
        this.data = (Data) getLastNonConfigurationInstance();
        if (this.data == null) {
            this.data = new Data();
            this.data.filmId = getIntent().getIntExtra(Constants.EXTRA_FILM_ID, 0);
            this.data.filmData = ODEONApplication.getInstance().getFilmDataCursor(this, this.data.filmId);
        } else {
            if (this.data.siteDistanceForPostCodeTask != null) {
                this.data.siteDistanceForPostCodeTask.attach(this);
                this.data.postCodeMode = true;
            }
            if (this.data.siteDistanceTask != null) {
                this.data.siteDistanceTask.attach(this);
            }
            if (this.data.sitesCursor != null) {
                startManagingCursor(this.data.sitesCursor);
                this.data.sitesCursor.requery();
            }
            if (this.data.filmData != null) {
                startManagingCursor(this.data.filmData);
                this.data.filmData.requery();
            }
        }
        this.data.distanceMode = parseIntentDistanceMode();
        setupSitesList(this.data.postCodeMode);
        if (this.data.distanceMode) {
            setupPostCodeSearch();
        } else {
            hidePostCodeSearch();
        }
        FilmHeaderView filmHeader = (FilmHeaderView) findViewById(R.id.filmHeaderLayout);
        if (filmHeader != null) {
            filmHeader.setDataInView(this, this.data.filmData);
        }
    }
}
