package uk.co.odeon.androidapp.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.adapters.RewardsMyFilmsListAdapter;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity.RootActivity;
import uk.co.odeon.androidapp.custom.NavigatorBarSubActivity;
import uk.co.odeon.androidapp.json.Rewards;
import uk.co.odeon.androidapp.model.FilmListMyFilm;
import uk.co.odeon.androidapp.model.FilmListMyFilm.FilmCategory;
import uk.co.odeon.androidapp.provider.FilmContent.FilmColumns;
import uk.co.odeon.androidapp.provider.FilmContent.FilmDetailsColumns;
import uk.co.odeon.androidapp.provider.OfferContent.OfferColumns;
import uk.co.odeon.androidapp.task.RewardsTaskCached;
import uk.co.odeon.androidapp.task.TaskTarget;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;

public class RewardsMyFilmsActivity extends NavigatorBarSubActivity implements TaskTarget<Rewards> {
    private static /* synthetic */ int[] $SWITCH_TABLE$uk$co$odeon$androidapp$model$FilmListMyFilm$FilmCategory;
    protected static final String TAG;
    protected AmazingListView filmList;
    protected RewardsMyFilmsListAdapter filmListAdapter;

    /* renamed from: uk.co.odeon.androidapp.activity.RewardsMyFilmsActivity.4 */
    class AnonymousClass4 implements OnClickListener {
        private final /* synthetic */ AlertDialog val$alertDialog;

        AnonymousClass4(AlertDialog alertDialog) {
            this.val$alertDialog = alertDialog;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$alertDialog.hide();
        }
    }

