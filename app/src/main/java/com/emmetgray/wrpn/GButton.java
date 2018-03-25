
/*
   Portions of this file copyright 2018 Bill Foote
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.emmetgray.wrpn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;

// A graphical button class that includes a few extra
// properties (and some default values set the way I like 'em)
@SuppressLint("AppCompatCustomView")
public class GButton extends ImageButton {
    private int pX, pY, pKeyCode;

    protected String whiteLabel;
    protected float whiteX, whiteY;
    protected String blueLabel;
    protected float blueX, blueY;

    protected ScaleInfo scaleInfo;

    public static void setupScaleInfo(ScaleInfo scaleInfo) {
        scaleInfo.whitePaint.setTextSize(scaleInfo.scale(140) / 10f);
        scaleInfo.whitePaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        scaleInfo.whitePaint.setColor(Color.argb(255, 255, 255, 255));
        scaleInfo.bluePaint.setTextSize(scaleInfo.scale(90) / 10f);
        scaleInfo.bluePaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        scaleInfo.bluePaint.setColor(Color.argb(255, 0, 100, 255));
    }

    public void setScaleInfo(ScaleInfo info) {
        scaleInfo = info;
    }
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

    public void setWhiteLabel(String str) {
        whiteLabel = str;
    }

    public void setBlueLabel(String str) {
        blueLabel = str;
    }

    public void alignText(int w, int h) {
        whiteX = (w - scaleInfo.whitePaint.measureText(whiteLabel)) / 2f;
        whiteY = scaleInfo.scaleY(21);
        blueX = (w - scaleInfo.bluePaint.measureText(blueLabel)) / 2f;
        blueY = h - scaleInfo.scaleY(3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean pressed = isPressed();
        int w = getWidth();
        int h = getHeight();
        int x;
        int y;
        int nextY;
        int oneX = scaleInfo.scaleX(1);
        int oneY = scaleInfo.scaleY(1);
        int offsetX = pressed ? oneX : 0;
        int offsetY = pressed ? oneY : 0;

        drawBlueLabel(canvas, offsetX, offsetY);
        drawWhiteLabel(canvas, offsetX, offsetY);
    }

    protected void drawBlueLabel(Canvas canvas, int offsetX, int offsetY) {
        canvas.drawText(blueLabel, blueX + offsetX, blueY + offsetY, scaleInfo.bluePaint);
    }

    protected void drawWhiteLabel(Canvas canvas, int offsetX, int offsetY) {
        canvas.drawText(whiteLabel, whiteX + offsetX, whiteY + offsetY, scaleInfo.whitePaint);
    }

}
