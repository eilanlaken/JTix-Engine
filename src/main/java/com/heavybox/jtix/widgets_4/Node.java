package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

public abstract class Node {

    protected NodeContainer container = null;
    final Polygon polygon = new Polygon();
    public boolean active = true;

    /* can be explicitly set by the programmer */
    public int   zIndex = 0;
    public float x      = 0;
    public float y      = 0;
    public float deg    = 0;
    public float sclX   = 1;
    public float sclY   = 1;

    /* calculated by container and Transform */
    // TODO: change to protected
    public int   screenZIndex = 0;
    public float screenX      = 0;
    public float screenY      = 0;
    public float screenDeg    = 0;
    public float screenSclX   = 1;
    public float screenSclY   = 1;

    // calculated by container
    public float offsetX = 0;
    public float offsetY = 0;
    public int maskingIndex = 1; // TODO.

    protected abstract void fixedUpdate(float delta);
    protected abstract void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    public abstract float calculateWidth(); // TODO: cache location and implement reset() logic
    public abstract float calculateHeight(); // TODO: cache location and implement reset() logic

    public final int getMaskingIndex() {
        if (container != null) return container.getMaskingIndex() + 1;
        return 1;
    }

    protected final void draw(Renderer2D renderer2D) {
        render(renderer2D, screenX, screenY, screenDeg, screenSclX, screenSclY);
    }

    public final void update(float delta) {
        transform();
        fixedUpdate(delta);
    }

    final void setInputRegion() {
        setPolygon(polygon);
        polygon.applyTransform(screenX, screenY, screenDeg, screenSclX, screenSclY);
    }

    final void transform() {
        int refZIndex = container == null ? this.zIndex : this.zIndex + container.screenZIndex;
        float refX = container == null ? 0 : container.screenX;
        float refY = container == null ? 0 : container.screenY;
        float refDeg = container == null ? 0 : container.screenDeg;
        float refSclX = container == null ? 1 : container.screenSclX;
        float refSclY = container == null ? 1 : container.screenSclY;
        float cos = MathUtils.cosDeg(refDeg);
        float sin = MathUtils.sinDeg(refDeg);
        float x = this.x * cos - this.y * sin;
        float y = this.x * sin + this.y * cos;
        screenZIndex = refZIndex + this.zIndex;
        screenX = refX + x * refSclX + offsetX * cos - offsetY * sin; // add the rotated offset vector x component
        screenY = refY + y * refSclY + offsetX * sin + offsetY * cos; // add the rotated offset vector y component
        screenDeg  = this.deg + refDeg;
        screenSclX = this.sclX * refSclX;
        screenSclY = this.sclY * refSclY;
    }


    // kind of a default implementation
    protected void setPolygon(final Polygon polygon) {
        polygon.setToRectangle(calculateWidth(), calculateHeight());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}