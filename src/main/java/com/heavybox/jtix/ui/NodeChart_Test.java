package com.heavybox.jtix.ui;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

public class NodeChart_Test extends Node {

    public int r;

    public NodeChart_Test(int r) {
        this.r = r;
    }

    @Override
    protected void setDefaultStyle() {
        style.renderBackground = true;
        style.overflow = Style.Overflow.IGNORE;
        style.paddingRight = 0;
        style.paddingLeft = 0;
        style.paddingTop = 0;
        style.paddingBottom = 0;
    }

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(Color.RED);
        renderer2D.drawCircleFilled(r, 20, x, y,deg, sclX, sclY); // TODO: consider angle and scale
    }

    @Override
    protected int getInnerWidth() {
        return r * 2;
    }

    @Override
    protected int getInnerHeight() {
        return r * 2;
    }

}
