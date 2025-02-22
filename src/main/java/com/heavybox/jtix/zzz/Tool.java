package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;

public abstract class Tool {

    public boolean active = false;

    public abstract void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);

}
