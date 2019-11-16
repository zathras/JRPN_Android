package com.jovial.jrpn;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;

public class IconView extends android.view.View {

    private ScaleInfo scaleInfo;
    private static String logoText = "JRPN";
    private float logoTextWidth;
    private Bitmap jupiter;
    private RectF jupiterDest;
    private float border;

    public IconView(Context context) {
        super(context);
        init();
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setScaleInfo(ScaleInfo scaleInfo) {
        this.scaleInfo = scaleInfo;
    }

    private void init() {
        jupiter = BitmapFactory.decodeResource(getResources(), R.mipmap.jupiter);
    }

    public void resize(MyAbsoluteLayout parent) {
        logoTextWidth = scaleInfo.logoPaint.measureText(logoText);  // Move to resize
        int width = (int) (logoTextWidth * 1.2f);
        int height = (int) (width * 1.4f);
        int x, y;
        if (scaleInfo.isLandscape) {
            x = scaleInfo.scaleX(458);
            y = scaleInfo.scaleY(19);
        } else {
            x = scaleInfo.scaleX(23);
            y = scaleInfo.scaleY(449);
            int maxY = scaleInfo.scaleY(488);   // EMMET-GRAY text starts at 490
            if (y + height > maxY) {
                y = maxY - height;
            }
        }
        border = scaleInfo.scale(0.015f * width);
        float destOffset = border * 1.2f;
        jupiterDest = new RectF(destOffset, destOffset, width - destOffset, width - destOffset);
        scaleInfo.logoPaint.setStrokeWidth(border/2f);
        parent.updateViewLayout(this, new MyAbsoluteLayout.LayoutParams(width, height, x, y));
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int h = getHeight();
        int w = getWidth();
        canvas.drawRect(0, 0, w, h, scaleInfo.logoPaint);
        canvas.drawRect(border, border, w-border, h-border, scaleInfo.faceTextPaint);  // Silver
        canvas.drawBitmap(jupiter, null, jupiterDest, scaleInfo.logoPaint);
        canvas.drawLine(0f, h*0.72f, w, h*0.72f, scaleInfo.logoPaint);  // Black line
        canvas.drawText("JRPN", (w - logoTextWidth) * 0.5f, h * 0.94f, scaleInfo.logoPaint);
    }

}
