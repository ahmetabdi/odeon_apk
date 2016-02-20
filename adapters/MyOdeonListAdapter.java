package uk.co.odeon.androidapp.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SectionIndexer;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity.RootActivity;
import uk.co.odeon.androidapp.indexers.MyOdeonListSectionIndexer;
import uk.co.odeon.androidapp.model.FilmListFilm;
import uk.co.odeon.androidapp.model.MyOdeonData;
import uk.co.odeon.androidapp.model.MyOdeonData.Type;
import uk.co.odeon.androidapp.provider.OfferContent.OfferColumns;
import uk.co.odeon.androidapp.provider.SiteContent.SiteColumns;
import uk.co.odeon.androidapp.util.amazinglist.AmazingArrayAdapter;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;

public class MyOdeonListAdapter extends AmazingArrayAdapter<MyOdeonData> implements SectionIndexer {
    private static /* synthetic */ int[] $SWITCH_TABLE$uk$co$odeon$androidapp$model$MyOdeonData$Type;
    protected static final String TAG;
    private CinemaListAdapterTools cinemaListTools;
    private Handler filmListHandler;
    private FilmListAdapterTools filmListTools;
    private OfferListAdapterTools offerListTools;
    MyOdeonListSectionIndexer sectionIndexer;

    /* renamed from: uk.co.odeon.androidapp.adapters.MyOdeonListAdapter.1 */
    class AnonymousClass1 implements OnClickListener {
        private final /* synthetic */ int val$index;
        private final /* synthetic */ MyOdeonData val$mod;

        AnonymousClass1(MyOdeonData myOdeonData, int i) {
            this.val$mod = myOdeonData;
            this.val$index = i;
        }

        public void onClick(View v) {
            MyOdeonListAdapter.this.removeItem(this.val$mod, this.val$index);
        }
    }

    private class ViewHolder {
        public TextView myOdeonListHeaderText;

        private ViewHolder() {
        }
    }

    static /* synthetic */ int[] $SWITCH_TABLE$uk$co$odeon$androidapp$model$MyOdeonData$Type() {
        int[] iArr = $SWITCH_TABLE$uk$co$odeon$androidapp$model$MyOdeonData$Type;
        if (iArr == null) {
            iArr = new int[Type.values().length];
            try {
                iArr[Type.cinema.ordinal()] = 3;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Type.dummy.ordinal()] = 1;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Type.film.ordinal()] = 4;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[Type.offer.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            $SWITCH_TABLE$uk$co$odeon$androidapp$model$MyOdeonData$Type = iArr;
        }
        return iArr;
    }

    static {
        TAG = AmazingArrayAdapter.class.getSimpleName();
    }

    public MyOdeonListAdapter(Context context, int textViewResourceId, List<MyOdeonData> items, Handler filmListHandler) {
        super(context, textViewResourceId, items);
        this.sectionIndexer = new MyOdeonListSectionIndexer(context, items);
        this.offerListTools = new OfferListAdapterTools();
        this.cinemaListTools = new CinemaListAdapterTools(null, ODEONApplication.getInstance().getChoosenLocation());
        this.filmListTools = new FilmListAdapterTools(filmListHandler);
        this.filmListHandler = filmListHandler;
    }

    public void refreshList(List<MyOdeonData> items) {
        setNotifyOnChange(false);
        clear();
        for (MyOdeonData item : items) {
            add(item);
        }
        this.sectionIndexer.setItems(items);
        notifyDataSetChanged();
        setNotifyOnChange(true);
    }

