package uk.co.odeon.androidapp.indexers;

import android.content.Context;
import android.util.Log;
import android.widget.SectionIndexer;
import java.util.List;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.model.MyOdeonData;
import uk.co.odeon.androidapp.model.MyOdeonData.Type;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;

public class MyOdeonListSectionIndexer implements SectionIndexer {
    private static final String TAG;
    protected List<MyOdeonData> items;
    public String sectionNameFavCinemas;
    public String sectionNameFavFilms;
    public String sectionNameOffers;
    protected Integer startPosFavCinemas;
    protected Integer startPosFavFilms;

    static {
        TAG = MyOdeonListSectionIndexer.class.getSimpleName();
    }

    public MyOdeonListSectionIndexer(Context ctx, List<MyOdeonData> items) {
        this.startPosFavCinemas = null;
        this.startPosFavFilms = null;
        this.items = items;
        this.sectionNameFavCinemas = ctx.getResources().getString(R.string.myodeon_list_header_fav_cinemas);
        this.sectionNameFavFilms = ctx.getResources().getString(R.string.myodeon_list_header_fav_films);
        this.sectionNameOffers = ctx.getResources().getString(R.string.myodeon_list_header_offers);
    }

    public void setItems(List<MyOdeonData> items) {
        this.items = items;
        index(true);
    }

    public void removeItem(MyOdeonData item) {
        this.items.remove(item);
        index(true);
    }

    public void replaceItem(MyOdeonData item, MyOdeonData replace, int position) {
        this.items.remove(item);
        this.items.add(position, replace);
        index(true);
    }

    public void index(boolean force) {
        if (force || this.startPosFavCinemas == null) {
            this.startPosFavCinemas = null;
            this.startPosFavFilms = null;
            int isOfferDummy = -1;
            int isCinemaDummy = -1;
            int isFilmDummy = -1;
            Log.i(TAG, "Indexing cursor, finding position of first fav cinema and fav film");
            for (MyOdeonData data : this.items) {
                int index = this.items.indexOf(data);
                if (isOfferDummy < 0 && data.type.equals(Type.offer)) {
                    isOfferDummy = 0;
                } else if (isCinemaDummy < 0 && data.type.equals(Type.cinema)) {
                    isCinemaDummy = 0;
                } else if (isFilmDummy < 0 && data.type.equals(Type.film)) {
                    isFilmDummy = 0;
                } else if (isOfferDummy < 0 && data.type.equals(Type.dummy)) {
                    isOfferDummy = 1;
                } else if (isCinemaDummy < 0 && data.type.equals(Type.dummy)) {
                    isCinemaDummy = 1;
                } else if (isFilmDummy < 0 && data.type.equals(Type.dummy)) {
                    isFilmDummy = 1;
                }
                if (this.startPosFavCinemas != null || (!data.type.equals(Type.cinema) && (!data.type.equals(Type.dummy) || isCinemaDummy <= 0))) {
                    if (this.startPosFavFilms == null && (data.type.equals(Type.film) || (data.type.equals(Type.dummy) && isFilmDummy > 0))) {
                        Log.i(TAG, "Found first fav film, pos is " + index);
                        this.startPosFavFilms = Integer.valueOf(index);
                        break;
                    }
                }
                Log.i(TAG, "Found first fav cinema, pos is " + index);
                this.startPosFavCinemas = Integer.valueOf(index);
            }
            if (this.startPosFavCinemas == null) {
                this.startPosFavCinemas = Integer.valueOf(99999999);
            }
            if (this.startPosFavFilms == null) {
                this.startPosFavFilms = Integer.valueOf(999999999);
            }
        }
    }

    public int getPositionForSection(int section) {
        index(false);
        switch (section) {
            case AmazingListView.PINNED_HEADER_GONE /*0*/:
                return 0;
            case AmazingListView.PINNED_HEADER_VISIBLE /*1*/:
                return this.startPosFavCinemas.intValue();
            default:
                return this.startPosFavFilms.intValue();
        }
    }

    public int getSectionForPosition(int position) {
        index(false);
        if (position < this.startPosFavCinemas.intValue()) {
            return 0;
        }
        if (position < this.startPosFavFilms.intValue()) {
            return 1;
        }
        return 2;
    }

    public Object[] getSections() {
        index(false);
        return new String[]{this.sectionNameOffers, this.sectionNameFavCinemas, this.sectionNameFavFilms};
    }
}
