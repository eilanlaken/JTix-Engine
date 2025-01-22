package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Renderer2D;

public class NodeText extends Node {

    public String text;

    public NodeText(String text) {
        this.text = text;
    }

    @Override
    protected void setDefaultStyle() {
        style.boxBackgroundEnabled = true;
        style.contentOverflowY = Style.Overflow.IGNORE;
        style.contentOverflowX = Style.Overflow.IGNORE;
        style.sizingWidth = Style.Sizing.AUTO;
        style.sizingHeight = Style.Sizing.AUTO;
        style.boxPaddingRight = 3;
        style.boxPaddingLeft = 10;
        style.boxPaddingTop = 0;
        style.boxPaddingBottom = 0;
    }

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(style.textColor);
        renderer2D.setFont(style.textFont);
        renderer2D.drawStringLine(text, style.textSize, style.textAntialiasing, x, y, deg, sclX, sclY);
    }

    @Override
    protected float getContentWidth() {
        return Renderer2D.calculateStringLineWidth(text, style.textFont, style.textSize,style.textAntialiasing);
    }

    @Override
    protected float getContentHeight() {
        return style.textSize;
    }

}
