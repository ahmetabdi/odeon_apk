package uk.co.odeon.androidapp.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

public class SpinnerWithHint extends Spinner {
    private boolean isFirstClick;
    public OnPerformClickListener onPerformClickListener;

    public interface OnPerformClickListener {
        void onFirstPerformClick();

        void onPerformClick();
    }

    public SpinnerWithHint(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.onPerformClickListener = null;
        this.isFirstClick = true;
    }

    public boolean performClick() {
        if (this.onPerformClickListener != null) {
            this.onPerformClickListener.onPerformClick();
        }
        if (!this.isFirstClick || this.onPerformClickListener == null) {
            return super.performClick();
        }
        this.isFirstClick = false;
        this.onPerformClickListener.onFirstPerformClick();
        return false;
    }
}
