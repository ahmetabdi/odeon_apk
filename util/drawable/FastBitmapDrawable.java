package uk.co.odeon.androidapp.util.drawable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class FastBitmapDrawable extends Drawable {
    private final Bitmap bitmap;
    private Paint paint;

    public FastBitmapDrawable(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public FastBitmapDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            this.bitmap = ((BitmapDrawable) drawable).getBitmap();
            return;
        }
        this.bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
        if (this.bitmap != null) {
            drawable.draw(new Canvas(this.bitmap));
        }
    }

    private Paint getPaint() {
        if (this.paint == null) {
            this.paint = new Paint();
            this.paint.setAntiAlias(true);
            this.paint.setDither(false);
            this.paint.setFilterBitmap(true);
        }
        return this.paint;
    }

    public void draw(Canvas canvas) {
        if (this.bitmap != null) {
            canvas.drawBitmap(this.bitmap, 0.0f, 0.0f, getPaint());
        }
    }

    public int getOpacity() {
        return -3;
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getIntrinsicWidth() {
        if (this.bitmap == null) {
            return 0;
        }
        return this.bitmap.getWidth();
    }

    public int getIntrinsicHeight() {
        if (this.bitmap == null) {
            return 0;
        }
        return this.bitmap.getHeight();
    }

    public int getMinimumWidth() {
        if (this.bitmap == null) {
            return 0;
        }
        return this.bitmap.getWidth();
    }

    public int getMinimumHeight() {
        if (this.bitmap == null) {
            return 0;
        }
        return this.bitmap.getHeight();
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }
}
