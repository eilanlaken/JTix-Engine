package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

public class NodeDebug extends Node {

    String text = "this is some long text that will overflow. I made it even longer.";
    int width = 800;

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(Color.WHITE);
        renderer2D.drawRectangleFilled(width, 150, x,y,deg,sclX,sclY);
        renderer2D.setColor(Color.BLACK);
        renderer2D.drawStringLine(text, 22, true, 0,0,x,y,deg,sclX,sclY);
    }

    @Override
    protected float getContentWidth() {
        return Math.max(width,(int) Renderer2D.calculateStringLineWidth(text, style.textFont, style.textSize, style.textAntialiasing));
    }

    @Override
    protected float getContentHeight() {
        return Math.max(150, style.textSize);
    }

    @Override
    protected void setDefaultStyle() {
        style.boxBorderSize = 5;
    }

}
