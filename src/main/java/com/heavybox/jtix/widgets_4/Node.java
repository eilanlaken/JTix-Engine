package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;

public abstract class Node {

    protected NodeContainer container;

    final Polygon polygon = new Polygon();

    /* can be explicitly set by user */
    public int   zIndex = 0;
    public float x      = 0;
    public float y      = 0;
    public float deg    = 0;
    public float sclX   = 1;
    public float sclY   = 1;

    /* set by parent container */
    public float parentWidth;
    public float parentHeight;
    public int parentZIndex = 0;
    public float parentX = 0;
    public float parentY = 0;
    public float parentDeg = 0;
    public float parentSclX = 1;
    public float parentSclY = 1;

    /* calculated by container and Transform */
    public int   screenZIndex = 0;
    public float screenX    = 0;
    public float screenY    = 0;
    public float screenDeg  = 0;
    public float screenSclX = 1;
    public float screenSclY = 1;

    protected abstract void fixedUpdate(float delta);
    protected final void draw(Renderer2D renderer2D) { render(renderer2D, screenX, screenY, screenDeg, screenSclX, screenSclY); }
    protected abstract void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    protected abstract float getWidth();
    protected abstract float getHeight();
    final void updatePolygon() { updatePolygon(polygon); }

    final void transform() {
        float cos = MathUtils.cosDeg(parentDeg);
        float sin = MathUtils.sinDeg(parentDeg);
        float x = this.x * cos - this.y * sin;
        float y = this.x * sin + this.y * cos;
        screenZIndex = parentZIndex + this.zIndex;
        screenX = parentX + x * parentSclX;
        screenY = parentY + y * parentSclY;
        screenDeg  = this.deg + parentDeg;
        screenSclX = this.sclX * parentSclX;
        screenSclY = this.sclY * parentSclY;
        polygon.applyTransform(screenX, screenY, screenDeg, screenSclX, screenSclY);
    }

    // kind of a default implementation
    protected void updatePolygon(final Polygon polygon) {
        polygon.setToRectangle(getWidth(), getHeight());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
