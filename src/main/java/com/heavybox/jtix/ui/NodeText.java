package com.heavybox.jtix.ui;

import com.heavybox.jtix.graphics.Renderer2D;

public class NodeText extends Node {

    public String text;

    public NodeText(String text) {
        this.text = text;
    }

    @Override
    protected Style getDefaultStyle() {
        Style defaultStyle = new Style();
        defaultStyle.renderBackground = false;
        defaultStyle.overflow = Style.Overflow.IGNORE;
        defaultStyle.paddingRight = 0;
        defaultStyle.paddingLeft = 0;
        defaultStyle.paddingTop = 0;
        defaultStyle.paddingBottom = 0;
        return defaultStyle;
    }

    @Override
    protected void render(Renderer2D renderer2D, float screenX, float screenY, float screenDeg, float screenSclX, float screenSclY) {
        renderer2D.setColor(style.textColor);
        renderer2D.setFont(style.font);
        renderer2D.drawStringLine(text, style.fontSize, true, screenX, screenY,true); // TODO: consider angle and scale
    }

    @Override
    protected int getInnerWidth() {
        return (int) Renderer2D.getTextLineWidth(style.font, text, style.fontSize,true);
    }

    @Override
    protected int getInnerHeight() {
        return style.fontSize;
    }

}
