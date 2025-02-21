package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TextureRegion;

public abstract class MapToken {

    protected final TextureRegion[] regions;
    protected float x, y, deg, sclX, sclY;

    public MapToken(TextureRegion[] regions) {
        this.regions = regions;
    }

    public void render(Renderer2D renderer2D) {
        for (TextureRegion region : regions) {
            renderer2D.drawTextureRegion(region, x, y, deg, sclX, sclY);
        }
    }

    public void translate(float x, float y) {
        this.x += x;
        this.y += y;
    }

}