    public void removeItem(MyOdeonData item, int position) {
        int i;
        setNotifyOnChange(false);
        int itemTypeCount = 0;
        int count = getCount();
        for (i = 0; i < count; i++) {
            if (item.type.equals(((MyOdeonData) getItem(i)).type)) {
                itemTypeCount++;
            }
        }
        if (item.type.equals(Type.cinema)) {
            ODEONApplication.getInstance().changeCinemaFavouriteInDatabase((int) getItemId(position), false);
        } else if (item.type.equals(Type.film)) {
            ODEONApplication.getInstance().changeFilmFavouriteInDatabase((int) getItemId(position), false);
        }
        if (itemTypeCount == 1) {
            MyOdeonData dummy = MyOdeonData.createDummy(item.type);
            remove(item);
            insert(dummy, position);
        } else {
            remove(item);
            this.sectionIndexer.removeItem(item);
        }
        notifyDataSetChanged();
        setNotifyOnChange(true);
        boolean isListEmpty = true;
        i = 0;
        while (i < count) {
            if (!((MyOdeonData) getItem(i)).type.equals(Type.offer) && !((MyOdeonData) getItem(i)).isDummy()) {
                isListEmpty = false;
                break;
            }
            i++;
        }
        if (isListEmpty) {
            Message msg = new Message();
            msg.what = Constants.FILM_LIST_MSG_EMPTY;
            this.filmListHandler.sendMessage(msg);
        }
    }

    public void switchDeletable() {
        int count = getCount();
        for (int i = 0; i < count; i++) {
            MyOdeonData item = (MyOdeonData) getItem(i);
            if (item.type.equals(Type.cinema) || item.type.equals(Type.film)) {
                item.deletable = !item.deletable;
            }
        }
        notifyDataSetChanged();
    }

    public View newView(Context context, MyOdeonData data, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v;
        switch ($SWITCH_TABLE$uk$co$odeon$androidapp$model$MyOdeonData$Type()[data.type.ordinal()]) {
            case AmazingListView.PINNED_HEADER_PUSHED_UP /*2*/:
                v = inflater.inflate(R.layout.offer_list_item, parent, false);
                this.offerListTools.initViewHolder(v);
                return v;
            case RootActivity.TYPE_RIGHT /*3*/:
                v = inflater.inflate(R.layout.cinema_list_item_deletable, parent, false);
                this.cinemaListTools.initViewHolder(v);
                return v;
            case RootActivity.TYPE_SINGLE /*4*/:
                v = inflater.inflate(R.layout.film_list_item_deletable, parent, false);
                this.filmListTools.initViewHolder(v);
                return v;
            default:
                v = inflater.inflate(R.layout.empty_list_item, parent, false);
                ViewHolder vh = new ViewHolder();
                vh.myOdeonListHeaderText = (TextView) v.findViewById(R.id.list_row_header_text);
                v.setTag(vh);
                return v;
        }
    }

    public View getAmazingView(int position, View convertView, ViewGroup parent) {
        int index = position;
        MyOdeonData mod = (MyOdeonData) getItem(position);
        View v = convertView;
        if (v == null) {
            v = newView(getContext(), mod, parent);
        } else {
            Object vh = v.getTag();
            if (((vh instanceof uk.co.odeon.androidapp.adapters.FilmListAdapterTools.ViewHolder) && !mod.type.equals(Type.film)) || (((vh instanceof uk.co.odeon.androidapp.adapters.CinemaListAdapterTools.ViewHolder) && !mod.type.equals(Type.cinema)) || (((vh instanceof uk.co.odeon.androidapp.adapters.OfferListAdapterTools.ViewHolder) && !mod.type.equals(Type.offer)) || ((vh instanceof ViewHolder) && !mod.type.equals(Type.dummy))))) {
                v = newView(getContext(), mod, parent);
            }
        }
        if (mod != null) {
            switch ($SWITCH_TABLE$uk$co$odeon$androidapp$model$MyOdeonData$Type()[mod.type.ordinal()]) {
                case AmazingListView.PINNED_HEADER_PUSHED_UP /*2*/:
                    bindOfferView(v, getContext(), mod.values);
                    break;
                case RootActivity.TYPE_RIGHT /*3*/:
                    bindCinemaView(v, getContext(), mod.values);
                    break;
                case RootActivity.TYPE_SINGLE /*4*/:
                    bindFilmView(v, getContext(), mod.values);
                    break;
                default:
                    bindDummyView(v, (String) mod.values.get(MyOdeonData.KEY_DUMMY_TEXT));
                    break;
            }
            if (mod.type.equals(Type.cinema) || mod.type.equals(Type.film)) {
                Button deletableButton = (Button) v.findViewById(R.id.list_deletable_button);
                if (mod.deletable) {
                    if (deletableButton != null) {
                        deletableButton.setVisibility(0);
                    }
                    deletableButton.setOnClickListener(new AnonymousClass1(mod, index));
                } else {
                    if (deletableButton != null) {
                        deletableButton.setVisibility(8);
                    }
                    deletableButton.setOnClickListener(null);
                }
            }
        }
        return v;
    }

