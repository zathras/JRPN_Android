package com.emmetgray.wrpn;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsoluteLayout;

@SuppressWarnings("deprecation")
public class CalcFace extends AbsoluteLayout {
    OnResizeListener pListener = null;

    public CalcFace(Context context) {
        super(context);
    }

    public CalcFace(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalcFace(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void SetOnResizeListener(OnResizeListener listener) {
        pListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (pListener != null) {
            pListener.OnResize(this.getId(), w, h, oldw, oldh);
        }
    }

}
