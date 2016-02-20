package uk.co.odeon.androidapp.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import java.util.List;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.custom.AutofitTextView;
import uk.co.odeon.androidapp.custom.TicketSelectorView;
import uk.co.odeon.androidapp.indexers.TicketListSectionIndexer;
import uk.co.odeon.androidapp.model.BookingPrice;
import uk.co.odeon.androidapp.model.BookingProcess;
import uk.co.odeon.androidapp.model.BookingSection;
import uk.co.odeon.androidapp.util.amazinglist.AmazingArrayAdapter;

public class BookingTicketListAdapter extends AmazingArrayAdapter<BookingPrice> implements SectionIndexer {
    protected static final String TAG;
    private String filmInfo;
    private String filmTitle;
    private final BookingSection section;
    SectionIndexer sectionIndexer;

    private class ViewHolder {
        public ImageView glassesView;
        public RelativeLayout infoHeader;
        public AutofitTextView infoHeaderRow1;
        public AutofitTextView infoHeaderRow2;
        public TextView nameView;
        public TextView subscriptionView;
        public TicketSelectorView ticketSelectorView;

        private ViewHolder() {
        }
    }

    static {
        TAG = AmazingArrayAdapter.class.getSimpleName();
    }

    public BookingTicketListAdapter(Context context, int textViewResourceId, List<BookingPrice> items, String filmTitle, String filmInfo) {
        super(context, textViewResourceId, items);
        this.section = BookingProcess.getInstance().seatingData.getSelectedSection();
        this.filmTitle = filmTitle;
        this.filmInfo = filmInfo;
        this.sectionIndexer = new TicketListSectionIndexer(context, items);
    }

    public View newView(Context context, BookingPrice price, ViewGroup parent) {
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "newView " + price.id);
        }
        View v = LayoutInflater.from(context).inflate(R.layout.booking_ticket_list_item, parent, false);
        ViewHolder vh = new ViewHolder();
        vh.nameView = (TextView) v.findViewById(R.id.bookingTicketName);
        vh.ticketSelectorView = (TicketSelectorView) v.findViewById(R.id.bookingTicketSelector);
        vh.glassesView = (ImageView) v.findViewById(R.id.bookingTicketGlasses);
        vh.subscriptionView = (TextView) v.findViewById(R.id.bookingTicketSubscription);
        vh.infoHeader = (RelativeLayout) v.findViewById(R.id.infoHeader);
        vh.infoHeaderRow1 = (AutofitTextView) v.findViewById(R.id.infoHeaderRow1);
        vh.infoHeaderRow2 = (AutofitTextView) v.findViewById(R.id.infoHeaderRow2);
        v.setTag(vh);
        return v;
    }

    public View getAmazingView(int position, View convertView, ViewGroup parent) {
        BookingPrice p = (BookingPrice) getItem(position);
        View v = convertView;
        if (v == null) {
            v = newView(getContext(), p, parent);
        }
        if (p != null) {
            ViewHolder vh = (ViewHolder) v.getTag();
            if (vh.nameView != null) {
                vh.nameView.setText(p.name);
            }
            if (vh.ticketSelectorView != null) {
                vh.ticketSelectorView.setDataInView(this.section, p);
                vh.ticketSelectorView.handler = new Handler() {
                    public void handleMessage(Message msg) {
                        if (msg.what == Constants.TICKET_LIST_RELOAD) {
                            BookingTicketListAdapter.this.notifyDataSetChanged();
                        }
                    }
                };
            }
            if (vh.glassesView != null) {
                vh.glassesView.setVisibility(p.is3d ? 0 : 8);
            }
            if (vh.subscriptionView != null) {
                vh.subscriptionView.setText(p.subscription != null ? p.subscription : "");
            }
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

    protected void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
        ViewHolder vh = (ViewHolder) view.getTag();
        if (displaySectionHeader) {
            vh.infoHeader.setVisibility(0);
            if (getSections().length <= 1 || getSectionForPosition(position) == 0) {
                vh.infoHeaderRow1.setText(this.filmTitle);
                vh.infoHeaderRow2.setText(this.filmInfo);
                vh.infoHeaderRow2.setVisibility(0);
                return;
            }
            vh.infoHeaderRow1.setText(view.getResources().getString(R.string.booking_sub_header_ticket_without_3d));
            vh.infoHeaderRow2.setVisibility(4);
            return;
        }
        vh.infoHeader.setVisibility(8);
    }

    public void configurePinnedHeader(View header, int position, int alpha) {
        try {
            RelativeLayout lSectionHeader = (RelativeLayout) header;
            AutofitTextView lSectionHeaderRow1 = (AutofitTextView) lSectionHeader.getChildAt(0);
            AutofitTextView lSectionHeaderRow2 = (AutofitTextView) lSectionHeader.getChildAt(1);
            if (getSections().length <= 1 || getSectionForPosition(position) == 0) {
                lSectionHeaderRow1.setText(this.filmTitle);
                lSectionHeaderRow2.setText(this.filmInfo);
                lSectionHeaderRow2.setVisibility(0);
                return;
            }
            lSectionHeaderRow1.setText(header.getResources().getString(R.string.booking_sub_header_ticket_without_3d));
            lSectionHeaderRow2.setVisibility(4);
        } catch (Throwable e) {
            Log.e(TAG, "Failed to configure pinned header: " + e.getMessage(), e);
        }
    }

    protected void onNextPageRequested(int page) {
    }
}