    public void bindOfferView(View view, Context context, HashMap<String, Object> dataMap) {
        this.offerListTools.bindView(view, (String) dataMap.get(OfferColumns.TITLE), (String) dataMap.get(OfferColumns.TEXT), (String) dataMap.get(OfferColumns.IMAGE_URL));
    }

    public void bindCinemaView(View view, Context context, HashMap<String, Object> dataMap) {
        this.cinemaListTools.bindView(view, ((Integer) dataMap.get("_id")).intValue(), (String) dataMap.get(SiteColumns.NAME), (Float) dataMap.get(SiteColumns.DISTANCE_FROM_GPS), (String) dataMap.get(SiteColumns.ADDR), (String) dataMap.get(SiteColumns.POSTCODE));
    }

    public void bindFilmView(View view, Context context, HashMap<String, Object> dataMap) {
        uk.co.odeon.androidapp.adapters.FilmListAdapterTools.ViewHolder vh = (uk.co.odeon.androidapp.adapters.FilmListAdapterTools.ViewHolder) view.getTag();
        this.filmListTools.bindView(view, vh, (FilmListFilm) dataMap.get("_id"), null);
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
        TextView headerText;
        if (view.getTag() instanceof uk.co.odeon.androidapp.adapters.OfferListAdapterTools.ViewHolder) {
            headerText = ((uk.co.odeon.androidapp.adapters.OfferListAdapterTools.ViewHolder) view.getTag()).offerListHeaderText;
        } else if (view.getTag() instanceof uk.co.odeon.androidapp.adapters.CinemaListAdapterTools.ViewHolder) {
            headerText = ((uk.co.odeon.androidapp.adapters.CinemaListAdapterTools.ViewHolder) view.getTag()).cinemaListHeaderText;
        } else if (view.getTag() instanceof uk.co.odeon.androidapp.adapters.FilmListAdapterTools.ViewHolder) {
            headerText = ((uk.co.odeon.androidapp.adapters.FilmListAdapterTools.ViewHolder) view.getTag()).filmListHeaderText;
        } else {
            headerText = ((ViewHolder) view.getTag()).myOdeonListHeaderText;
        }
        if (displaySectionHeader) {
            headerText.setVisibility(0);
            headerText.setText(getSections()[getSectionForPosition(position)].toString());
            return;
        }
        headerText.setVisibility(8);
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

    protected void onNextPageRequested(int page) {
    }

    public long getItemId(int pos) {
        MyOdeonData mod = pos < getCount() ? (MyOdeonData) getItem(pos) : null;
        if (mod != null) {
            if (mod.type.equals(Type.cinema)) {
                return (long) ((Integer) mod.values.get("_id")).intValue();
            }
            if (mod.type.equals(Type.film)) {
                return (long) ((FilmListFilm) mod.values.get("_id")).filmId.intValue();
            }
        }
        return -1;
    }

    private void bindDummyView(View view, String text) {
        TextView textView = (TextView) view.findViewById(R.id.emtpty_item_text);
        if (textView != null) {
            textView.setText(text);
        }
    }
}
