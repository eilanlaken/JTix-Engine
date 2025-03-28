package com.heavybox.jtix.zzz.v1;

import com.heavybox.jtix.graphics.Renderer2D;

public abstract class MapToken {

    public float x;
    public float y;
    public float deg;
    public float sclX = 1;
    public float sclY = 1;

    public abstract void render(Renderer2D renderer2D);

}
