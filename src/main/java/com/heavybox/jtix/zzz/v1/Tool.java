package com.heavybox.jtix.zzz.v1;

import com.heavybox.jtix.graphics.Renderer2D;

public abstract class Tool {

    public final SceneRPGMapMaker scene;
    public boolean active = false;

    public Tool(final SceneRPGMapMaker scene) {
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

    public abstract void update();
    public abstract void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    public abstract void onSelect();
    public abstract void onDeselect();


}
