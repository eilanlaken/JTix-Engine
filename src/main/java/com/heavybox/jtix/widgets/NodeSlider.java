package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

public class NodeSlider extends Node {

    public float value     = 0.5f;
    public int   width     = 150;
    public int   height    = 6;
    public int   thumbWidth = 8;
    public int   thumbHeight = 8;


    public Color barColorFill = Color.valueOf("EFEFEF");
    public Color barColorBorder = Color.valueOf("C8C8C8");
    public Color trackColor = Color.valueOf("0075FF");
    public Color thumbColorFill = Color.valueOf("0075FF");
    public Color thumbColorBorder = Color.valueOf("0075FF");

    public float borderThickness = 1;
    public float borderRadius = height * 1.5f * 0;

    public NodeSlider() {
    }

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        float cornerRadius = MathUtils.clampFloat(borderRadius, 0,height * 0.5f);

        renderBar(renderer2D, width, height, cornerRadius, x, y, deg, sclX, sclY);


        Vector2 trackPos = new Vector2(width * 0.5f * (value - 1), 0);
        trackPos.scl(sclX, sclY).rotateDeg(deg).add(x,y);
        float trackWidth = width * value;
        float trackHeight = height + borderThickness * 2;
        renderTrack(renderer2D, trackWidth, trackHeight, cornerRadius, trackPos.x, trackPos.y, deg, sclX, sclY);


        Vector2 thumbPos = new Vector2(width * (value - 0.5f), 0);
        thumbPos.scl(sclX, sclY).rotateDeg(deg).add(x,y);
        renderThumb(renderer2D, thumbWidth, thumbHeight, thumbPos.x, thumbPos.y, deg, sclX, sclY);
    }

    protected void renderBar(Renderer2D renderer2D, int width, int height, float cornerRadius, float x, float y, float deg, float sclX, float sclY) {

        renderer2D.setColor(barColorFill);
        renderer2D.drawRectangleFilled(width, height,
                cornerRadius, 6,
                cornerRadius, 6,
                cornerRadius, 6,
                cornerRadius, 6,
                x,y,deg,sclX,sclY);
        renderer2D.setColor(barColorBorder);
        renderer2D.drawRectangleBorder(width, height, borderThickness,
                cornerRadius, 6,
                cornerRadius, 6,
                cornerRadius, 6,
                cornerRadius, 6,
        x,y,deg,sclX,sclY);
    }

    protected void renderTrack(Renderer2D renderer2D, float trackWidth, float trackHeight, float cornerRadius, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(trackColor);
        renderer2D.drawRectangleFilled(trackWidth, trackHeight,
                cornerRadius, 6,
                cornerRadius, 6,
                cornerRadius, 6,
                cornerRadius, 6,
                x,y,deg,sclX,sclY);
    }

    protected void renderThumb(Renderer2D renderer2D, float thumbWidth, float thumbHeight, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(thumbColorFill);
        renderer2D.drawCircleFilled(thumbWidth, 15, x,y,deg,sclX,sclY);
        renderer2D.setColor(thumbColorBorder);
        renderer2D.drawCircleBorder(thumbWidth, thumbWidth * 0.1f, 15, x,y,deg,sclX,sclY);
    }

    @Override
    protected int getContentWidth() {
        return width + thumbWidth;
    }

    @Override
    protected int getContentHeight() {
        return Math.max(height, thumbHeight * 2);
    }

    @Override
    protected void setDefaultStyle() {
        style.boxBackgroundEnabled = false;
    }
}
