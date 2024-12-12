package com.heavybox.jtix.z_ui_old;

import com.heavybox.jtix.z_graphics_old.Renderer2D_old;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

public abstract class UIElement {

    protected UIElementContainer parent;

    public float x, y, z, degX, degY, degZ, sclX, sclY;

    protected float[] bounds; // represent the flat [x0,y0, x1,y1, ...] polygon that bounds the UI element.
    protected float[] boundsTransformed;

    private boolean inBounds = false;

    protected UIElement(float x, float y, float z, float degX, float degY, float degZ, float sclX, float sclY) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.degX = degX;
        this.degZ = degZ;
        this.degY = degY;
        this.sclX = sclX;
        this.sclY = sclY;
    }

    protected void setBounds(float[] bounds) {
        this.bounds = bounds;
        this.boundsTransformed = new float[bounds.length];
    }

    // tests if the "Pointer" (mouse curse, controller marker, tap-finger etc.) is within the bounds of the UIElement.
    public boolean pointerInBounds(float x, float y) {
        float scaleX = sclX * MathUtils.cosDeg(degY);
        float scaleY = sclY * MathUtils.cosDeg(degX);

        Vector2 vertex = new Vector2();
        for (int i = 0; i < bounds.length; i += 2) {
            float poly_x = bounds[i];
            float poly_y = bounds[i + 1];

            vertex.set(poly_x, poly_y);
            vertex.scl(scaleX, scaleY);
            vertex.rotateDeg(degZ);
            vertex.add(x, y);
            boundsTransformed[i] = vertex.x;
            boundsTransformed[i + 1] = vertex.y;
        }

        return MathUtils.polygonContainsPoint(boundsTransformed, x, y);
    }

    public boolean descendantOf(UIElement ancestor) {
        if (!(ancestor instanceof UIElementContainer)) return false;
        UIElementContainer container = (UIElementContainer) ancestor;
        if (container.children.contains(this)) return true;
        boolean result = false;
        for (UIElement child : container.children) {
            result = result || descendantOf(child);
        }
        return result;
    }

    public abstract void onMouseEnter();
    public abstract void onMouseExit();
    public abstract void onMouseLeftDown();
    public abstract void onMouseLeftUp();
    public abstract void onMouseMiddleDown();
    public abstract void onMouseMiddleUp();
    public abstract void onMouseRightDown();
    public abstract void onMouseRightUp();
    public abstract void onMouseScroll();

    public abstract void draw(Renderer2D_old renderer);

}
