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
import android.graphics.Color;
import android.util.AttributeSet;

// For the red and blue keys.  They display the foreground
// ("white") text as black.
public class GButtonShift extends GButton {

    public GButtonShift(Context context) {
        super(context);
    }

    public GButtonShift(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GButtonShift(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void alignText(int w, int h) {
        super.alignText(w, h);
    }

    @Override
    protected void drawWhiteLabel(Canvas canvas, int offsetX, int offsetY) {
        int oldColor = scaleInfo.whitePaint.getColor();
        scaleInfo.whitePaint.setColor(Color.BLACK);
        canvas.drawText(whiteLabel, whiteX + offsetX, whiteY + offsetY, scaleInfo.whitePaint);
        scaleInfo.whitePaint.setColor(oldColor);
    }
}
