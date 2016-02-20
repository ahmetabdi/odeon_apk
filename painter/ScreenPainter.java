package uk.co.odeon.androidapp.painter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import uk.co.odeon.androidapp.R;

public class ScreenPainter {
    private static final int BOX_COLOR = -16777216;
    public static final int CANVAS_MIN_HEIGHT_IN_PERCENTAGE = 10;
    private static final int SCREEN_HEIGHT_IN_PERCENTAGE = 70;
    private static final int SCREEN_HEIGHT_MAX = 70;
    private static final int TEXT_COLOR = -1;
    private static final int TEXT_HEIGHT_IN_PERCENTAGE = 30;
    private static final int TEXT_HEIGHT_MAX = 30;
    private Paint boxPaint;
    private RectF boxRect;
    private final float density;
    private final Rect rect;
    private int screenHeight;
    private Paint screenPaint;
    private Paint screenThicknessPaint;
    private final float seatplanBottom;
    private final float seatplanRatio;
    private final String text;
    private int textHeight;
    private Paint textPaint;

    public ScreenPainter(Context context, Rect rect, float seatplanBottom, float density, float seatplanRatio) {
        this.rect = rect;
        this.seatplanBottom = seatplanBottom;
        this.density = density;
        this.seatplanRatio = seatplanRatio;
        this.text = context.getResources().getString(R.string.seatplan_screen);
        doLayout();
    }

    private void doLayout() {
        this.boxRect = new RectF((float) this.rect.left, this.seatplanBottom, (float) this.rect.right, (float) this.rect.bottom);
        this.textHeight = Math.min((int) ((this.boxRect.height() / 100.0f) * 30.0f), TEXT_HEIGHT_MAX);
        this.screenHeight = Math.min((int) ((this.boxRect.height() / 100.0f) * 70.0f), SCREEN_HEIGHT_MAX);
    }

    private Paint getBoxPaint() {
        if (this.boxPaint == null) {
            this.boxPaint = new Paint();
            this.boxPaint.setAntiAlias(true);
            this.boxPaint.setColor(BOX_COLOR);
        }
        return this.boxPaint;
    }

    private Paint getTextPaint() {
        if (this.textPaint == null) {
            this.textPaint = new Paint();
            this.textPaint.setAntiAlias(true);
            this.textPaint.setColor(TEXT_COLOR);
            this.textPaint.setTextSize((float) this.textHeight);
            this.textPaint.setTextAlign(Align.CENTER);
        }
        return this.textPaint;
    }

    private Paint getScreenPaint() {
        if (this.screenPaint == null) {
            this.screenPaint = new Paint();
            this.screenPaint.setAntiAlias(true);
            this.screenPaint.setColor(-3355444);
            this.screenPaint.setStyle(Style.FILL_AND_STROKE);
        }
        return this.screenPaint;
    }

    private Paint getScreenThicknessPaint() {
        if (this.screenThicknessPaint == null) {
            this.screenThicknessPaint = new Paint();
            this.screenThicknessPaint.setAntiAlias(true);
            this.screenThicknessPaint.setColor(-7829368);
            this.screenThicknessPaint.setStyle(Style.STROKE);
        }
        return this.screenThicknessPaint;
    }

    public void paint(Canvas canvas) {
        canvas.save();
        canvas.clipRect(this.rect);
        float onePx = (1.0f * this.seatplanRatio) * this.density;
        float margin = 5.0f * onePx;
        float screenTop = (this.seatplanBottom + ((float) this.textHeight)) + margin;
        float screenPaddingTopEdge = 20.0f * onePx;
        float screenPaddingBottomEdge = 10.0f * onePx;
        float screenThickness = 7.0f * onePx;
        canvas.drawRect(this.boxRect, getBoxPaint());
        canvas.drawText(this.text, (float) this.rect.centerX(), this.seatplanBottom + ((float) this.textHeight), getTextPaint());
        Path path = new Path();
        path.moveTo(this.boxRect.left + screenPaddingTopEdge, screenTop);
        path.lineTo(this.boxRect.right - screenPaddingTopEdge, screenTop);
        path.lineTo(this.boxRect.right - screenPaddingBottomEdge, ((((float) this.screenHeight) + screenTop) - screenThickness) - margin);
        path.lineTo(this.boxRect.right - screenPaddingBottomEdge, (((float) this.screenHeight) + screenTop) - margin);
        path.lineTo(this.boxRect.left + screenPaddingBottomEdge, (((float) this.screenHeight) + screenTop) - margin);
        path.lineTo(this.boxRect.left + screenPaddingBottomEdge, ((((float) this.screenHeight) + screenTop) - screenThickness) - margin);
        path.lineTo(this.boxRect.left + screenPaddingTopEdge, screenTop);
        path.close();
        canvas.drawPath(path, getScreenPaint());
        canvas.drawLine(this.boxRect.left + screenPaddingBottomEdge, ((((float) this.screenHeight) + screenTop) - screenThickness) - margin, this.boxRect.right - screenPaddingBottomEdge, ((((float) this.screenHeight) + screenTop) - screenThickness) - margin, getScreenThicknessPaint());
        canvas.restore();
    }
}
