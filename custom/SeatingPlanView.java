package uk.co.odeon.androidapp.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import de.sobutz.views.PanZoomView;
import uk.co.odeon.androidapp.model.BookingSeat;
import uk.co.odeon.androidapp.model.BookingSeatingData;
import uk.co.odeon.androidapp.model.BookingSection;
import uk.co.odeon.androidapp.painter.AnimatedSeatingPainter;
import uk.co.odeon.androidapp.painter.ScreenPainter;
import uk.co.odeon.androidapp.painter.SeatingPainter;

public class SeatingPlanView extends PanZoomView {
    private static final String TAG;
    private static final float ZOOM_STEP = 0.5f;
    private static final int ZOOM_WIDTH_VISIBLE = 150;
    private boolean animate;
    private final float density;
    private GestureDetector gestureDetector;
    private final OnGestureListener gestureListener;
    private float offsetForCentre;
    private OnDoubleTapListener onDoubleTapListener;
    private OnPlanTouchedListener onPlanTouchedListener;
    private Rect paddedRect;
    private ScreenPainter screenPainter;
    private BookingSeatingData seatingData;
    private AnimatedSeatingPainter seatingPainter;
    private Rect seatingRect;
    private float seatplanRatio;
    private float seatplanRatioHeight;
    private float seatplanRatioWidth;

    public interface OnDoubleTapListener {
        void onDoubleTapIn();

        void onDoubleTapOut();
    }

    public interface OnPlanTouchedListener {
        boolean onSeatTouched(BookingSeat bookingSeat);

        void onSeatTouchedLong(BookingSeat bookingSeat);
    }

    static {
        TAG = SeatingPlanView.class.getSimpleName();
    }

    public SeatingPlanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.gestureListener = new SimpleOnGestureListener() {
            public boolean onDoubleTap(MotionEvent e) {
                if (SeatingPlanView.this.isMaxZoomed()) {
                    SeatingPlanView.this.setZoom(SeatingPlanView.this.getMinZoom(), false);
                    if (SeatingPlanView.this.onDoubleTapListener != null) {
                        SeatingPlanView.this.onDoubleTapListener.onDoubleTapOut();
                    }
                } else {
                    SeatingPlanView.this.setZoom(SeatingPlanView.this.getMaxZoom(), e.getX(), e.getY(), true);
                    if (SeatingPlanView.this.onDoubleTapListener != null) {
                        SeatingPlanView.this.onDoubleTapListener.onDoubleTapIn();
                    }
                }
                return true;
            }

            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (SeatingPlanView.this.seatingPainter == null || SeatingPlanView.this.onPlanTouchedListener == null) {
                    return false;
                }
                return SeatingPlanView.this.onPlanTouchedListener.onSeatTouched(SeatingPlanView.this.seatingPainter.findSeat(SeatingPlanView.this.translateX(e.getX()), SeatingPlanView.this.translateY(e.getY())));
            }

