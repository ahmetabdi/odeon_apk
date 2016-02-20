package uk.co.odeon.androidapp.painter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;
import de.sobutz.anim.util.AccelerateDecelerateValue;
import java.util.Stack;
import uk.co.odeon.androidapp.model.BookingSeatingData;
import uk.co.odeon.androidapp.model.BookingSection;

public class AnimatedSeatingPainter extends SeatingPainter {
    private static final int ANIM_LENGTH = 1000;
    private static final float MAX_ALPHA = 225.0f;
    private static final float MIN_ALPHA = 0.0f;
    private final Runnable animRunnable;
    private Stack<BookingSection> animSections;
    private AccelerateDecelerateValue animValue;
    private Paint sectionPaint;
    private final View view;

    public AnimatedSeatingPainter(View view, BookingSeatingData seatingData, Rect rect, float density, float seatplanRatio, float offsetForCentre) {
        super(seatingData, rect, density, seatplanRatio, offsetForCentre);
        this.animRunnable = new Runnable() {
            public void run() {
                long now = SystemClock.uptimeMillis();
                AnimatedSeatingPainter.this.animValue.update(now);
                if (AnimatedSeatingPainter.this.animValue.isResting()) {
                    if (AnimatedSeatingPainter.this.animValue.getValue() == AnimatedSeatingPainter.MAX_ALPHA) {
                        AnimatedSeatingPainter.this.animValue.start(0.0f);
                    } else {
                        AnimatedSeatingPainter.this.animValue.start(AnimatedSeatingPainter.MAX_ALPHA);
                        AnimatedSeatingPainter.this.animSections.pop();
                        AnimatedSeatingPainter.this.showSectionToast();
                    }
                }
                if (!AnimatedSeatingPainter.this.animSections.isEmpty()) {
                    AnimatedSeatingPainter.this.view.getHandler().postDelayed(this, 20 - (SystemClock.uptimeMillis() - now));
                }
                AnimatedSeatingPainter.this.view.invalidate();
            }
        };
        this.view = view;
        this.animSections = new Stack();
    }

    private void showSectionToast() {
        if (!this.animSections.isEmpty()) {
            Toast.makeText(this.view.getContext(), ((BookingSection) this.animSections.peek()).name, 0).show();
        }
    }

    public void animate() {
        for (BookingSection s : getSeatingData().sections) {
            this.animSections.push(s);
        }
        this.animValue = new AccelerateDecelerateValue(0.0f, 1000.0f);
        this.animValue.start(MAX_ALPHA);
        this.view.getHandler().post(this.animRunnable);
        showSectionToast();
    }

    public void stopAnimation() {
        this.view.getHandler().removeCallbacks(this.animRunnable);
        this.animSections.clear();
    }

    private Paint getSectionPaint() {
        if (this.sectionPaint == null) {
            this.sectionPaint = new Paint();
            this.sectionPaint.setAntiAlias(true);
            this.sectionPaint.setColor(-1);
            this.sectionPaint.setStyle(Style.FILL);
        }
        return this.sectionPaint;
    }

    public void paint(Canvas canvas) {
        super.paint(canvas);
        if (!this.animSections.isEmpty()) {
            RectF r = new RectF(getSectionRect((BookingSection) this.animSections.peek(), true));
            if (!r.isEmpty()) {
                Paint p = getSectionPaint();
                p.setAlpha((int) this.animValue.getValue());
                canvas.drawRoundRect(r, 2.0f, 2.0f, p);
            }
        }
    }
}
