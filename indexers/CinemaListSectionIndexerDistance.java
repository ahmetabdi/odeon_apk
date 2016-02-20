package uk.co.odeon.androidapp.indexers;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.util.Log;
import android.util.SparseIntArray;
import android.widget.SectionIndexer;
import twitter4j.internal.http.HttpResponseCode;
import uk.co.odeon.androidapp.Constants.APP_LOCATION;
import uk.co.odeon.androidapp.R;

public class CinemaListSectionIndexerDistance extends DataSetObserver implements SectionIndexer {
    protected static final int[] STEPS_MILES;
    private static final String TAG;
    protected Cursor dataCursor;
    protected int distColIdx;
    protected int favColIdx;
    protected SparseIntArray posToSect;
    protected SparseIntArray sectToPos;
    protected String[] sects;

    static {
        TAG = CinemaListSectionIndexerDistance.class.getSimpleName();
        int[] iArr = new int[8];
        iArr[1] = 5;
        iArr[2] = 10;
        iArr[3] = 20;
        iArr[4] = 50;
        iArr[5] = 250;
        iArr[6] = HttpResponseCode.MULTIPLE_CHOICES;
        iArr[7] = 350;
        STEPS_MILES = iArr;
    }

    public CinemaListSectionIndexerDistance(Context ctx, Cursor c, int distColIdx, int favouriteColumnIndex, APP_LOCATION location) {
        this.sectToPos = new SparseIntArray();
        this.posToSect = new SparseIntArray();
        this.dataCursor = c;
        this.distColIdx = distColIdx;
        this.favColIdx = favouriteColumnIndex;
        if (c != null) {
            c.registerDataSetObserver(this);
        }
        this.sects = new String[(STEPS_MILES.length + 2)];
        this.sects[0] = "1";
        this.sects[1] = "";
        int curStep = 2;
        while (curStep < STEPS_MILES.length) {
            if (APP_LOCATION.ire.equals(location)) {
                this.sects[curStep] = ctx.getResources().getString(R.string.cinema_list_dist_header_within_ire, new Object[]{Integer.valueOf(STEPS_MILES[curStep - 1])});
            } else {
                this.sects[curStep] = ctx.getResources().getString(R.string.cinema_list_dist_header_within, new Object[]{Integer.valueOf(STEPS_MILES[curStep - 1])});
            }
            curStep++;
        }
        if (APP_LOCATION.ire.equals(location)) {
            this.sects[curStep] = ctx.getResources().getString(R.string.cinema_list_dist_header_more_ire, new Object[]{Integer.valueOf(STEPS_MILES[curStep - 1])});
            return;
        }
        this.sects[curStep] = ctx.getResources().getString(R.string.cinema_list_dist_header_more, new Object[]{Integer.valueOf(STEPS_MILES[curStep - 1])});
    }

    public void index(boolean force) {
        if (force || this.sectToPos.size() <= 0) {
            this.sectToPos.clear();
            this.posToSect.clear();
            for (int i = 0; i <= STEPS_MILES.length; i++) {
                this.sectToPos.put(i, 0);
            }
            Log.i(TAG, "Indexing cursor, finding positions");
            int savedCursorPos = this.dataCursor.getPosition();
            int curStep = 0;
            double curMiles = (double) STEPS_MILES[0];
            int pos = 0;
            this.dataCursor.moveToFirst();
            while (!this.dataCursor.isAfterLast()) {
                boolean isFav = !this.dataCursor.isNull(this.favColIdx);
                if (isFav) {
                    Log.d(TAG, "Position #" + pos + " is favourite");
                    if (pos == 0) {
                        this.sectToPos.put(curStep, pos);
                        Log.d(TAG, "Position for section #" + curStep + " is #" + pos);
                    }
                    this.posToSect.put(pos, 0);
                    pos++;
                    this.dataCursor.moveToNext();
                } else {
                    if (!isFav && curStep == 0) {
                        curStep++;
                        try {
                            this.sectToPos.put(curStep, pos);
                            Log.d(TAG, "Reached end of favourites, advanced to step/section #" + curStep);
                        } finally {
                            this.dataCursor.moveToPosition(savedCursorPos);
                        }
                    }
                    double dist = this.dataCursor.getDouble(this.distColIdx);
                    Log.v(TAG, "dist=" + dist);
                    while (dist > curMiles) {
                        Log.i(TAG, "Exceeded step #" + curStep + " of " + curMiles + " miles at pos #" + pos);
                        curStep++;
                        if (curStep < STEPS_MILES.length) {
                            curMiles = (double) STEPS_MILES[curStep - 1];
                        } else {
                            curMiles = 999999.0d;
                        }
                        this.sectToPos.put(curStep, pos);
                        Log.i(TAG, "Next Step  #" + curStep + " starts with " + curMiles);
                    }
                    this.dataCursor.moveToNext();
                    this.posToSect.put(pos, curStep);
                    pos++;
                }
            }
        }
    }

    public int getPositionForSection(int section) {
        index(false);
        if (section > STEPS_MILES.length) {
            return getPositionForSection(STEPS_MILES.length);
        }
        return this.sectToPos.get(section);
    }

    public int getSectionForPosition(int position) {
        index(false);
        return this.posToSect.get(position, 0);
    }

    public Object[] getSections() {
        return this.sects;
    }
}
