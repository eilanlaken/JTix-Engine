package com.heavybox.jtix.ui_3;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

public class NodeDebug extends Node {

    String text = "this is some long text that will overflow. I made it even longer.";

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
//        renderer2D.setColor(Color.YELLOW);
//        renderer2D.drawRectangleFilled(300, 150, x,y,deg,sclX,sclY);
        renderer2D.setColor(Color.RED);
        renderer2D.drawStringLine(text, 18, true, 0,0,x,y,deg,sclX,sclY);
    }

    @Override
    protected int getContentWidth() {
        return (int) Renderer2D.calculateStringLineWidth(text, style.textFont, style.textSize, style.textAntialiasing);
    }

    @Override
    protected int getContentHeight() {
        return style.textSize;
    }

    @Override
    protected void setDefaultStyle() {

    }

}
