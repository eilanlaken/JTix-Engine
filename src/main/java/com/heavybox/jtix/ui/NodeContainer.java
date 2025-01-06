package com.heavybox.jtix.ui;

import com.heavybox.jtix.graphics.Renderer2D;

public abstract class NodeContainer extends Node {

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

    public abstract void setChildrenTransforms();

    @Override
    protected int getInnerWidth() {
        return 0;
    }

    @Override
    protected int getInnerHeight() {
        return 0;
    }

    @Override
    protected void setDefaultStyle() {

    }

}
