package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;

public abstract class MapToken {

    protected final Type type;
    protected float x, y, deg, sclX = 1, sclY = 1;

    public MapToken(Type type) {
        this.type = type;
    }

    public abstract void render(Renderer2D renderer2D);

    public void setTransform(Command command) {
        this.x = command.x;
        this.y = command.y;
        this.deg = command.deg;
        this.sclX = command.sclX;
        this.sclY = command.sclY;
    }

    public void translate(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public enum Type {
        TREE,
        HOUSE,
        PROP, // windmill, well, tower, fence, flowers etc.
        CASTLE_BLOCK,
        TEXT,
    }

}
