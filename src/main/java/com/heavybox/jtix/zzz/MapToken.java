package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TextureRegion;

public abstract class MapToken {

    protected final Type type;
    protected float x, y, deg, sclX, sclY;

    public MapToken(Type type) {
        this.type = type;
    }

    public abstract void render(Renderer2D renderer2D);

    public void translate(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public enum Type {
        TREE,
        HOUSE_RURAL,
        HOUSE_CITY,
        PROP,
        CASTLE_BLOCK,
        TEXT,
    }

}
