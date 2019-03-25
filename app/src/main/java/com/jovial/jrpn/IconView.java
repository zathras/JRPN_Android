package com.jovial.jrpn;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class IconView extends android.view.View {

    private ScaleInfo scaleInfo;

    public IconView(Context context) {
        super(context);
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setScaleInfo(ScaleInfo scaleInfo) {
        this.scaleInfo = scaleInfo;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, 1000, 1000, scaleInfo.faceBgPaint);
        // TODO
    }

}
