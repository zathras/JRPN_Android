package com.emmetgray.wrpn;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

// A graphical button class that includes a few extra
// properties (and some default values set the way I like 'em)
public class GButton extends ImageButton {
    private int pX, pY, pKeyCode;

    public GButton(Context context) {
        super(context);
        init();
    }

    public GButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    // set the default values
    private void init() {
        pX = 0;
        pY = 0;
        pKeyCode = 0;
        this.setPadding(0, 0, 0, 0);
        this.setScaleType(ScaleType.FIT_XY);
    }

    // pull the attributes into normal properties
    private void init(AttributeSet attrs) {
        init();

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.GButton);
        pX = a.getInt(R.styleable.GButton_originalX, 0);
        pY = a.getInt(R.styleable.GButton_originalY, 0);
        pKeyCode = a.getInt(R.styleable.GButton_keyCode, 0);
        a.recycle();
    }

    public int getOriginalX() {
        return pX;
    }

    public int getOriginalY() {
        return pY;
    }

    public int getKeyCode() {
        return pKeyCode;
    }
}
