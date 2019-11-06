
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


package com.jovial.jrpn;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;

public class CalcFace extends MyAbsoluteLayout {

    /**
     * This represents the bit of yellow text above the buttons
     */
    public static class YellowText {

        protected GButton over;
        protected String text;
        protected float stringWidth;

        public YellowText(GButton over, String text) {
            this.over = over;
            this.text = text;
        }

        protected float getRightX() {
            return over.getX() + over.getWidth() - 1;
        }

        public void alignText(ScaleInfo info) {
            this.stringWidth = info.yellowPaint.measureText(text);
        }

        public void draw(Canvas canvas, ScaleInfo info) {
            float textX = (over.getX() + getRightX() - stringWidth) / 2;
            float textY = over.getY() - info.scaleY(2);
            canvas.drawText(text, textX, textY, info.yellowPaint);
        }
    }

    public static class YellowMultiText extends YellowText {

        protected GButton right;
        protected int linesUp;
        protected float pixelsUp;
        private Path lines = new Path();

        public YellowMultiText(GButton left, GButton right, int linesUp, String text) {
            super(left, text);
            this.right = right;
            this.linesUp =linesUp;
        }

        protected float getRightX() {
            return right.getX() + right.getWidth() - 1;
        }

        @Override
        public void alignText(ScaleInfo info) {
            super.alignText(info);
            pixelsUp = (-info.yellowPaint.ascent()) * linesUp;
        }

        @Override
        public void draw(Canvas canvas, ScaleInfo info) {
            info.yellowPaint.setStyle(Paint.Style.STROKE);
            float textX = (over.getX() + getRightX() - stringWidth) / 2;
            float textY = over.getY() - info.scaleY(2) - pixelsUp;
            float ascent = -info.yellowPaint.ascent();  // ascent is positive, ascent() is negative
            float lineY = textY - ascent / 2f;
            float x1 = over.getX()- info.scaleX(2);
            float x2 = textX - info.scaleX(4);
            lines.reset();
            lines.moveTo(x1, over.getY() - pixelsUp - info.scaleY(1));
            lines.lineTo(x1, lineY);
            lines.lineTo(x2, lineY);
            canvas.drawPath(lines, info.yellowPaint);

            x1 = textX + stringWidth + info.scaleX(4);
            x2 = getRightX() + info.scaleX(2);
            lines.reset();
            lines.moveTo(x1, lineY);
            lines.lineTo(x2, lineY);
            lines.lineTo(x2, right.getY() - pixelsUp - info.scaleY(1));
            canvas.drawPath(lines, info.yellowPaint);

            info.yellowPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(text, textX, textY, info.yellowPaint);
        }
    }


    public YellowText[] yellowText;
    private ScaleInfo scaleInfo;
    private static String faceText = "E M M E T - G R A Y / J O V I A L";
    private float faceTextWidth;
    private fmMain myMain;

    public CalcFace(Context context) {
        super(context);
    }

    public CalcFace(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalcFace(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void setMain(fmMain myMain) {
        this.myMain = myMain;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        myMain.doResize(w, h);
    }

    void setScaleInfo(ScaleInfo scaleInfo) {
        this.scaleInfo = scaleInfo;
    }

    void resize() {
        Paint p = scaleInfo.yellowPaint;
        // Color taken to match the F key.  This is a less intense
        // yellow than Emmet's original, but I think it's more readable.
        // Originally it was Color.YELLOW
        p.setColor(Color.argb(255, 255, 231, 66));
        p.setStyle(Paint.Style.FILL);
        p.setStrokeWidth(1.1f * (float) scaleInfo.drawScaleNumerator / (float) scaleInfo.drawScaleDenominator);
        p.setStrokeJoin(Paint.Join.ROUND);
        p.setTextSize(scaleInfo.scale(10f));
        p.setTypeface(fmMain.EMBEDDED_FONT);

        p = scaleInfo.faceTextPaint;
        p.setColor(Color.argb(255, 231, 231, 231));
        p.setStyle(Paint.Style.FILL);
        p.setTextSize(scaleInfo.scale(12f));
        p.setTypeface(fmMain.EMBEDDED_FONT);

        p=scaleInfo.faceBgPaint;
        p.setColor(Color.argb(255, 66, 66, 66));
        p.setStyle(Paint.Style.FILL);

        p=scaleInfo.logoPaint;
        p.setColor(Color.argb(255, 0, 0, 0));
        if (scaleInfo.isLandscape) {
            p.setTextSize(scaleInfo.scale(12f));
        } else {
            p.setTextSize(scaleInfo.scale(10f));
        }
        p.setTypeface(fmMain.EMBEDDED_FONT);

        for (YellowText yt : yellowText) {
            yt.alignText(scaleInfo);
        }

        faceTextWidth = scaleInfo.faceTextPaint.measureText(faceText);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();

        if (h < w) {
            int cw = fmMain.CALC_WIDTH;
            int ch = fmMain.CALC_HEIGHT;
            canvas.drawRect(35*w/cw, 290*h/ch, (35+10)*w/cw + faceTextWidth, (290+25)*h / ch, scaleInfo.faceBgPaint);
            canvas.drawText(faceText, 40 * w / cw, 310 * h / ch, scaleInfo.faceTextPaint);
        } else {
            int cw = fmMain.CALC_HEIGHT;
            int ch = fmMain.CALC_WIDTH;
            canvas.drawRect(33*w/cw, 490*h/ch, (33+28)*w/cw + faceTextWidth, (490+16)*h/ch, scaleInfo.faceBgPaint);
            canvas.drawText(faceText, 47*w/cw, 502*h/ch, scaleInfo.faceTextPaint);
        }

        for (YellowText yt : yellowText) {
            yt.draw(canvas, scaleInfo);
        }
    }
}


