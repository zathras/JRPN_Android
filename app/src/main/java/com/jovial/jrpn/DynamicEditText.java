package com.jovial.jrpn;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;

// a modification of the normal EditText box that allows for the size
// of the font to dynamically change based upon the length of the string
public class DynamicEditText extends AppCompatEditText {

    private final static String MAX_LARGE_TEXT = "88888888888888888888";
    private final static String MAX_SMALL_TEXT = " 00000000 00000000 00000000 00000000 .b.";

    private float largeTextSize = 20f;
    private float smallTextSize = 10f;

    public DynamicEditText(Context context) {
        super(context);
    }

    public DynamicEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.DynamicEditText);
        a.recycle();
    }

    public DynamicEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.DynamicEditText);
        a.recycle();
    }

    public void setTextSizes(ScaleInfo scaleInfo, int width) {
        // calculate the size of the font to fill the screen
        float padding = 2f*getPaddingLeft(); // @@  + scaleInfo.scale(5);
        largeTextSize = calculateDisplayFont(MAX_LARGE_TEXT, width, padding, scaleInfo);
        smallTextSize = calculateDisplayFont(MAX_SMALL_TEXT, width, padding, scaleInfo);
        setText(getText());
    }

    private float calculateDisplayFont(String text, float width, float padding, ScaleInfo scaleInfo) {
        float small = 1;
        float large = scaleInfo.scale(55);

        while (large - small > 0.25f) {
            float mid = (large + small) / 2;
            Paint p = getPaint();
            p.setTextSize(mid);
	    /* @@
            float w = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, p.measureText(text),
                    getResources().getDisplayMetrics());
		    */
            float w = p.measureText(text);
            if (w <= width - padding) {
                small = mid;
            } else {
                large = mid;
            }
        }
        return small;
    }


    public void setText(String text) {
        String temp = text.trim();

        if (temp.length() > MAX_LARGE_TEXT.length()) {
            this.setGravity(Gravity.AXIS_PULL_BEFORE + Gravity.CENTER);
            // no modification of either the text or the size
            setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);	// Raw pixels
            super.setText(text);
        } else {
            // adjust the padding
            if (text.length() > MAX_LARGE_TEXT.length()) {
                text = text.substring(text.length() - MAX_LARGE_TEXT.length());
            }

            this.setGravity(Gravity.TOP);
            setTextSize(TypedValue.COMPLEX_UNIT_PX, largeTextSize);	// Raw pixels
            super.setText(text);
        }
    }
}