            public void onLongPress(MotionEvent e) {
                if (SeatingPlanView.this.seatingPainter != null && SeatingPlanView.this.onPlanTouchedListener != null) {
                    SeatingPlanView.this.onPlanTouchedListener.onSeatTouchedLong(SeatingPlanView.this.seatingPainter.findSeat(SeatingPlanView.this.translateX(e.getX()), SeatingPlanView.this.translateY(e.getY())));
                }
            }
        };
        this.gestureDetector = new GestureDetector(this.gestureListener);
        this.density = context.getResources().getDisplayMetrics().density;
        setBackgroundColor(-1);
    }

    protected float getMinZoom() {
        return 1.0f;
    }

    protected float getMaxZoom() {
        return ((float) getWidth()) / (150.0f * this.seatplanRatio);
    }

    public void setSeatingData(BookingSeatingData seatingData, boolean animate) {
        this.seatingData = seatingData;
        this.animate = animate;
        this.seatingPainter = null;
        invalidate();
    }

    protected void onDrawContent(Canvas canvas) {
        this.paddedRect = getPaddedRect();
        this.seatingRect = new Rect();
        this.seatingRect.left = this.paddedRect.left;
        this.seatingRect.top = this.paddedRect.top;
        this.seatingRect.right = this.paddedRect.right;
        this.seatingRect.bottom = (int) (((float) this.paddedRect.bottom) - (((float) ((getHeight() / 100) * 10)) * this.density));
        refreshSeatplanRatio();
        ScreenPainter screenPainter = getScreenPainter();
        if (screenPainter != null) {
            screenPainter.paint(canvas);
        }
        SeatingPainter painter = getSeatingPainter();
        if (painter != null) {
            painter.paint(canvas);
        }
    }

    private Rect getPaddedRect() {
        return new Rect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
    }

    private void refreshSeatplanRatio() {
        try {
            this.seatplanRatioWidth = ((float) this.seatingRect.width()) / ((float) this.seatingData.seatingPlanWidth);
            this.seatplanRatioHeight = ((float) this.seatingRect.height()) / ((float) this.seatingData.seatingPlanHeight);
            if (this.seatplanRatioWidth >= 1.0f && this.seatplanRatioHeight >= 1.0f) {
                this.seatplanRatio = 1.0f;
            } else if (this.seatplanRatioWidth <= this.seatplanRatioHeight) {
                this.seatplanRatio = this.seatplanRatioWidth;
            } else {
                this.seatplanRatio = this.seatplanRatioHeight;
            }
            this.offsetForCentre = (((float) this.paddedRect.width()) - (((float) this.seatingData.seatingPlanWidth) * this.seatplanRatio)) / 2.0f;
        } catch (Exception e) {
            Log.w(TAG, "Could not calculate seatplan ratio: " + e.toString(), e);
        }
    }

    private AnimatedSeatingPainter getSeatingPainter() {
        if (this.seatingPainter == null && this.seatingData != null) {
            this.seatingPainter = new AnimatedSeatingPainter(this, this.seatingData, this.seatingRect, this.density, this.seatplanRatio, this.offsetForCentre);
            if (this.animate) {
                this.seatingPainter.animate();
                this.animate = false;
            }
        }
        return this.seatingPainter;
    }

    private ScreenPainter getScreenPainter() {
        if (this.screenPainter == null) {
            this.screenPainter = new ScreenPainter(getContext(), getPaddedRect(), this.seatingData != null ? Math.min((float) this.seatingRect.bottom, ((float) this.seatingData.seatingPlanHeight) * this.seatplanRatio) : (float) this.seatingRect.bottom, this.density, this.seatplanRatio);
        }
        return this.screenPainter;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.seatingPainter = null;
        this.screenPainter = null;
    }

    public OnPlanTouchedListener getOnPlanTouchedListener() {
        return this.onPlanTouchedListener;
    }

    public void setOnPlanTouchedListener(OnPlanTouchedListener onPlanTouchedListener) {
        this.onPlanTouchedListener = onPlanTouchedListener;
    }

    public OnDoubleTapListener getOnDoubleTapListener() {
        return this.onDoubleTapListener;
    }

    public void setOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener) {
        this.onDoubleTapListener = onDoubleTapListener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.seatingPainter != null) {
            this.seatingPainter.stopAnimation();
        }
        if (this.gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void zoomTo(BookingSection section) {
        if (!isMaxZoomed()) {
            Rect r = this.seatingPainter.getSectionRect(section, true);
            setZoom(getMaxZoom(), (float) r.centerX(), (float) r.centerY(), true);
        }
    }

    public void zoomTo(BookingSeat seat) {
        if (!isMaxZoomed()) {
            Rect r = this.seatingPainter.getSeatRect(seat);
            setZoom(getMaxZoom(), (float) r.centerX(), (float) r.centerY(), true);
        }
    }

    public float getZoomStep() {
        return ZOOM_STEP;
    }

    protected void onDetachedFromWindow() {
        if (this.seatingPainter != null) {
            this.seatingPainter.stopAnimation();
        }
        super.onDetachedFromWindow();
    }
}
