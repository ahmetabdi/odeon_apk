package uk.co.odeon.androidapp.util.amazinglist;

import android.view.View;

public interface AmazingAdapter {
    void configurePinnedHeader(View view, int i, int i2);

    int getPinnedHeaderState(int i);

    int getPositionForSection(int i);

    int getSectionForPosition(int i);

    Object[] getSections();

    void setHasMorePagesListener(HasMorePagesListener hasMorePagesListener);
}
