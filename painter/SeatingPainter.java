package uk.co.odeon.androidapp.painter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import uk.co.odeon.androidapp.model.BookingSeat;
import uk.co.odeon.androidapp.model.BookingSeat.Type;
import uk.co.odeon.androidapp.model.BookingSeatingData;
import uk.co.odeon.androidapp.model.BookingSection;
import uk.co.odeon.androidapp.model.BookingSection.Mode;

public class SeatingPainter {
    protected static final int COLOR_BLOCKED = -1;
    private float density;
    private float offsetForCentre;
    private Paint paint;
    private Rect rect;
    private BookingSeatingData seatingData;
    private float seatplanRatio;

    public SeatingPainter(BookingSeatingData seatingData, Rect rect, float density, float seatplanRatio, float offsetForCentre) {
        this.seatingData = seatingData;
        this.rect = rect;
        this.density = density;
        this.seatplanRatio = seatplanRatio;
        this.offsetForCentre = offsetForCentre;
    }

    protected Paint getPaint() {
        if (this.paint == null) {
            this.paint = new Paint();
            this.paint.setAntiAlias(true);
            this.paint.setColor(COLOR_BLOCKED);
            this.paint.setStyle(Style.FILL);
        }
        return this.paint;
    }

    protected BookingSeatingData getData() {
        return this.seatingData;
    }

    public Rect getSectionRect(BookingSection section, boolean skipSpecialSeats) {
        Rect r = new Rect();
        for (BookingSeat seat : section.seats) {
            if (skipSpecialSeats && !seat.isSpecialSeat()) {
                r.union(getSeatRect(seat));
            }
        }
        return r;
    }

    public Rect getSeatRect(BookingSeat seat) {
        Rect r = new Rect();
        r.left = (int) ((((float) (seat.xPosition + 1)) * this.seatplanRatio) + this.offsetForCentre);
        r.top = (int) (((float) (seat.yPosition + 1)) * this.seatplanRatio);
        r.right = (int) ((((float) ((seat.xPosition + seat.width) + COLOR_BLOCKED)) * this.seatplanRatio) + this.offsetForCentre);
        r.bottom = (int) (((float) ((seat.yPosition + seat.height) + COLOR_BLOCKED)) * this.seatplanRatio);
        return r;
    }

    public void paint(Canvas canvas) {
        Paint p = getPaint();
        int sectionIndex = 0;
        for (BookingSection section : this.seatingData.sections) {
            int sectionColor = (section.selected || !(this.seatingData.getSelectedSection() != null)) ? Color.parseColor(section.getSelectedColor()) : Color.parseColor(section.getUnselectedColor());
            for (BookingSeat seat : section.seats) {
                if (seat.isOccupied() && section.mode.equals(Mode.reserved)) {
                    p.setColor(COLOR_BLOCKED);
                } else if (seat.selected || seat.isOccupied() || !section.mode.equals(Mode.reserved) || !seat.type.equals(Type.wheelchair)) {
                    p.setColor(sectionColor);
                } else {
                    p.setColor(COLOR_BLOCKED);
                }
                RectF seatRect = new RectF(getSeatRect(seat));
                float corner = 1.5f * this.density;
                canvas.drawRoundRect(seatRect, corner, corner, p);
                if (seat.isOccupied() && section.mode.equals(Mode.reserved)) {
                    drawFatty(canvas, seatRect, sectionColor, COLOR_BLOCKED);
                } else if (!seat.selected && !seat.isOccupied() && section.mode.equals(Mode.reserved) && seat.type.equals(Type.wheelchair)) {
                    drawWheelchair(canvas, seatRect, sectionColor, COLOR_BLOCKED);
                } else if (seat.selected) {
                    drawFatty(canvas, seatRect, COLOR_BLOCKED, sectionColor);
                }
            }
            sectionIndex++;
        }
    }

    private void drawFatty(Canvas canvas, RectF rect, int color, int backgroundColor) {
        new ManikinPainter(rect, color, backgroundColor, this.density).paint(canvas);
    }

    private void drawWheelchair(Canvas canvas, RectF rect, int color, int backgroundColor) {
        new WheelchairPainter(rect, color, backgroundColor, this.density, this.seatplanRatio).paint(canvas);
    }

    public BookingSeat findSeat(float x, float y) {
        if (this.seatingData != null && this.rect.contains((int) x, (int) y)) {
            for (BookingSection section : this.seatingData.sections) {
                for (BookingSeat seat : section.seats) {
                    if ((((float) seat.xPosition) * this.seatplanRatio) + this.offsetForCentre <= x && (((float) (seat.xPosition + seat.width)) * this.seatplanRatio) + this.offsetForCentre >= x && ((float) seat.yPosition) * this.seatplanRatio <= y && ((float) (seat.yPosition + seat.height)) * this.seatplanRatio >= y && !seat.isSpecialSeat()) {
                        return seat;
                    }
                }
            }
        }
        return null;
    }

    public BookingSeatingData getSeatingData() {
        return this.seatingData;
    }
}
