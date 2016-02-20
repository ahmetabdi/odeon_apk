package uk.co.odeon.androidapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.adapters.CinemaListAdapterTools.CinemaListMode;
import uk.co.odeon.androidapp.adapters.CinemaListAdapterTools.ViewHolder;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity.RootActivity;
import uk.co.odeon.androidapp.indexers.CinemaListAlphabetIndexerWithFavSite;
import uk.co.odeon.androidapp.indexers.CinemaListSectionIndexerDistance;
import uk.co.odeon.androidapp.provider.SiteContent.SiteColumns;
import uk.co.odeon.androidapp.util.amazinglist.AmazingCursorAdapter;

public class CinemaListCursorAdapter extends AmazingCursorAdapter implements SectionIndexer {
    private static /* synthetic */ int[] $SWITCH_TABLE$uk$co$odeon$androidapp$adapters$CinemaListAdapterTools$CinemaListMode;
    protected static final String TAG;
    private int addrColIndex;
    private int distColIndex;
    private String favCinemaText;
    private int favColIdx;
    private int idColIndex;
    private Context mContext;
    private int nameColIndex;
    private int postCodeColIndex;
    private SectionIndexer sectionIndexer;
    private CinemaListAdapterTools tools;

    static /* synthetic */ int[] $SWITCH_TABLE$uk$co$odeon$androidapp$adapters$CinemaListAdapterTools$CinemaListMode() {
        int[] iArr = $SWITCH_TABLE$uk$co$odeon$androidapp$adapters$CinemaListAdapterTools$CinemaListMode;
        if (iArr == null) {
            iArr = new int[CinemaListMode.values().length];
            try {
                iArr[CinemaListMode.ALPHA.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[CinemaListMode.ALPHA_WITH_DISTANCE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[CinemaListMode.DISTANCE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            $SWITCH_TABLE$uk$co$odeon$androidapp$adapters$CinemaListAdapterTools$CinemaListMode = iArr;
        }
        return iArr;
    }

    static {
        TAG = CinemaListCursorAdapter.class.getSimpleName();
    }

    public CinemaListCursorAdapter(CinemaListMode mode, boolean postCode, Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        init(mode, postCode, context, c);
    }

    public CinemaListCursorAdapter(CinemaListMode mode, boolean postCode, Context context, Cursor c) {
        super(context, c);
        init(mode, postCode, context, c);
    }

    protected void init(CinemaListMode mode, boolean postCode, Context context, Cursor c) {
        this.favCinemaText = context.getResources().getString(R.string.cinema_list_fav_header);
        this.mContext = context;
        this.nameColIndex = c.getColumnIndex(SiteColumns.NAME);
        this.addrColIndex = c.getColumnIndex(SiteColumns.ADDR);
        this.postCodeColIndex = c.getColumnIndex(SiteColumns.POSTCODE);
        this.distColIndex = postCode ? c.getColumnIndex(SiteColumns.DISTANCE_FROM_POSTCODE) : c.getColumnIndex(SiteColumns.DISTANCE_FROM_GPS);
        this.idColIndex = c.getColumnIndex("_id");
        this.favColIdx = c.getColumnIndex("favID");
        switch ($SWITCH_TABLE$uk$co$odeon$androidapp$adapters$CinemaListAdapterTools$CinemaListMode()[mode.ordinal()]) {
            case RootActivity.TYPE_RIGHT /*3*/:
                this.sectionIndexer = new CinemaListSectionIndexerDistance(context, c, this.distColIndex, this.favColIdx, ODEONApplication.getInstance().getChoosenLocation());
                break;
            default:
                this.sectionIndexer = new CinemaListAlphabetIndexerWithFavSite(c, this.nameColIndex, this.favColIdx, " 123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                break;
        }
        this.tools = new CinemaListAdapterTools(mode, ODEONApplication.getInstance().getChoosenLocation());
    }

    public void bindView(View view, Context context, Cursor cursor) {
        int siteId = cursor.getInt(this.idColIndex);
        Log.d(TAG, "bindView #" + siteId);
        this.tools.bindView(view, siteId, cursor.getString(this.nameColIndex), cursor.isNull(this.distColIndex) ? null : Float.valueOf(cursor.getFloat(this.distColIndex)), cursor.getString(this.addrColIndex), cursor.getString(this.postCodeColIndex));
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "newView " + cursor.getInt(this.idColIndex));
        }
        View v = LayoutInflater.from(context).inflate(R.layout.cinema_list_item, parent, false);
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
            vh.cinemaListHeaderText.setVisibility(0);
            vh.cinemaListHeaderText.setText(translateSectionText(getSections()[getSectionForPosition(position)].toString()));
            return;
        }
        vh.cinemaListHeaderText.setVisibility(8);
    }

    protected String translateSectionText(String sectText) {
        return "1".equals(sectText) ? this.favCinemaText : sectText;
    }

    public void configurePinnedHeader(View header, int position, int alpha) {
        try {
            TextView lSectionHeader = (TextView) header;
            Object sect = getSections()[getSectionForPosition(position)];
            if (sect != null) {
                lSectionHeader.setText(translateSectionText(sect.toString()));
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
