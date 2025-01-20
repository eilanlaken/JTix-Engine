package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

public class NodeCheckbox extends Node {

    // state
    public boolean checked = true;

    public Color borderColor = Color.valueOf("767676");
    public Color checkmarkBackgroundColor = Color.valueOf("0075FF");
    public Color checkmarkColor = Color.WHITE.clone();

    public int size = 26;
    public int borderSize = 2;
    public int cornerRadius = 5;


    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        float radius = MathUtils.clampFloat(cornerRadius, 0, size * 0.5f);
        renderBox(renderer2D, size, borderSize, radius, x, y, deg, sclX, sclY);

        if (checked) {
            float checkedSize = size + borderSize * 2;
            renderCheckmark(renderer2D, checkedSize, radius, x, y, deg, sclX, sclY);
        }
    }

    protected void renderBox(Renderer2D renderer2D, int size, float borderSize, float radius, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(borderColor);
        renderer2D.drawRectangleBorder(size, size, borderSize,
                radius, 6,
                radius, 6,
                radius, 6,
                radius, 6,
                x,y,deg,sclX,sclY);
    }

    protected void renderCheckmark(Renderer2D renderer2D, float size, float radius, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(checkmarkBackgroundColor);
        renderer2D.drawRectangleFilled(size, size,
                radius, 6,
                radius, 6,
                radius, 6,
                radius, 6,
                x,y,deg,sclX,sclY);

        renderer2D.setColor(checkmarkColor);
        renderer2D.drawLineFilled(-8,0, 0,-8,3,x,y,deg,sclX,sclY);
        renderer2D.drawLineFilled(-0.7106f, -5.1754f, 9.2894f,4.9246f,4,x,y,deg,sclX,sclY);
        //renderer2D.drawLineFilled(0, -5.879f, 10,4.221f,3,x,y,deg,sclX,sclY);
    }

    @Override
    protected int getContentWidth() {
        return size + borderSize * 2;
    }

    @Override
    protected int getContentHeight() {
        return size + borderSize * 2;
    }

    @Override
    protected void setDefaultStyle() {
        style.boxBackgroundEnabled = false;
    }
}
