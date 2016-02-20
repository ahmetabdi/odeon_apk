package uk.co.odeon.androidapp.activity;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import java.util.ArrayList;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.adapters.MyOdeonListAdapter;
import uk.co.odeon.androidapp.custom.NavigatorBarSubActivity;
import uk.co.odeon.androidapp.model.FilmListFilm;
import uk.co.odeon.androidapp.model.MyOdeonData;
import uk.co.odeon.androidapp.model.MyOdeonData.Type;
import uk.co.odeon.androidapp.provider.FilmContent.FilmColumns;
import uk.co.odeon.androidapp.provider.FilmContent.FilmDetailsColumns;
import uk.co.odeon.androidapp.provider.OfferContent.OfferColumns;
import uk.co.odeon.androidapp.provider.SiteContent.SiteColumns;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;

public class MyOdeonActivity extends NavigatorBarSubActivity {
    protected static final String TAG;
    protected AmazingListView myOdeonList;
    protected MyOdeonListAdapter myOdeonListAdapter;
    protected Button navigationHeaderEditButton;

    static {
        TAG = MyOdeonActivity.class.getSimpleName();
    }

    public AmazingListView getMyOdeonList() {
        if (this.myOdeonList == null) {
            this.myOdeonList = (AmazingListView) findViewById(R.id.my_odeon_list);
        }
        return this.myOdeonList;
    }

