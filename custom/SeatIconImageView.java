package uk.co.odeon.androidapp.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import uk.co.odeon.androidapp.painter.ManikinPainter;

public class SeatIconImageView extends View {
    public static int COLOR_PLACED;
    public static int COLOR_UNPLACED;
    public int color;

    static {
        COLOR_PLACED = -8355712;
        COLOR_UNPLACED = -1;
    }

    public SeatIconImageView(Context context) {
        super(context);
        this.color = COLOR_UNPLACED;
        setLayoutParams(new LayoutParams((int) TypedValue.applyDimension(1, 30.0f, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(1, 30.0f, getResources().getDisplayMetrics())));
    }

    protected void onDraw(Canvas canvas) {
        new ManikinPainter(new RectF((float) canvas.getClipBounds().left, (float) canvas.getClipBounds().top, (float) canvas.getClipBounds().right, (float) canvas.getClipBounds().bottom), this.color, -16777216, getResources().getDisplayMetrics().density).paint(canvas);
    }
}
