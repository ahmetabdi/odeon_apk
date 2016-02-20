package uk.co.odeon.androidapp.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View.MeasureSpec;
import android.widget.TextView;

public class AutofitTextView extends TextView {
    private static final int DEFAULT_MIN_TEXT_SIZE = 8;
    private static final float PRECISION = 0.5f;
    private static final boolean SPEW = false;
    private static final String TAG;
    private float mMaxTextSize;
    private float mMinTextSize;
    private Paint mPaint;
    private float mPrecision;

    static {
        TAG = AutofitTextView.class.getSimpleName();
    }

    public AutofitTextView(Context context) {
        super(context);
        init();
    }

    public AutofitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.mMinTextSize = 8.0f;
        this.mMaxTextSize = getTextSize();
        this.mPrecision = PRECISION;
        this.mPaint = new Paint();
    }

    public float getMinTextSize() {
        return this.mMinTextSize;
    }

    public void setMinTextSize(int minTextSize) {
        this.mMinTextSize = (float) minTextSize;
    }

    public float getMaxTextSize() {
        return this.mMaxTextSize;
    }

    public void setMaxTextSize(int maxTextSize) {
        this.mMaxTextSize = (float) maxTextSize;
    }

    public float getPrecision() {
        return this.mPrecision;
    }

    public void setPrecision(float precision) {
        this.mPrecision = precision;
    }

    private void refitText(String text, int width) {
        if (width > 0) {
            Context context = getContext();
            Resources r = Resources.getSystem();
            int targetWidth = (width - getPaddingLeft()) - getPaddingRight();
            float newTextSize = this.mMaxTextSize;
            float high = this.mMaxTextSize;
            if (context != null) {
                r = context.getResources();
            }
            this.mPaint.set(getPaint());
            this.mPaint.setTextSize(newTextSize);
            if (this.mPaint.measureText(text) > ((float) targetWidth)) {
                newTextSize = getTextSize(r, text, (float) targetWidth, 0.0f, high);
                if (newTextSize < this.mMinTextSize) {
                    newTextSize = this.mMinTextSize;
                }
            }
            setTextSize(0, newTextSize);
        }
    }

    private float getTextSize(Resources resources, String text, float targetWidth, float low, float high) {
        float mid = (low + high) / 2.0f;
        this.mPaint.setTextSize(TypedValue.applyDimension(0, mid, resources.getDisplayMetrics()));
        float textWidth = this.mPaint.measureText(text);
        if (high - low < this.mPrecision) {
            return low;
        }
        if (textWidth > targetWidth) {
            return getTextSize(resources, text, targetWidth, low, mid);
        }
        return textWidth < targetWidth ? getTextSize(resources, text, targetWidth, mid, high) : mid;
    }

    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        refitText(text.toString(), getWidth());
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw) {
            refitText(getText().toString(), w);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        refitText(getText().toString(), MeasureSpec.getSize(widthMeasureSpec));
    }
}