    public MyOdeonListAdapter getMyOdeonListAdapter() {
        if (this.myOdeonListAdapter == null) {
            this.myOdeonListAdapter = new MyOdeonListAdapter(this, 0, new ArrayList(), new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.what == Constants.FILM_LIST_MSG_TRAILERCLICK) {
                        if (msg.obj == null) {
                            MyOdeonActivity.this.openFilmInfo((long) msg.arg1);
                        } else {
                            MyOdeonActivity.this.openTrailer(msg.arg1, (String) msg.obj);
                        }
                    } else if (msg.what == Constants.FILM_LIST_MSG_EMPTY) {
                        MyOdeonActivity.this.handleNavigationHeaderEditButton(false);
                    }
                }
            });
        }
        return this.myOdeonListAdapter;
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_odeon);
        configureNavigationHeader();
        getMyOdeonList().setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i(MyOdeonActivity.TAG, "Item clicked: pos" + position + "/id" + id + " viewId: " + view.getId());
                if (id > 0) {
                    MyOdeonData mod = (MyOdeonData) ((MyOdeonListAdapter) MyOdeonActivity.this.getMyOdeonList().getAdapter()).getItem(position);
                    if (mod == null) {
                        return;
                    }
                    if (mod.type.equals(Type.cinema)) {
                        MyOdeonActivity.this.openCinemaInfo(id);
                    } else if (mod.type.equals(Type.film)) {
                        MyOdeonActivity.this.openFilmInfo(id);
                    }
                }
            }
        });
        getMyOdeonList().addFooterView(getLayoutInflater().inflate(R.layout.logo_footer, null), null, false);
        getMyOdeonList().setFooterDividersEnabled(false);
        getMyOdeonList().setAdapter(getMyOdeonListAdapter());
        getMyOdeonList().setFastScrollEnabled(true);
        getMyOdeonList().setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.list_row_header, getMyOdeonList(), false));
    }

    public void onResume() {
        super.onResume();
        refreshAmazingList();
    }

    public void onRecycled() {
        super.onRecycled();
        refreshAmazingList();
    }

    protected void refreshAmazingList() {
        handleNavigationHeaderEditButton(false);
        ArrayList<MyOdeonData> myOdeonDataList = new ArrayList();
        myOdeonDataList.addAll(readOfferData());
        myOdeonDataList.addAll(readFavouriteCinemaData());
        myOdeonDataList.addAll(readFavouriteFilmData());
        getMyOdeonListAdapter().refreshList(myOdeonDataList);
    }

    protected void configureNavigationHeader() {
        View header = getLayoutInflater().inflate(R.layout.header_cancel_ok, null, false);
        configureNavigationHeaderTitle(header, R.string.myodeon_header_title);
        this.navigationHeaderEditButton = (Button) header.findViewById(R.id.navigationHeaderCancel);
        configureNavigationHeaderCancel(header, "Edit", R.drawable.nav_bar_btn_4_round, new OnClickListener() {
            public void onClick(View v) {
                MyOdeonActivity.this.switchNavigationHeaderEditButton();
            }
        });
        inflateCustomHeader(header);
    }

    protected void handleNavigationHeaderEditButton(boolean visible) {
        if (this.navigationHeaderEditButton == null) {
            return;
        }
        if (visible) {
            this.navigationHeaderEditButton.setVisibility(0);
            return;
        }
        if (this.navigationHeaderEditButton.getText().equals(getString(R.string.edit_button_done))) {
            switchNavigationHeaderEditButton();
        }
        this.navigationHeaderEditButton.setVisibility(4);
    }

    protected void switchNavigationHeaderEditButton() {
        getMyOdeonListAdapter().switchDeletable();
        this.navigationHeaderEditButton.setText(this.navigationHeaderEditButton.getText().equals(getString(R.string.edit_button_edit)) ? getString(R.string.edit_button_done) : getString(R.string.edit_button_edit));
    }

    protected ArrayList<MyOdeonData> readOfferData() {
        ArrayList<MyOdeonData> offerDataList = new ArrayList();
        Cursor offersCursor = managedQuery(OfferColumns.CONTENT_URI, null, null, null, null);
        if (offersCursor != null) {
            Log.d(TAG, "Found " + offersCursor.getCount() + " offers");
            if (offersCursor.moveToFirst()) {
                while (!offersCursor.isAfterLast()) {
                    MyOdeonData offer = new MyOdeonData();
                    offer.type = Type.offer;
                    offer.values.put("_id", Integer.valueOf(offersCursor.getInt(offersCursor.getColumnIndex("_id"))));
                    offer.values.put(OfferColumns.TITLE, offersCursor.getString(offersCursor.getColumnIndex(OfferColumns.TITLE)));
                    offer.values.put(OfferColumns.TEXT, offersCursor.getString(offersCursor.getColumnIndex(OfferColumns.TEXT)));
                    offer.values.put(OfferColumns.IMAGE_URL, offersCursor.getString(offersCursor.getColumnIndex(OfferColumns.IMAGE_URL)));
                    offerDataList.add(offer);
                    offersCursor.moveToNext();
                }
            } else {
                offerDataList.add(MyOdeonData.createDummy(Type.offer));
            }
        } else {
            showAlert(null);
        }
        return offerDataList;
    }

    protected ArrayList<MyOdeonData> readFavouriteCinemaData() {
        ArrayList<MyOdeonData> cinemaDataList = new ArrayList();
        Cursor cinemasCursor = managedQuery(SiteColumns.CONTENT_URI, null, "SiteFavourite._id IS NOT NULL", null, null);
        Log.d(TAG, "Found " + cinemasCursor.getCount() + " favourite cinemas");
        if (cinemasCursor.moveToFirst()) {
            while (!cinemasCursor.isAfterLast()) {
                MyOdeonData cinema = new MyOdeonData();
                cinema.type = Type.cinema;
                cinema.values.put("_id", Integer.valueOf(cinemasCursor.getInt(cinemasCursor.getColumnIndex("_id"))));
                cinema.values.put(SiteColumns.NAME, cinemasCursor.getString(cinemasCursor.getColumnIndex(SiteColumns.NAME)));
                cinema.values.put(SiteColumns.ADDR, cinemasCursor.getString(cinemasCursor.getColumnIndex(SiteColumns.ADDR)));
                cinema.values.put(SiteColumns.POSTCODE, cinemasCursor.getString(cinemasCursor.getColumnIndex(SiteColumns.POSTCODE)));
                cinema.values.put(SiteColumns.DISTANCE_FROM_GPS, cinemasCursor.isNull(cinemasCursor.getColumnIndex(SiteColumns.DISTANCE_FROM_GPS)) ? null : Float.valueOf(cinemasCursor.getFloat(cinemasCursor.getColumnIndex(SiteColumns.DISTANCE_FROM_GPS))));
                cinemaDataList.add(cinema);
                cinemasCursor.moveToNext();
            }
            handleNavigationHeaderEditButton(true);
        } else {
            cinemaDataList.add(MyOdeonData.createDummy(Type.cinema));
        }
        return cinemaDataList;
    }

    protected ArrayList<MyOdeonData> readFavouriteFilmData() {
        ArrayList<MyOdeonData> filmDataList = new ArrayList();
        Cursor filmsCursor = managedQuery(FilmColumns.CONTENT_URI, null, " favourite=1 ", null, FilmColumns.DEFAULT_SORT_ORDER);
        Log.d(TAG, "Found " + filmsCursor.getCount() + " favourite films");
        if (filmsCursor.moveToFirst()) {
            while (!filmsCursor.isAfterLast()) {
                boolean z;
                MyOdeonData film = new MyOdeonData();
                film.type = Type.film;
                Integer valueOf = Integer.valueOf(filmsCursor.getInt(filmsCursor.getColumnIndex("_id")));
                String string = filmsCursor.getString(filmsCursor.getColumnIndex(OfferColumns.TITLE));
                String string2 = filmsCursor.getString(filmsCursor.getColumnIndex(FilmColumns.TRAILER_URL));
                String string3 = filmsCursor.getString(filmsCursor.getColumnIndex(OfferColumns.IMAGE_URL));
                String string4 = filmsCursor.getString(filmsCursor.getColumnIndex(FilmColumns.CERTIFICATE));
                boolean z2 = filmsCursor.getInt(filmsCursor.getColumnIndex(FilmColumns.RATEABLE)) == 1;
                float f = (float) filmsCursor.getInt(filmsCursor.getColumnIndex(FilmColumns.HALFRATING));
                String string5 = filmsCursor.getString(filmsCursor.getColumnIndex(FilmColumns.GENRE));
                String string6 = filmsCursor.getString(filmsCursor.getColumnIndex(FilmColumns.RELDATE));
                if (filmsCursor.getInt(filmsCursor.getColumnIndex(FilmColumns.BBF)) == 1) {
                    z = true;
                } else {
                    z = false;
                }
                film.values.put("_id", new FilmListFilm(valueOf, string, string2, string3, string4, z2, f, string5, string6, z));
                filmDataList.add(film);
                filmsCursor.moveToNext();
            }
            handleNavigationHeaderEditButton(true);
        } else {
            filmDataList.add(MyOdeonData.createDummy(Type.film));
        }
        return filmDataList;
    }

    private void openCinemaInfo(long cinemaId) {
        Intent cinemaDetails = new Intent(getParent(), FilmListActivity.class);
        cinemaDetails.putExtra(Constants.EXTRA_CINEMA_ID, (int) cinemaId);
        startSubActivity(cinemaDetails, "Cinema Details");
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
