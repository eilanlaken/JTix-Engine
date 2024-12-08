package com.heavybox.jtix.ui;

import com.heavybox.jtix.graphics.Renderer2D_3;

public abstract class UIElementButton extends UIElement {

    public String text;

    public UIElementButton(float x, float y, float deg, float sclX, float sclY, float[] bounds) {
        super(x, y, deg, sclX, sclY, bounds);
    }

}
