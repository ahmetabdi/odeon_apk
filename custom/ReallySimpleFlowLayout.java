package uk.co.odeon.androidapp.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import uk.co.odeon.androidapp.R;

public class ReallySimpleFlowLayout extends ViewGroup {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static int DEFAULT_HORIZONTAL_SPACING;
    private static int DEFAULT_VERTICAL_SPACING;
    private final int horizontalSpacing;
    private int lineHeight;
    private final int verticalSpacing;

    static {
        $assertionsDisabled = !ReallySimpleFlowLayout.class.desiredAssertionStatus();
        DEFAULT_VERTICAL_SPACING = 5;
        DEFAULT_HORIZONTAL_SPACING = 5;
    }

    public ReallySimpleFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.ReallySimpleFlowLayout);
        this.horizontalSpacing = styledAttributes.getDimensionPixelSize(0, DEFAULT_HORIZONTAL_SPACING);
        this.verticalSpacing = styledAttributes.getDimensionPixelSize(1, DEFAULT_VERTICAL_SPACING);
        styledAttributes.recycle();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if ($assertionsDisabled || MeasureSpec.getMode(widthMeasureSpec) != 0) {
            int pl = getPaddingLeft();
            int pt = getPaddingTop();
            int pr = getPaddingRight();
            int pb = getPaddingBottom();
            int fullWidth = MeasureSpec.getSize(widthMeasureSpec);
            int width = (fullWidth - pl) - pr;
            int childCount = getChildCount();
            int height = (MeasureSpec.getSize(heightMeasureSpec) - pt) - pb;
            this.lineHeight = 0;
            int x = 0;
            int y = 0;
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (!(child == null || child.getVisibility() == 8)) {
                    if (MeasureSpec.getMode(heightMeasureSpec) == Integer.MIN_VALUE) {
                        child.measure(MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
                    } else {
                        child.measure(MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(0, 0));
                    }
                    int childWidth = child.getMeasuredWidth();
                    this.lineHeight = Math.max(this.lineHeight, child.getMeasuredHeight());
                    if (x + childWidth > width) {
                        x = 0;
                        y += this.lineHeight + this.verticalSpacing;
                    }
                    x += this.horizontalSpacing + childWidth;
                }
            }
            if (MeasureSpec.getMode(heightMeasureSpec) != Integer.MIN_VALUE) {
                height = ((this.lineHeight + y) + pt) + pb;
            }
            setMeasuredDimension(fullWidth, height);
            return;
        }
        throw new AssertionError();
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int width = r - l;
        int horizontalCenterOffset = calculateHorizontalCenterOffset(l, t, r, b);
        int x = getPaddingLeft() + horizontalCenterOffset;
        int y = getPaddingTop();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (!(child == null || child.getVisibility() == 8)) {
                int childw = child.getMeasuredWidth();
                int childh = child.getMeasuredHeight();
                if (x + childw > width - getPaddingRight()) {
                    x = getPaddingLeft() + horizontalCenterOffset;
                    y += this.lineHeight + this.verticalSpacing;
                }
                child.layout(x, y, x + childw, y + childh);
                x += this.horizontalSpacing + childw;
            }
        }
    }

    private int calculateHorizontalCenterOffset(int l, int t, int r, int b) {
        int count = getChildCount();
        int width = r - l;
        int x = getPaddingLeft();
        int maxChildWidth = 0;
        int sumChildWidth = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (!(child == null || child.getVisibility() == 8)) {
                int childw = child.getMeasuredWidth();
                if (x + childw > width - getPaddingRight()) {
                    x = getPaddingLeft();
                    maxChildWidth = Math.max(maxChildWidth, sumChildWidth);
                    sumChildWidth = 0;
                }
                sumChildWidth += this.horizontalSpacing + childw;
                x += this.horizontalSpacing + childw;
            }
        }
        if (maxChildWidth <= 0) {
            maxChildWidth = sumChildWidth;
        }
        return (((width - getPaddingLeft()) - getPaddingRight()) - (maxChildWidth - this.horizontalSpacing)) / 2;
    }
}
