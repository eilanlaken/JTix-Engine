package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

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
    public int   refZIndex = 0;
    public float refX      = 0;
    public float refY      = 0;
    public float refDeg    = 0;
    public float refSclX   = 1;
    public float refSclY   = 1;

    /* calculated by container and Transform */
    public int   screenZIndex = 0;
    public float screenX      = 0;
    public float screenY      = 0;
    public float screenDeg    = 0;
    public float screenSclX   = 1;
    public float screenSclY   = 1;

    public float offsetX = 0;
    public float offsetY = 0;

    protected abstract void fixedUpdate(float delta);
    protected final void draw(Renderer2D renderer2D) { render(renderer2D, screenX, screenY, screenDeg, screenSclX, screenSclY); }
    protected abstract void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    public abstract float getWidth();
    public abstract float getHeight();

    public final void update(float delta) {
        transform();
        fixedUpdate(delta);
    }

    final void setInputRegion() {
        setPolygon(polygon);
        polygon.applyTransform(screenX, screenY, screenDeg, screenSclX, screenSclY);
    }

//    final void transform() {
//        float cos = MathUtils.cosDeg(refDeg);
//        float sin = MathUtils.sinDeg(refDeg);
//        float x = this.x * cos - this.y * sin;
//        float y = this.x * sin + this.y * cos;
//        screenZIndex = refZIndex + this.zIndex;
//        screenX = refX + x * refSclX;
//        screenY = refY + y * refSclY;
//        float offsetX = refOffsetX * cos - refOffsetY * sin;
//        float offsetY = refOffsetX * sin + refOffsetY * cos;
//        screenX += offsetX;
//        screenY += offsetY;
//        screenDeg  = this.deg + refDeg;
//        screenSclX = this.sclX * refSclX;
//        screenSclY = this.sclY * refSclY;
//    }


    final void transform() {
        float cos = MathUtils.cosDeg(refDeg);
        float sin = MathUtils.sinDeg(refDeg);
        float x = this.x * cos - this.y * sin;
        float y = this.x * sin + this.y * cos;
        screenZIndex = refZIndex + this.zIndex;
        screenX = refX + x * refSclX;
        screenY = refY + y * refSclY;
        Vector2 ref = new Vector2(offsetX, offsetY);
        ref.rotateAroundDeg(0,0, refDeg);
        screenX += ref.x;
        screenY += ref.y;
        screenDeg  = this.deg + refDeg;
        screenSclX = this.sclX * refSclX;
        screenSclY = this.sclY * refSclY;
    }


    // kind of a default implementation
    protected void setPolygon(final Polygon polygon) {
        polygon.setToRectangle(getWidth(), getHeight());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
