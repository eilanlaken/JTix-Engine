package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;

public class NodeDebug extends Node {

    public float width = MathUtils.randomUniformFloat(50,200);
    public float height = MathUtils.randomUniformFloat(50,200);
    public Color color = Color.random();

    public NodeDebug(float x, float w) {
        this.x = x;
        this.width = w;
    }

    @Override
    protected void fixedUpdate(float delta) {

    }

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(color);
        System.out.println(x);
        renderer2D.drawRectangleFilled(width, height, x, y, deg, sclX, sclY);
    }

    @Override
    protected float getContentWidth() {
        return width;
    }

    @Override
    protected float getContentHeight() {
        return height;
    }

    @Override
    protected float getTotalWidth() {
        return width;
    }

    @Override
    protected float getTotalHeight() {
        return height;
    }

}
