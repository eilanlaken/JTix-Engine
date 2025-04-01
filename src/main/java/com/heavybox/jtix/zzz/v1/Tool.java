package com.heavybox.jtix.zzz.v1;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.graphics.Renderer2D;

public abstract class Tool {

    public final Scene scene;
    public boolean active = false;

    public Tool(final Scene scene) {
        this.scene = scene;
    }

    public void select() {
        this.active = true;
        onSelect();
    }

    public void deselect() {
        onDeselect();
        this.active = false;
    }

    public void frameUpdate() {
        if (!active) return;
        update();
    }

    public abstract void update();
    public abstract void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    public abstract void onSelect();
    public abstract void onDeselect();


}
