package com.heavybox.jtix.ui;

import com.heavybox.jtix.graphics.Renderer2D_3;

public class UIButton extends UI {

    /* attributes */
    public String text;
    public boolean enabled = true;

    public UIButton(float x, float y, float deg, float sclX, float sclY, float[] bounds, String text) {
        super(x, y, deg, sclX, sclY, bounds);
        this.text = text;
    }

    @Override
    public void render(Renderer2D_3 renderer2D) {
        renderer2D.setColor(styleColorBackground);
        renderer2D.drawPolygonFilled(boundsTransformed); // bounds are already transformed.
        renderer2D.setColor(styleColorText);
        renderer2D.drawString(text, styleFont, global_x, global_y);
    }

}
