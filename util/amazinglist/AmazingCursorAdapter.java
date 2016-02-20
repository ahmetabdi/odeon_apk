package uk.co.odeon.androidapp.util.amazinglist;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CursorAdapter;
import android.widget.SectionIndexer;

public abstract class AmazingCursorAdapter extends CursorAdapter implements SectionIndexer, OnScrollListener, AmazingAdapter {
    boolean automaticNextPageLoading;
    HasMorePagesListener hasMorePagesListener;
    int initialPage;
    int page;

    protected abstract void bindSectionHeader(View view, int i, boolean z);

    public abstract void configurePinnedHeader(View view, int i, int i2);

    public abstract View getAmazingView(int i, View view, ViewGroup viewGroup);

    public abstract int getPositionForSection(int i);

    public abstract int getSectionForPosition(int i);

    public abstract Object[] getSections();

    protected abstract void onNextPageRequested(int i);

    public AmazingCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.page = 1;
        this.initialPage = 1;
        this.automaticNextPageLoading = false;
    }

    public AmazingCursorAdapter(Context context, Cursor c) {
        super(context, c);
        this.page = 1;
        this.initialPage = 1;
        this.automaticNextPageLoading = false;
    }

    public void setHasMorePagesListener(HasMorePagesListener hasMorePagesListener) {
        this.hasMorePagesListener = hasMorePagesListener;
    }

    public int getPinnedHeaderState(int position) {
        if (position < 0 || getCount() == 0) {
            return 0;
        }
        int nextSectionPosition = getPositionForSection(getSectionForPosition(position) + 1);
        if (nextSectionPosition == -1 || position != nextSectionPosition - 1) {
            return 1;
        }
        return 2;
    }

    public void setInitialPage(int initialPage) {
        this.initialPage = initialPage;
    }

    public void resetPage() {
        this.page = this.initialPage;
    }

    public void nextPage() {
        this.page++;
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view instanceof AmazingListView) {
            ((AmazingListView) view).configureHeaderView(firstVisibleItem);
        }
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    public final View getView(int position, View convertView, ViewGroup parent) {
        View res = getAmazingView(position, convertView, parent);
        if (position == getCount() - 1 && this.automaticNextPageLoading) {
            onNextPageRequested(this.page + 1);
        }
        bindSectionHeader(res, position, getPositionForSection(getSectionForPosition(position)) == position);
        return res;
    }

    public void notifyNoMorePages() {
        this.automaticNextPageLoading = false;
        if (this.hasMorePagesListener != null) {
            this.hasMorePagesListener.noMorePages();
        }
    }

    public void notifyMayHaveMorePages() {
        this.automaticNextPageLoading = true;
        if (this.hasMorePagesListener != null) {
            this.hasMorePagesListener.mayHaveMorePages();
        }
    }
}
