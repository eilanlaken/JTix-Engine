package com.heavybox.jtix.ui;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;

public final class UserInterface {


    /* API for creating buttons, paragraphs etc. */
    public static UIButton createButton(float width, float height, String text) {
        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;
        float[] bounds = new float[] {
                -halfWidth, halfHeight,  // Top-left
                -halfWidth, -halfHeight,  // Bottom-left
                halfWidth, -halfHeight, // Bottom-right
                halfWidth, halfHeight,  // Top-right
        };
        UIButton btn = new UIButton(-200,0,bounds,text);
        return btn;
    }

    private static float[] calculateBounds() {

        return null;
    }

}
