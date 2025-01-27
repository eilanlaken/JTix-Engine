package com.heavybox.jtix.widgets_3;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Renderer2D;

public class NodeText extends Node {

    public String  text         = null;
    public Color   color        = Theme.textColor;
    public Font    font         = Theme.textFont;
    public boolean antialiasing = Theme.textAntialiasing;
    public int     size         = Theme.textSize;

    // Todo: max width, wrapping, etc. Currently represent a single line of text.

    public NodeText(String text) {
        this.text = text;
    }

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(color);
        renderer2D.setFont(font);
        renderer2D.drawStringLine(text, size, antialiasing, x, y, deg, sclX, sclY);
    }

    @Override
    protected float getContentWidth() {
        return Renderer2D.calculateStringLineWidth(text, font, size, antialiasing);
    }

    @Override
    protected float getContentHeight() {
        return size;
    }

}
