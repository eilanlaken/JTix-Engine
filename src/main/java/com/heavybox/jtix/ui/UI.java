package com.heavybox.jtix.ui;


import com.heavybox.jtix.graphics.Renderer2D_3;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

public abstract class UI {

    protected UIContainer parent;
    public float x, y, deg, sclX, sclY;
    public float global_x, global_y, global_deg, global_sclX, global_sclY;
    protected float[] bounds; // represent the flat [x0,y0, x1,y1, ...] polygon that bounds the UI element.
    protected float[] boundsTransformed;

    // attributes
    public boolean visible = true;
    public int     id;
    public Style   style;

    protected UI(float x, float y, float deg, float sclX, float sclY, float[] bounds) {
        this.x = x;
        this.y = y;
        this.deg = deg;
        this.sclX = sclX;
        this.sclY = sclY;

        this.global_x = x;
        this.global_y = y;
        this.global_deg = deg;
        this.global_sclX = sclX;
        this.global_sclY = sclY;

        this.bounds = bounds;
        this.boundsTransformed = new float[bounds.length];
    }

    // tests if the "Pointer" (mouse curse, controller marker, tap-finger etc.) is within the bounds of the UI element.
    public boolean pointerInBounds(float x, float y) {
        Vector2 vertex = new Vector2();
        for (int i = 0; i < bounds.length; i += 2) {
            float poly_x = bounds[i];
            float poly_y = bounds[i + 1];

            vertex.set(poly_x, poly_y);
            vertex.scl(sclX, sclY);
            vertex.rotateDeg(deg);
            vertex.add(x, y);
            boundsTransformed[i] = vertex.x;
            boundsTransformed[i + 1] = vertex.y;
        }

        return MathUtils.polygonContainsPoint(boundsTransformed, x, y);
    }

    public boolean belongsTo(UIContainer container) {
        if (container.children.contains(this)) return true;
        boolean result = false;
        for (UI child : container.children) {
            if (child instanceof UIContainer) {
                UIContainer childContainer = (UIContainer) child;
                result = result || belongsTo(childContainer);
            }
        }
        return result;
    }

    public abstract void render(Renderer2D_3 renderer2D);

}
