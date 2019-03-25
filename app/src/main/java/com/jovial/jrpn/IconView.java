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

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int h = getHeight();
        int w = getWidth();
        logoTextWidth = scaleInfo.logoPaint.measureText(logoText);  // Move to resize
        canvas.drawRect(0, 0, w, h, scaleInfo.logoPaint);
        float border = scaleInfo.scale(0.015f * w);
        canvas.drawRect(border, border, w-border, h-border, scaleInfo.faceTextPaint);  // Silver
        scaleInfo.logoPaint.setStrokeWidth(border/2f);
        RectF dest = new RectF(border, border, w - border, w - border);
        canvas.drawBitmap(jupiter, null, dest, scaleInfo.logoPaint);
        canvas.drawLine(0f, h*0.72f, w, h*0.72f, scaleInfo.logoPaint);  // Black line
        canvas.drawText("JRPN", (w - logoTextWidth) * 0.5f, h * 0.95f, scaleInfo.logoPaint);
        // TODO
    }

}
