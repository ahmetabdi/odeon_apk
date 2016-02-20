package uk.co.odeon.androidapp.indexers;

import android.content.Context;
import android.util.Log;
import android.widget.SectionIndexer;
import java.util.List;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.model.BookingPrice;

public class TicketListSectionIndexer implements SectionIndexer {
    private static final String TAG;
    protected List<BookingPrice> items;
    public String sectionName3D;
    public String sectionNameNormal;
    protected Integer startPosNormal;

    static {
        TAG = TicketListSectionIndexer.class.getSimpleName();
    }

    public TicketListSectionIndexer(Context ctx, List<BookingPrice> items) {
        this.startPosNormal = null;
        this.items = items;
        this.sectionName3D = ctx.getResources().getString(R.string.tickets_list_header_3d);
        this.sectionNameNormal = ctx.getResources().getString(R.string.tickets_list_header_normal);
    }

    public void index(boolean force) {
        if (force || this.startPosNormal == null) {
            Log.i(TAG, "Indexing cursor, finding position of first non 3d ticket");
            for (BookingPrice price : this.items) {
                int index = this.items.indexOf(price);
                if (!price.is3d) {
                    Log.i(TAG, "Found non 3d ticket, pos is " + index);
                    this.startPosNormal = Integer.valueOf(index);
                    break;
                }
            }
            if (this.startPosNormal == null) {
                this.startPosNormal = Integer.valueOf(999999999);
            }
        }
    }

    public int getPositionForSection(int section) {
        index(false);
        if (section == 0) {
            return 0;
        }
        return this.startPosNormal.intValue();
    }

    public int getSectionForPosition(int position) {
        index(false);
        if (position >= this.startPosNormal.intValue()) {
            return 1;
        }
        return 0;
    }

    public Object[] getSections() {
        index(false);
        if (this.startPosNormal.intValue() == -1) {
            return new String[]{this.sectionName3D};
        } else if (this.startPosNormal.intValue() == 0) {
            return new String[]{this.sectionNameNormal};
        } else {
            return new String[]{this.sectionName3D, this.sectionNameNormal};
        }
    }
}
