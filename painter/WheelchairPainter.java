package uk.co.odeon.androidapp.painter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class WheelchairPainter {
    private int backgroundColor;
    private int color;
    private Paint cutPaint;
    private float density;
    private Paint paint;
    private RectF rect;
    private float seatplanRatio;
    private Paint strokePaint;

    public WheelchairPainter(RectF rect, int color, int backgroundColor, float density, float seatplanRatio) {
        this.rect = rect;
        this.color = color;
        this.backgroundColor = backgroundColor;
        this.density = density;
        this.seatplanRatio = seatplanRatio;
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

    protected Paint getStrokePaint() {
        if (this.strokePaint == null) {
            this.strokePaint = new Paint();
            this.strokePaint.setAntiAlias(true);
            this.strokePaint.setColor(-1);
            this.strokePaint.setStyle(Style.STROKE);
            this.strokePaint.setStrokeWidth((1.0f * this.seatplanRatio) * this.density);
        }
        return this.strokePaint;
    }

    public void paint(Canvas canvas) {
        Paint p = getPaint();
        p.setColor(this.color);
        Paint pCut = getCutPaint();
        pCut.setColor(this.backgroundColor);
        Paint pStroke = getStrokePaint();
        pStroke.setColor(this.color);
        RectF rectF = new RectF(this.rect);
        rectF.inset(this.density, this.density);
        canvas.save();
        canvas.clipRect(rectF);
        float onePx = (1.0f * this.seatplanRatio) * this.density;
        float circleX = rectF.centerX() - (rectF.width() / 10.0f);
        float circleY = rectF.centerY() + (rectF.height() / 10.0f);
        float circleR = Math.min((rectF.right - circleX) - onePx, (rectF.bottom - circleY) - onePx);
        float bodyX = circleX;
        float bodyY = circleY;
        float bodyHeight = (bodyY - rectF.top) - ((bodyY - rectF.top) / 5.0f);
        float armX = bodyX;
        float armY = bodyY - (bodyHeight / 2.0f);
        float armLenght = (rectF.right - bodyX) / 3.0f;
        float tightX = bodyX - (pStroke.getStrokeWidth() / 2.0f);
        float tightY = bodyY;
        float tightLenght = ((rectF.right - bodyX) / 3.0f) * 2.0f;
        float legX = (tightX + tightLenght) - (pStroke.getStrokeWidth() / 2.0f);
        float legY = circleY;
        float legLenghtX = legX + ((rectF.right - bodyX) / 10.0f);
        float legLenghtY = legY + (((rectF.bottom - legY) / 3.0f) * 2.0f);
        float feetX = legLenghtX - (pStroke.getStrokeWidth() / 2.0f);
        float feetY = legLenghtY;
        float feetLenght = rectF.right - feetX;
        RectF bodyC = new RectF();
        bodyC.left = bodyX - onePx;
        bodyC.right = rectF.right;
        bodyC.top = rectF.top;
        bodyC.bottom = bodyY + onePx;
        float headX = bodyX;
        float headY = bodyY - bodyHeight;
        float headR = (bodyY - bodyHeight) - rectF.top;
        canvas.drawCircle(circleX, circleY, circleR, pStroke);
        canvas.drawRect(bodyC, pCut);
        canvas.drawCircle(legX, legY, 2.0f * onePx, pCut);
        canvas.drawLine(bodyX, bodyY, bodyX, bodyY - bodyHeight, pStroke);
        canvas.drawLine(armX, armY, armX + armLenght, armY, pStroke);
        canvas.drawLine(tightX, tightY, tightX + tightLenght, tightY, pStroke);
        canvas.drawLine(legX, legY, legLenghtX, legLenghtY, pStroke);
        canvas.drawLine(feetX, feetY, feetX + feetLenght, feetY, pStroke);
        canvas.drawCircle(headX, headY, headR, p);
        canvas.restore();
    }
}
