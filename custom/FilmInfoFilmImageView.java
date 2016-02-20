package uk.co.odeon.androidapp.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FilmInfoFilmImageView extends ImageView {
    public FilmInfoFilmImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FilmInfoFilmImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FilmInfoFilmImageView(Context context) {
        super(context);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), Math.round(((float) getMeasuredWidth()) / (((float) getDrawable().getIntrinsicWidth()) / ((float) getDrawable().getIntrinsicHeight()))));
    }
}
