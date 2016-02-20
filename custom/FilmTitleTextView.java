package uk.co.odeon.androidapp.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class FilmTitleTextView extends TextView {
    protected static Integer availWidth;
    protected boolean autoScaled;

    static {
        availWidth = null;
    }

    public FilmTitleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.autoScaled = false;
        init();
    }

    public FilmTitleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.autoScaled = false;
        init();
    }

    public FilmTitleTextView(Context context) {
        super(context);
        this.autoScaled = false;
        init();
    }

    public void init() {
        setTextSize(1, 14.0f);
        if (availWidth != null) {
            autoScale();
        }
    }

    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        this.autoScaled = false;
    }

    protected void autoScale() {
        if (getPaint().measureText(getText().toString()) > ((float) availWidth.intValue())) {
            setTextSize(1, 12.0f);
        }
        this.autoScaled = false;
    }

    public boolean onPreDraw() {
        if (!this.autoScaled) {
            if (getWidth() > 0) {
                availWidth = Integer.valueOf(getWidth());
            }
            autoScale();
        }
        return super.onPreDraw();
    }
}
