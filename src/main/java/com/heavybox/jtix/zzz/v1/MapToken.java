package com.heavybox.jtix.zzz.v1;

import com.heavybox.jtix.graphics.Renderer2D;

public abstract class MapToken {

    public float x    = 0;
    public float y    = 0;
    public float deg  = 0;
    public float sclX = 1;
    public float sclY = 1;

    public abstract void render(Renderer2D renderer2D);

}
