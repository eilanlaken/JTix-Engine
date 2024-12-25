package com.heavybox.jtix.z_old_ui;

import com.heavybox.jtix.z_graphics_old.Renderer2D_4;

public class UIButton extends UI {

    /* attributes */
    public String text;
    public boolean enabled = true;

    public UIButton(float x, float y, float[] bounds, String text) {
        super(x, y, 0, 1, 1, bounds);
        this.text = text;
    }

    @Override
    public void render(Renderer2D_4 renderer2D) {
        renderer2D.setColor(style.backgroundColor);
        renderer2D.drawPolygonFilled(bounds); // bounds are already transformed.
        renderer2D.setColor(style.textColor);
        renderer2D.drawTextLine(text, style.fontSize, style.font, true, x, y, true);
    }

}
