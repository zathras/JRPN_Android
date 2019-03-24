
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
import android.graphics.Canvas;
import android.util.AttributeSet;

public class GButtonEnter extends GButton {

    private float whiteHeight;
    private String[] letters = null;
    float[] whiteX;

    public GButtonEnter(Context context) {
        super(context);
    }

    public GButtonEnter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GButtonEnter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void alignText(int w, int h) {
        super.alignText(w, h);
        if (letters == null) {
            letters = new String[whiteLabel.length()];
            for (int i = 0; i < whiteLabel.length(); i++) {
                letters[i] = "" + whiteLabel.charAt(i);
            }
            whiteX = new float[letters.length];
        }
        for (int i = 0; i < letters.length; i++) {
            whiteX[i] = (w - scaleInfo.whitePaint.measureText(letters[i])) / 2f;
        }
        whiteHeight = -scaleInfo.whitePaint.ascent();
    }

    @Override
    protected void drawWhiteLabel(Canvas canvas, int offsetX, int offsetY) {
        float y = 0;
        for (int i = 0; i < letters.length; i++)  {
            canvas.drawText(letters[i], whiteX[i] + offsetX, whiteY + offsetY + y, scaleInfo.whitePaint);
            y += whiteHeight;
        }
    }
}
