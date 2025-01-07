package com.heavybox.jtix.ui_2;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

public class NodeText extends Node {

    public String text;

    public NodeText(String text) {
        this.text = text;
    }

    @Override
    protected void setDefaultStyle() {
        style.renderBackground = true;
        style.overflow = Style.Overflow.IGNORE;
        style.sizeWidth = Style.Size.GAS;
        style.sizeHeight = Style.Size.LIQUID;
        style.paddingRight = 3;
        style.paddingLeft = 10;
        style.paddingTop = 0;
        style.paddingBottom = 0;
    }

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(style.textColor);
        renderer2D.setFont(style.font);
        renderer2D.drawStringLine(text, style.fontSize, true, x, y,true); // TODO: consider angle and scale
    }

    @Override
    protected int getContentWidth() {
        return (int) Renderer2D.getTextLineWidth(style.font, text, style.fontSize,true);
    }

    @Override
    protected int getContentHeight() {
        return style.fontSize;
    }

}
