package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;

public class NodeInputCheckbox extends Node implements NodeInput<Boolean> {

    // state
    public boolean checked = true;

    public Color borderColorUnchecked = Color.valueOf("767676");
    public Color borderColorChecked = Color.valueOf("0075FF");
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
            renderCheckmark(renderer2D, size, radius, x, y, deg, sclX, sclY);
        }
    }

    protected void renderBox(Renderer2D renderer2D, int size, float borderSize, float radius, float x, float y, float deg, float sclX, float sclY) {

        if (checked) renderer2D.setColor(borderColorChecked);
        else renderer2D.setColor(borderColorUnchecked);
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
        renderer2D.drawLineFilled(-9,1, -1,-7,3,x,y,deg,sclX,sclY);
        renderer2D.drawLineFilled(-1.7106f, -4.1754f, 8.2894f,5.9246f,4,x,y,deg,sclX,sclY);
    }

    @Override
    protected float getContentWidth() {
        return size + borderSize * 2;
    }

    @Override
    protected float getContentHeight() {
        return size + borderSize * 2;
    }

    @Override
    protected void setDefaultStyle() {
        style.boxBackgroundEnabled = false;
    }

    @Override
    public Boolean getValue() {
        return checked;
    }

    @Override
    public void setValue(Boolean value) {
        this.checked = value != null && value;
    }

}
