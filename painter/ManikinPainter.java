package uk.co.odeon.androidapp.painter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class ManikinPainter {
    private int backgroundColor;
    private int color;
    private Paint cutPaint;
    private float density;
    private Paint paint;
    private RectF rect;

    public ManikinPainter(RectF rect, int color, int backgroundColor, float density) {
        this.rect = rect;
        this.color = color;
        this.backgroundColor = backgroundColor;
        this.density = density;
    }

    protected Paint getPaint() {
        if (this.paint == null) {
            this.paint = new Paint();
            this.paint.setAntiAlias(true);
            this.paint.setColor(-1);
            this.paint.setStyle(Style.FILL);
        }
        return this.paint;
    }

    protected Paint getCutPaint() {
        if (this.cutPaint == null) {
            this.cutPaint = new Paint();
            this.cutPaint.setAntiAlias(true);
            this.cutPaint.setColor(-16777216);
            this.cutPaint.setStyle(Style.FILL);
        }
        return this.cutPaint;
    }

    public void paint(Canvas canvas) {
        Paint p = getPaint();
        this.paint.setColor(this.color);
        Paint pb = getCutPaint();
        pb.setColor(this.backgroundColor);
        RectF r = new RectF(this.rect);
        r.inset(this.density, this.density);
        canvas.save();
        canvas.clipRect(r);
        float h = r.height() / 2.0f;
        RectF r2 = new RectF();
        r2.left = r.centerX() - (r.width() / 2.5f);
        r2.right = r.centerX() + (r.width() / 2.5f);
        r2.top = (r.top + h) + (0.5f * this.density);
        r2.bottom = r.bottom + h;
        canvas.drawRoundRect(r2, r2.width() / 7.0f, r2.width() / 7.0f, p);
        canvas.drawCircle(r.centerX(), r.top + (h / 1.5f), h / 1.5f, pb);
        canvas.drawCircle(r.centerX(), r.top + (h / 1.5f), h / 1.75f, p);
        canvas.restore();
    }
}
