package uk.co.odeon.androidapp.indexers;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.util.Log;
import android.widget.SectionIndexer;
import uk.co.odeon.androidapp.provider.FilmContent.FilmColumns;

public class FilmListSectionIndexerComingSoonFutureRelease extends DataSetObserver implements SectionIndexer {
    protected static final String SECTIONNAME_COMING_SOON = "Coming Soon";
    protected static final String SECTIONNAME_FUTURE_RELEASE = "Future Release";
    private static final String TAG;
    protected int columnIndexComingSoon;
    protected Cursor dataCursor;
    protected Integer startPosFutureRelease;

    static {
        TAG = FilmListSectionIndexerComingSoonFutureRelease.class.getSimpleName();
    }

    public FilmListSectionIndexerComingSoonFutureRelease(Cursor cursor) {
        this.startPosFutureRelease = null;
        this.dataCursor = cursor;
        this.columnIndexComingSoon = cursor.getColumnIndex(FilmColumns.COMINGSOON);
        if (cursor != null) {
            cursor.registerDataSetObserver(this);
        }
    }

    public void index(boolean force) {
        if (force || this.startPosFutureRelease == null) {
            Log.i(TAG, "Indexing cursor, finding position of first non-commingsoon film");
            int savedCursorPos = this.dataCursor.getPosition();
            this.dataCursor.moveToFirst();
            while (!this.dataCursor.isAfterLast()) {
                int comingSoon = this.dataCursor.getInt(this.columnIndexComingSoon);
                Log.d(TAG, "ComingSoon=" + comingSoon);
                if (comingSoon <= 0) {
                    Log.i(TAG, "Found non-commingsoon film, pos is " + this.dataCursor.getPosition());
                    this.startPosFutureRelease = Integer.valueOf(this.dataCursor.getPosition());
                    break;
                }
                try {
                    this.dataCursor.moveToNext();
                } catch (Throwable th) {
                    this.dataCursor.moveToPosition(savedCursorPos);
                }
            }
            if (this.startPosFutureRelease == null) {
                this.startPosFutureRelease = Integer.valueOf(999999999);
            }
            this.dataCursor.moveToPosition(savedCursorPos);
        }
    }

    public int getPositionForSection(int section) {
        index(false);
        if (section == 0) {
            return 0;
        }
        return this.startPosFutureRelease.intValue();
    }

    public int getSectionForPosition(int position) {
        index(false);
        if (position >= this.startPosFutureRelease.intValue()) {
            return 1;
        }
        return 0;
    }

    public Object[] getSections() {
        index(false);
        if (this.startPosFutureRelease.intValue() == -1) {
            return new String[]{SECTIONNAME_COMING_SOON};
        }
        return new String[]{SECTIONNAME_COMING_SOON, SECTIONNAME_FUTURE_RELEASE};
    }

    public void onChanged() {
        super.onChanged();
        this.startPosFutureRelease = null;
    }

    public void onInvalidated() {
        super.onInvalidated();
        this.startPosFutureRelease = null;
    }
}