    static /* synthetic */ int[] $SWITCH_TABLE$uk$co$odeon$androidapp$model$FilmListMyFilm$FilmCategory() {
        int[] iArr = $SWITCH_TABLE$uk$co$odeon$androidapp$model$FilmListMyFilm$FilmCategory;
        if (iArr == null) {
            iArr = new int[FilmCategory.values().length];
            try {
                iArr[FilmCategory.bookedAndNotRated.ordinal()] = 2;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[FilmCategory.bookedAndRated.ordinal()] = 3;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[FilmCategory.otherRatedFilms.ordinal()] = 4;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[FilmCategory.recommended.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            $SWITCH_TABLE$uk$co$odeon$androidapp$model$FilmListMyFilm$FilmCategory = iArr;
        }
        return iArr;
    }

    static {
        TAG = RewardsMyFilmsActivity.class.getSimpleName();
    }

    public AmazingListView getFilmList() {
        if (this.filmList == null) {
            this.filmList = (AmazingListView) findViewById(R.id.rewards_my_films_list);
        }
        return this.filmList;
    }

    public RewardsMyFilmsListAdapter getFilmListAdapter() {
        if (this.filmListAdapter == null) {
            this.filmListAdapter = new RewardsMyFilmsListAdapter(this, 0, new ArrayList(), new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.what == Constants.FILM_LIST_MSG_TRAILERCLICK) {
                        if (msg.obj == null) {
                            RewardsMyFilmsActivity.this.openFilmInfo((long) msg.arg1);
                        } else {
                            RewardsMyFilmsActivity.this.openTrailer(msg.arg1, (String) msg.obj);
                        }
                    }
                }
            });
        }
        return this.filmListAdapter;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewards_my_films);
        getFilmList().setAdapter(getFilmListAdapter());
        getFilmList().setFastScrollEnabled(true);
        getFilmList().setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.list_row_header, getFilmList(), false));
        getFilmList().addFooterView(getLayoutInflater().inflate(R.layout.logo_footer, null), null, false);
        getFilmList().setFooterDividersEnabled(false);
        getFilmList().setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position < RewardsMyFilmsActivity.this.getFilmList().getCount()) {
                    FilmListMyFilm f = (FilmListMyFilm) RewardsMyFilmsActivity.this.getFilmList().getItemAtPosition(position);
                    Log.i(RewardsMyFilmsActivity.TAG, "Item clicked: pos" + position + "/id" + id + " viewId: " + view.getId() + " filmId: " + f.filmId);
                    if (f.filmId != null) {
                        RewardsMyFilmsActivity.this.openFilmInfo((long) f.filmId.intValue());
                        return;
                    }
                    return;
                }
                Log.i(RewardsMyFilmsActivity.TAG, "Item clicked: pos" + position + "/id" + id + " viewId: " + view.getId() + " was not found in film list.");
            }
        });
    }

    public void onResume() {
        super.onResume();
        if (ODEONApplication.getInstance().hasCustomerLoginInPrefs()) {
            refreshAmazingList();
        } else {
            onBackPressed();
        }
    }

    public void onRecycled() {
        super.onRecycled();
        if (ODEONApplication.getInstance().hasCustomerLoginInPrefs()) {
            refreshAmazingList();
        } else {
            onBackPressed();
        }
    }

    protected void refreshAmazingList() {
        ODEONApplication app = ODEONApplication.getInstance();
        new RewardsTaskCached(this).execute(new String[]{app.getCustomerLoginEmail(), app.getCustomerLoginPassword(), null});
        showCancelableProgress(R.string.booking_progress, new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                RewardsMyFilmsActivity.this.hideProgress(true);
                RewardsMyFilmsActivity.this.onBackPressed();
            }
        }, true);
    }

    public void setTaskResult(Rewards taskResult) {
        hideProgress(true);
        if (taskResult == null) {
            showAlert(getString(R.string.rewards_error_text));
        } else if (taskResult.hasError()) {
            showAlert(taskResult.getError());
        } else {
            getFilmListAdapter().refreshList(getFilms(taskResult));
        }
    }

    protected void showAlert(String msg) {
        AlertDialog alertDialog = new Builder(getDialogContext()).create();
        alertDialog.setTitle(getString(R.string.rewards_error_title));
        alertDialog.setMessage(msg);
        alertDialog.setButton(getString(R.string.rewards_error_ok), new AnonymousClass4(alertDialog));
        alertDialog.show();
    }

    protected List<FilmListMyFilm> getFilms(Rewards rewardsData) {
        ArrayList<FilmListMyFilm> films = new ArrayList();
        for (FilmCategory cat : FilmCategory.values()) {
            Cursor filmsCursor = null;
            List<Integer> filmIds = null;
            switch ($SWITCH_TABLE$uk$co$odeon$androidapp$model$FilmListMyFilm$FilmCategory()[cat.ordinal()]) {
                case AmazingListView.PINNED_HEADER_VISIBLE /*1*/:
                    filmsCursor = managedQuery(FilmColumns.CONTENT_URI, null, " recommended=1 ", null, FilmColumns.DEFAULT_SORT_ORDER);
                    break;
                case AmazingListView.PINNED_HEADER_PUSHED_UP /*2*/:
                    filmIds = rewardsData.getFilmsBookedAndNotRated();
                    break;
                case RootActivity.TYPE_RIGHT /*3*/:
                    filmIds = rewardsData.getFilmsBookedAndRated();
                    break;
                case RootActivity.TYPE_SINGLE /*4*/:
                    filmIds = rewardsData.getOtherRatedFilms();
                    break;
            }
            if (filmIds != null) {
                filmsCursor = queryFilmIds(filmIds);
            }
            Log.d(TAG, "Found " + (filmsCursor == null ? 0 : filmsCursor.getCount()) + " films for category " + cat);
            convertFilmsFromCursor(filmsCursor, films, cat);
            Log.d(TAG, "New film count: " + films.size());
        }
        return films;
    }

    protected Cursor queryFilmIds(List<Integer> filmIds) {
        if (filmIds == null || (filmIds != null && filmIds.isEmpty())) {
            return null;
        }
        String filmIdsStr = null;
        for (Integer intValue : filmIds) {
            int filmId = intValue.intValue();
            if (filmIdsStr == null) {
                filmIdsStr = String.valueOf(filmId);
            } else {
                filmIdsStr = new StringBuilder(String.valueOf(filmIdsStr)).append(",").append(filmId).toString();
            }
        }
        return managedQuery(FilmColumns.CONTENT_URI, null, " _id IN ( " + filmIdsStr + ")", null, FilmColumns.DEFAULT_SORT_ORDER);
    }

    protected boolean convertFilmsFromCursor(Cursor filmsCursor, ArrayList<FilmListMyFilm> films, FilmCategory cat) {
        boolean hasFilms = false;
        if (filmsCursor != null && filmsCursor.moveToFirst()) {
            while (!filmsCursor.isAfterLast()) {
                films.add(convertFilm(filmsCursor, cat));
                filmsCursor.moveToNext();
                hasFilms = true;
            }
        }
        if (!hasFilms) {
            films.add(new FilmListMyFilm(cat));
        }
        return hasFilms;
    }

    protected FilmListMyFilm convertFilm(Cursor filmsCursor, FilmCategory cat) {
        boolean z;
        boolean z2 = false;
        Integer valueOf = Integer.valueOf(filmsCursor.getInt(filmsCursor.getColumnIndex("_id")));
        String string = filmsCursor.getString(filmsCursor.getColumnIndex(OfferColumns.TITLE));
        String string2 = filmsCursor.getString(filmsCursor.getColumnIndex(FilmColumns.TRAILER_URL));
        String string3 = filmsCursor.getString(filmsCursor.getColumnIndex(OfferColumns.IMAGE_URL));
        String string4 = filmsCursor.getString(filmsCursor.getColumnIndex(FilmColumns.CERTIFICATE));
        if (filmsCursor.getInt(filmsCursor.getColumnIndex(FilmColumns.RATEABLE)) == 1) {
            z = true;
        } else {
            z = false;
        }
        float f = (float) filmsCursor.getInt(filmsCursor.getColumnIndex(FilmColumns.HALFRATING));
        String string5 = filmsCursor.getString(filmsCursor.getColumnIndex(FilmColumns.GENRE));
        String string6 = filmsCursor.getString(filmsCursor.getColumnIndex(FilmColumns.RELDATE));
        if (filmsCursor.getInt(filmsCursor.getColumnIndex(FilmColumns.BBF)) == 1) {
            z2 = true;
        }
        return new FilmListMyFilm(cat, valueOf, string, string2, string3, string4, z, f, string5, string6, z2);
    }

    private void openFilmInfo(long filmId) {
        startSubActivity(new Intent(getParent(), FilmInfoActivity.class).setAction("android.intent.action.VIEW").setDataAndType(ContentUris.withAppendedId(FilmDetailsColumns.CONTENT_URI, filmId), FilmDetailsColumns.CONTENT_ITEM_TYPE), "Film Details");
    }

    private void openTrailer(int filmId, String trailerURL) {
        Intent filmTrailerIntent = new Intent(getParent(), FilmTrailerActivity.class).setAction("android.intent.action.VIEW").setDataAndType(ContentUris.withAppendedId(FilmDetailsColumns.CONTENT_URI, (long) filmId), FilmDetailsColumns.CONTENT_ITEM_TYPE);
        filmTrailerIntent.putExtra(Constants.EXTRA_FILM_ID, filmId);
        filmTrailerIntent.putExtra(FilmColumns.TRAILER_URL, trailerURL);
        startSubActivity(filmTrailerIntent, "Film Trailer");
    }
}
