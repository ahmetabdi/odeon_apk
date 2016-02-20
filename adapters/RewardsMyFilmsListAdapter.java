package uk.co.odeon.androidapp.adapters;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.adapters.FilmListAdapterTools.ViewHolder;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity.RootActivity;
import uk.co.odeon.androidapp.indexers.RewardsMyFilmsSectionIndexer;
import uk.co.odeon.androidapp.model.FilmListMyFilm;
import uk.co.odeon.androidapp.model.FilmListMyFilm.FilmCategory;
import uk.co.odeon.androidapp.util.amazinglist.AmazingArrayAdapter;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;

public class RewardsMyFilmsListAdapter extends AmazingArrayAdapter<FilmListMyFilm> {
    private static /* synthetic */ int[] $SWITCH_TABLE$uk$co$odeon$androidapp$model$FilmListMyFilm$FilmCategory;
    protected static final String TAG;
    private Context ctx;
    private FilmListAdapterTools filmListTools;
    private RewardsMyFilmsSectionIndexer sectionIndexer;

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
        TAG = RewardsMyFilmsListAdapter.class.getSimpleName();
    }

    public RewardsMyFilmsListAdapter(Context context, int textViewResourceId, List<FilmListMyFilm> items, Handler filmListHandler) {
        super(context, textViewResourceId, items);
        this.ctx = context;
        this.sectionIndexer = new RewardsMyFilmsSectionIndexer(items);
        this.filmListTools = new FilmListAdapterTools(filmListHandler);
    }

    public void refreshList(List<FilmListMyFilm> items) {
        setNotifyOnChange(false);
        clear();
        for (FilmListMyFilm item : items) {
            add(item);
        }
        this.sectionIndexer.setFilms(items);
        notifyDataSetChanged();
        setNotifyOnChange(true);
    }

    protected String translateSectionName(FilmCategory cat) {
        int strId = -1;
        switch ($SWITCH_TABLE$uk$co$odeon$androidapp$model$FilmListMyFilm$FilmCategory()[cat.ordinal()]) {
            case AmazingListView.PINNED_HEADER_VISIBLE /*1*/:
                strId = R.string.rewards_myfilms_recommended;
                break;
            case AmazingListView.PINNED_HEADER_PUSHED_UP /*2*/:
                strId = R.string.rewards_myfilms_bookedAndNotRated;
                break;
            case RootActivity.TYPE_RIGHT /*3*/:
                strId = R.string.rewards_myfilms_bookedAndRated;
                break;
            case RootActivity.TYPE_SINGLE /*4*/:
                strId = R.string.rewards_myfilms_allOtherFilmsRated;
                break;
        }
        if (strId != -1) {
            return this.ctx.getResources().getString(strId);
        }
        return cat.toString();
    }

    protected void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
        TextView headerText;
        if (view.getTag() instanceof ViewHolder) {
            headerText = ((ViewHolder) view.getTag()).filmListHeaderText;
        } else {
            headerText = (TextView) view.findViewById(R.id.list_row_header_text);
        }
        if (displaySectionHeader) {
            headerText.setVisibility(0);
            headerText.setText(translateSectionName((FilmCategory) getSections()[getSectionForPosition(position)]));
            return;
        }
        headerText.setVisibility(8);
    }

    public void configurePinnedHeader(View header, int position, int alpha) {
        try {
            TextView lSectionHeader = (TextView) header;
            Object sect = getSections()[getSectionForPosition(position)];
            if (sect != null) {
                lSectionHeader.setText(translateSectionName((FilmCategory) sect));
            }
        } catch (Throwable e) {
            Log.e(TAG, "Failed to configure pinned header: " + e.getMessage(), e);
        }
    }

    public View newView(Context context, FilmListMyFilm film, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (film.filmId != null) {
            View v = inflater.inflate(R.layout.film_list_item, parent, false);
            this.filmListTools.initViewHolder(v);
            return v;
        }
        v = inflater.inflate(R.layout.rewards_my_films_empty_sect, parent, false);
        v.setTag(null);
        return v;
    }

    public View getAmazingView(int position, View convertView, ViewGroup parent) {
        FilmListMyFilm film = (FilmListMyFilm) getItem(position);
        View v = convertView;
        if (v == null || v.getTag() == null || film.filmId == null) {
            v = newView(getContext(), film, parent);
        }
        Object vh = v.getTag();
        if (film.filmId != null) {
            this.filmListTools.bindView(v, (ViewHolder) vh, film, null);
        }
        return v;
    }

    public int getPositionForSection(int section) {
        return this.sectionIndexer.getPositionForSection(section);
    }

    public int getSectionForPosition(int position) {
        return this.sectionIndexer.getSectionForPosition(position);
    }

    public Object[] getSections() {
        return this.sectionIndexer.getSections();
    }

    protected void onNextPageRequested(int page) {
    }
}
