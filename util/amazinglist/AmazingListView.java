package uk.co.odeon.androidapp.util.amazinglist;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class AmazingListView extends ListView implements HasMorePagesListener {
    public static final int PINNED_HEADER_GONE = 0;
    public static final int PINNED_HEADER_PUSHED_UP = 2;
    public static final int PINNED_HEADER_VISIBLE = 1;
    public static final String TAG;
    private ListAdapter adapter;
    boolean footerViewAttached;
    View listFooter;
    private View mHeaderView;
    private int mHeaderViewHeight;
    private boolean mHeaderViewVisible;
    private int mHeaderViewWidth;

    static {
        TAG = AmazingListView.class.getSimpleName();
    }

    public AmazingListView(Context context) {
        super(context);
        this.footerViewAttached = false;
    }

    public AmazingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.footerViewAttached = false;
    }

    public AmazingListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.footerViewAttached = false;
    }

    public void setFastScrollEnabled(boolean enabled) {
        super.setFastScrollEnabled(enabled);
        super.onSizeChanged(getWidth(), getHeight(), getWidth(), getHeight());
    }

    public void setPinnedHeaderView(View view) {
        this.mHeaderView = view;
        if (this.mHeaderView != null) {
            setFadingEdgeLength(PINNED_HEADER_GONE);
        }
        requestLayout();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mHeaderView != null) {
            measureChild(this.mHeaderView, widthMeasureSpec, heightMeasureSpec);
            this.mHeaderViewWidth = this.mHeaderView.getMeasuredWidth();
            this.mHeaderViewHeight = this.mHeaderView.getMeasuredHeight();
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.mHeaderView != null) {
            this.mHeaderView.layout(PINNED_HEADER_GONE, PINNED_HEADER_GONE, this.mHeaderViewWidth, this.mHeaderViewHeight);
            configureHeaderView(getFirstVisiblePosition());
        }
    }

    public void configureHeaderView(int position) {
        if (this.mHeaderView != null) {
            switch (getAmazingAdapter().getPinnedHeaderState(position)) {
                case PINNED_HEADER_GONE /*0*/:
                    this.mHeaderViewVisible = false;
                case PINNED_HEADER_VISIBLE /*1*/:
                    getAmazingAdapter().configurePinnedHeader(this.mHeaderView, position, 255);
                    if (this.mHeaderView.getTop() != 0) {
                        this.mHeaderView.layout(PINNED_HEADER_GONE, PINNED_HEADER_GONE, this.mHeaderViewWidth, this.mHeaderViewHeight);
                    }
                    this.mHeaderViewVisible = true;
                case PINNED_HEADER_PUSHED_UP /*2*/:
                    View firstView = getChildAt(PINNED_HEADER_GONE);
                    if (firstView != null) {
                        int y;
                        int alpha;
                        int bottom = firstView.getBottom();
                        int headerHeight = this.mHeaderView.getHeight();
                        if (bottom < headerHeight) {
                            y = (bottom - headerHeight) + PINNED_HEADER_VISIBLE;
                            alpha = ((headerHeight + y) * 255) / headerHeight;
                        } else {
                            y = PINNED_HEADER_GONE;
                            alpha = 255;
                        }
                        getAmazingAdapter().configurePinnedHeader(this.mHeaderView, position, alpha);
                        if (this.mHeaderView.getTop() != y) {
                            this.mHeaderView.layout(PINNED_HEADER_GONE, y, this.mHeaderViewWidth, this.mHeaderViewHeight + y);
                        }
                        this.mHeaderViewVisible = true;
                    }
                default:
            }
        }
    }

    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
            if (this.mHeaderViewVisible) {
                drawChild(canvas, this.mHeaderView, getDrawingTime());
            }
        } catch (IndexOutOfBoundsException iofb) {
            Log.w(TAG, "Data is refreshed in the background and access is invalid", iofb);
        }
    }

    public void setLoadingView(View listFooter) {
        this.listFooter = listFooter;
    }

    public View getLoadingView() {
        return this.listFooter;
    }

    public void setAdapter(ListAdapter adapter) {
        if (adapter instanceof AmazingAdapter) {
            if (this.adapter != null) {
                getAmazingAdapter().setHasMorePagesListener(null);
                setOnScrollListener(null);
            }
            this.adapter = adapter;
            getAmazingAdapter().setHasMorePagesListener(this);
            setOnScrollListener((OnScrollListener) getAmazingAdapter());
            View dummy = new View(getContext());
            super.addFooterView(dummy);
            super.setAdapter(adapter);
            super.removeFooterView(dummy);
            return;
        }
        throw new IllegalArgumentException(new StringBuilder(String.valueOf(AmazingListView.class.getSimpleName())).append(" must use adapter of type ").append(AmazingAdapter.class.getSimpleName()).toString());
    }

    public ListAdapter getAdapter() {
        return this.adapter;
    }

    public AmazingAdapter getAmazingAdapter() {
        return (AmazingAdapter) this.adapter;
    }

    public void noMorePages() {
        if (this.listFooter != null) {
            removeFooterView(this.listFooter);
        }
        this.footerViewAttached = false;
    }

    public void mayHaveMorePages() {
        if (!this.footerViewAttached && this.listFooter != null) {
            addFooterView(this.listFooter);
            this.footerViewAttached = true;
        }
    }

    public boolean isLoadingViewVisible() {
        return this.footerViewAttached;
    }

    protected void layoutChildren() {
        try {
            super.layoutChildren();
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("The content of the adapter has changed but ListView did not receive a notification.")) {
                Log.w(TAG, "Data is refreshed in the background and access is invalid", e);
                return;
            }
            throw e;
        }
    }
}
