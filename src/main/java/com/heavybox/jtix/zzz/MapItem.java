package com.heavybox.jtix.zzz;

// represents a map object:
// a tree + trunk + fruits, text, house, etc.
// TODO: add a bounding rectangle for selection etc.
public abstract class MapItem {

    protected float x, y, deg, sclX, sclY;

    public abstract void render();

    public void translate(float x, float y) {
        this.x += x;
        this.y += y;
    }

}
