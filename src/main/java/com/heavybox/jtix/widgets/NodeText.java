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
        style.contentOverflowY = WidgetsStyle.Overflow.IGNORE;
        style.contentOverflowX = WidgetsStyle.Overflow.IGNORE;
        style.sizingWidth = WidgetsStyle.Sizing.DYNAMIC;
        style.sizingHeight = WidgetsStyle.Sizing.DYNAMIC;
        style.boxPaddingRight = 3;
        style.boxPaddingLeft = 10;
        style.boxPaddingTop = 0;
        style.boxPaddingBottom = 0;
    }

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(style.textColor);
        renderer2D.setFont(style.textFont);
        renderer2D.drawStringLine(text, style.textSize, true, x, y,true); // TODO: consider angle and scale
    }

    @Override
    protected int getContentWidth() {
        return (int) Renderer2D.getTextLineWidth(style.textFont, text, style.textSize,true);
    }

    @Override
    protected int getContentHeight() {
        return style.textSize;
    }

}
