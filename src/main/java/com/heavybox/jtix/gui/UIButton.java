package com.heavybox.jtix.gui;

import com.heavybox.jtix.graphics.Renderer2D;

public class UIButton extends UI {

    /* attributes */
    public String text;
    public boolean enabled = true;

    public UIButton(String text) {
        this.text = text;
    }

    @Override
    public void render(Renderer2D renderer2D) {
        renderer2D.setColor(style.backgroundColor);
        renderer2D.drawPolygonFilled(bounds); // bounds are already transformed.
        renderer2D.setColor(style.textColor);
        renderer2D.drawTextLine(text, style.fontSize, style.font, true, style.x, style.y, true);
    }

}
