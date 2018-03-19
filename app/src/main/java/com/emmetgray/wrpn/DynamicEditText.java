package com.emmetgray.wrpn;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

// a modification of the normal EditText box that allows for the size
// of the font to dynamically change based upon the length of the string
public class DynamicEditText extends EditText {
    private float pBaseFontSize = 10f;
    private int pLengthThreshold = -1;

    public DynamicEditText(Context context) {
        super(context);
    }

    public DynamicEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.DynamicEditText);
        pBaseFontSize = a.getFloat(R.styleable.DynamicEditText_baseFontSize,
                10f);
        pLengthThreshold = a.getInt(
                R.styleable.DynamicEditText_lengthThreshold, -1);
        a.recycle();
    }

    public DynamicEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.DynamicEditText);
        pBaseFontSize = a.getFloat(R.styleable.DynamicEditText_baseFontSize,
                10f);
        pLengthThreshold = a.getInt(
                R.styleable.DynamicEditText_lengthThreshold, -1);
        a.recycle();
    }

    // this is the "standard" (smaller) size font
    public float getBaseFontSize() {
        return pBaseFontSize;
    }

    public void setBaseFontSize(float size) {
        pBaseFontSize = size;
    }

    // this is the threshold for switching to the smaller font
    public int getLengthThreshold() {
        return pLengthThreshold;
    }

    public void setLengthThreshold(int length) {
        pLengthThreshold = length;
    }

    public void setText(String text) {
        String temp = text.trim();

        if (temp.length() > pLengthThreshold) {
            this.setGravity(Gravity.AXIS_PULL_BEFORE + Gravity.CENTER);
            // no modification of either the text or the size
            this.setTextSize(pBaseFontSize);
            super.setText(text);
        } else {
            this.setGravity(Gravity.TOP);
            this.setTextSize(pBaseFontSize * 2f);
            // adjust the padding
            if (text.startsWith(" ")) {
                text = text.substring(pLengthThreshold);
            }
            super.setText(text);
        }
    }
}
