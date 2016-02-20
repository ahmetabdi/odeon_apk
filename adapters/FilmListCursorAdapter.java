package uk.co.odeon.androidapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.SectionIndexer;
import android.widget.TextView;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.adapters.FilmListAdapterTools.ViewHolder;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity.RootActivity;
import uk.co.odeon.androidapp.indexers.FilmListSectionIndexerComingSoonFutureRelease;
import uk.co.odeon.androidapp.indexers.FilmListSingleSectionIndexer;
import uk.co.odeon.androidapp.model.FilmListFilm;
import uk.co.odeon.androidapp.provider.FilmContent.FilmColumns;
import uk.co.odeon.androidapp.provider.OfferContent.OfferColumns;
import uk.co.odeon.androidapp.util.amazinglist.AmazingCursorAdapter;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;

public class FilmListCursorAdapter extends AmazingCursorAdapter implements SectionIndexer {
    private static /* synthetic */ int[] $SWITCH_TABLE$uk$co$odeon$androidapp$adapters$FilmListCursorAdapter$IndexerType;
    protected static final String TAG;
    private SparseIntArray accessibleData;
    private int bbfColIndex;
    private int certColIndex;
    private int comingSoonColIndex;
    private boolean filterMode;
    private int futureReleaseColIndex;
    private int genreColIndex;
    private int halfRatingColIndex;
    private int idColIndex;
    private int imageURLColIndex;
    private Context mContext;
    private int rateableColIndex;
    private int relDateColIndex;
    private SectionIndexer sectionIndexer;
    private int titleColIndex;
    private FilmListAdapterTools tools;
    private int trailerURLColIndex;

    public enum IndexerType {
        ALPHA,
        COMMING_SOON,
        SINGLE_SECTION
    }

