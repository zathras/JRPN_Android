
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

import android.graphics.Paint;

/**
 * A little data holder for the stuff that changes when we're resized.
 */
public class ScaleInfo {
    int drawScaleNumerator = 1;
    int drawScaleDenominator = 1;
    int drawScaleNumeratorX = 1;
    int drawScaleDenominatorX = 1;
    int drawScaleNumeratorY= 1;
    int drawScaleDenominatorY = 1;

    final Paint bluePaint = new Paint();  // The blue text on keys
    final Paint whitePaint = new Paint(); // The white text on keys
    final Paint yellowPaint = new Paint();  // The yellow text above the keys
    final Paint faceTextPaint = new Paint();  // The "EMMET-GRAY/JOVIAL" text on the face
    final Paint faceBgPaint = new Paint();    // Matching the background color
    final Paint logoPaint = new Paint();      // The black part and the font for the logo

    public int scale(int num) {
        return num * drawScaleNumerator / drawScaleDenominator;
    }

    public int scaleX(int num) {
        return num * drawScaleNumeratorX / drawScaleDenominatorX;
    }

    public int scaleY(int num) {
        return num * drawScaleNumeratorY / drawScaleDenominatorY;
    }

    public float scale(float num) {
        return num * drawScaleNumerator / drawScaleDenominator;
    }

    public float scaleX(float num) {
        return num * drawScaleNumeratorX / drawScaleDenominatorX;
    }

    public float scaleY(float num) {
        return num * drawScaleNumeratorY / drawScaleDenominatorY;
    }
}
