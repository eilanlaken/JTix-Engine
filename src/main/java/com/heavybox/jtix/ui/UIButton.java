package com.heavybox.jtix.ui;

import com.heavybox.jtix.graphics.Renderer2D;

public class UIButton extends UI {

    /* attributes */
    public String text;
    public boolean enabled = true;

    public UIButton(float x, float y, float[] bounds, String text) {
        super(x, y, 0, 1, 1, bounds);
        this.text = text;
    }

    @Override
    public void render(Renderer2D renderer2D) {
        renderer2D.setColor(style.colorBackground);
        renderer2D.drawPolygonFilled(bounds); // bounds are already transformed.
        renderer2D.setColor(style.colorText);
        renderer2D.drawTextLine(text, style.fontSize, style.font, true, x, y, true);
    }

}