    static /* synthetic */ int[] $SWITCH_TABLE$uk$co$odeon$androidapp$adapters$FilmListCursorAdapter$IndexerType() {
        int[] iArr = $SWITCH_TABLE$uk$co$odeon$androidapp$adapters$FilmListCursorAdapter$IndexerType;
        if (iArr == null) {
            iArr = new int[IndexerType.values().length];
            try {
                iArr[IndexerType.ALPHA.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[IndexerType.COMMING_SOON.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[IndexerType.SINGLE_SECTION.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            $SWITCH_TABLE$uk$co$odeon$androidapp$adapters$FilmListCursorAdapter$IndexerType = iArr;
        }
        return iArr;
    }

    static {
        TAG = FilmListCursorAdapter.class.getSimpleName();
    }

    public FilmListCursorAdapter(IndexerType indexerType, Context context, Cursor c, Handler handler, boolean autoRequery) {
        super(context, c, autoRequery);
        this.filterMode = false;
        init(indexerType, context, c, handler);
    }

    public FilmListCursorAdapter(IndexerType indexerType, Context context, Cursor c, Handler handler) {
        super(context, c);
        this.filterMode = false;
        init(indexerType, context, c, handler);
    }

    protected void init(IndexerType indexerType, Context context, Cursor c, Handler handler) {
        this.mContext = context;
        this.idColIndex = c.getColumnIndex("_id");
        this.imageURLColIndex = c.getColumnIndex(OfferColumns.IMAGE_URL);
        this.trailerURLColIndex = c.getColumnIndex(FilmColumns.TRAILER_URL);
        this.titleColIndex = c.getColumnIndex(OfferColumns.TITLE);
        this.certColIndex = c.getColumnIndex(FilmColumns.CERTIFICATE);
        this.halfRatingColIndex = c.getColumnIndex(FilmColumns.HALFRATING);
        this.rateableColIndex = c.getColumnIndex(FilmColumns.RATEABLE);
        this.comingSoonColIndex = c.getColumnIndex(FilmColumns.COMINGSOON);
        this.futureReleaseColIndex = c.getColumnIndex(FilmColumns.FUTURERELEASE);
        this.relDateColIndex = c.getColumnIndex(FilmColumns.RELDATE);
        this.genreColIndex = c.getColumnIndex(FilmColumns.GENRE);
        this.bbfColIndex = c.getColumnIndex(FilmColumns.BBF);
        switch ($SWITCH_TABLE$uk$co$odeon$androidapp$adapters$FilmListCursorAdapter$IndexerType()[indexerType.ordinal()]) {
            case AmazingListView.PINNED_HEADER_PUSHED_UP /*2*/:
                this.sectionIndexer = new FilmListSectionIndexerComingSoonFutureRelease(c);
                break;
            case RootActivity.TYPE_RIGHT /*3*/:
                this.sectionIndexer = new FilmListSingleSectionIndexer();
                break;
            default:
                this.sectionIndexer = new AlphabetIndexer(c, this.titleColIndex, " 123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                break;
        }
        this.tools = new FilmListAdapterTools(handler);
    }

    public SparseIntArray getAccessibleData() {
        return this.accessibleData;
    }

    public void setAccessibleData(SparseIntArray accessibleData) {
        this.accessibleData = accessibleData;
    }

    public boolean isFilterMode() {
        return this.filterMode;
    }

    public void setFilterMode(boolean filterMode) {
        this.filterMode = filterMode;
    }

    public void setSingleSectionIndexerSectionName(String sectionName) {
        if (this.sectionIndexer != null && (this.sectionIndexer instanceof FilmListSingleSectionIndexer)) {
            ((FilmListSingleSectionIndexer) this.sectionIndexer).setSectionName(sectionName);
        }
    }

    public void bindView(View view, Context context, Cursor cursor) {
        boolean z;
        float f;
        String string;
        String string2;
        boolean z2;
        View view2;
        int filmId = cursor.getInt(this.idColIndex);
        Log.d(TAG, "bindView #" + filmId);
        ViewHolder vh = (ViewHolder) view.getTag();
        Integer valueOf = Integer.valueOf(filmId);
        String string3 = cursor.getString(this.titleColIndex);
        String string4 = cursor.getString(this.trailerURLColIndex);
        String string5 = cursor.getString(this.imageURLColIndex);
        String string6 = cursor.getString(this.certColIndex);
        if (cursor.getInt(this.rateableColIndex) == 1) {
            if (cursor.getInt(this.comingSoonColIndex) == 0) {
                if (cursor.getInt(this.futureReleaseColIndex) == 0) {
                    z = true;
                    f = (float) cursor.getInt(this.halfRatingColIndex);
                    string = cursor.getString(this.genreColIndex);
                    string2 = cursor.getString(this.relDateColIndex);
                    if (cursor.getInt(this.bbfColIndex) != 1) {
                        z2 = true;
                    } else {
                        z2 = false;
                    }
                    view2 = view;
                    this.tools.bindView(view2, vh, new FilmListFilm(valueOf, string3, string4, string5, string6, z, f, string, string2, z2), this.accessibleData);
                }
            }
        }
        z = false;
        f = (float) cursor.getInt(this.halfRatingColIndex);
        string = cursor.getString(this.genreColIndex);
        string2 = cursor.getString(this.relDateColIndex);
        if (cursor.getInt(this.bbfColIndex) != 1) {
            z2 = false;
        } else {
            z2 = true;
        }
        view2 = view;
        this.tools.bindView(view2, vh, new FilmListFilm(valueOf, string3, string4, string5, string6, z, f, string, string2, z2), this.accessibleData);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v;
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "newView " + cursor.getInt(this.idColIndex));
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        if (this.filterMode) {
            v = inflater.inflate(R.layout.film_list_item_accessible, parent, false);
        } else {
            v = inflater.inflate(R.layout.film_list_item, parent, false);
        }
        this.tools.initViewHolder(v);
        bindView(v, context, cursor);
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

    protected void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
        ViewHolder vh = (ViewHolder) view.getTag();
        if (displaySectionHeader) {
            vh.filmListHeaderText.setVisibility(0);
            vh.filmListHeaderText.setText(getSections()[getSectionForPosition(position)].toString());
            return;
        }
        vh.filmListHeaderText.setVisibility(8);
    }

    public void configurePinnedHeader(View header, int position, int alpha) {
        try {
            TextView lSectionHeader = (TextView) header;
            Object sect = getSections()[getSectionForPosition(position)];
            if (sect != null) {
                lSectionHeader.setText(sect.toString());
            }
        } catch (Throwable e) {
            Log.e(TAG, "Failed to configure pinned header: " + e.getMessage(), e);
        }
    }

    public View getAmazingView(int position, View convertView, ViewGroup parent) {
        if (getCursor().moveToPosition(position)) {
            View v;
            if (convertView == null) {
                v = newView(this.mContext, getCursor(), parent);
            } else {
                v = convertView;
            }
            bindView(v, this.mContext, getCursor());
            return v;
        }
        throw new IllegalStateException("couldn't move cursor to position " + position);
    }

    protected void onNextPageRequested(int page) {
    }
}
