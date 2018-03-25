
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

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

// I fudge the sqrt symbol in the blue text on the OCT key so the bar over the x
// joins the sqrt symbol.
public class GButtonSqrt extends GButton {

    public GButtonSqrt(Context context) {
        super(context);
    }

    public GButtonSqrt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GButtonSqrt(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    float sqrtWidth;
    float blueHeight;

    @Override
    public void alignText(int w, int h) {
        super.alignText(w, h);
        sqrtWidth = scaleInfo.bluePaint.measureText("\u221A");  // √
        blueHeight = -scaleInfo.bluePaint.ascent();     // Android ascent() is negative
    }

    @Override
    protected void drawBlueLabel(Canvas canvas, int offsetX, int offsetY) {
        // Draw a combining overline, shifted over to the right by the better part
        // of the width of the square root sign.  We also scoot the x up a bit, so the
        // line matches the horizontal part of the square root symbol.
        // This isn't perfect, and it might even make it worse with some fonts,
        // but on balance I think there's a better chance it will look good this
        // way.
        float x = blueX + offsetX;
        float y = blueY + offsetY;
        canvas.drawText("\u221A", x, y, scaleInfo.bluePaint);   // √
        float shiftUp = blueHeight / 10f;
        float shiftLeft = sqrtWidth / 6f;
        y -= shiftUp;
        x += sqrtWidth - shiftLeft;
        canvas.drawText("x\u0305", x, y, scaleInfo.bluePaint);  // x with a bar over it
    }
}
