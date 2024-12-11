package com.heavybox.jtix.ui;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;

public final class UserInterface {


    /* API for creating buttons, paragraphs etc. */
    public static UIButton createButton(float width, float height, Color bgColor, Color textColor, String text, Font font) {
        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;
        float[] bounds = new float[] {
                -halfWidth, halfHeight,  // Top-left
                -halfWidth, -halfHeight,  // Bottom-left
                halfWidth, -halfHeight, // Bottom-right
                halfWidth, halfHeight,  // Top-right
        };
        UIButton btn = new UIButton(-200,0,0,1,1,bounds,text);
        btn.styleFont = font;
        btn.styleColorBackground = bgColor;
        btn.styleColorText = textColor;
        return btn;
    }



}
