package com.heavybox.jtix.ui;

import com.heavybox.jtix.graphics.Renderer2D;

public abstract class UIElement {

    protected UIElementContainer parent;

    public float x, y, z, degX, degY, degZ, sclX, sclY;

    protected float[] bounds; // represent the flat [x0,y0, x1,y1, ...] polygon that bounds the UI element.
    protected float[] boundsTransformed;

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

    public abstract void draw(Renderer2D renderer);

}
