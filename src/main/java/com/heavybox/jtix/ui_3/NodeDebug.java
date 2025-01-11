package com.heavybox.jtix.ui_3;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

public class NodeDebug extends Node {

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        //renderer2D.setColor(Color.YELLOW);
        //renderer2D.drawRectangleFilled(300, 150, x,y,deg,sclX,sclY);
    }

    @Override
    protected int getContentWidth() {
        return 300;
    }

    @Override
    protected int getContentHeight() {
        return 150;
    }

    @Override
    protected void setDefaultStyle() {

    }

}
