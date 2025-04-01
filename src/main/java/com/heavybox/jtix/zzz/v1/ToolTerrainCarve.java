package com.heavybox.jtix.zzz.v1;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Input;

public class ToolTerrainCarve extends Tool {

    public Texture brushTexture;
    public float size = 25;
    public Mode mode = Mode.SUB;

    public ToolTerrainCarve(Scene scene) {
        super(scene);
        this.brushTexture = Assets.get("assets/app-brushes/terrain-brush_0.png");
    }

    @Override
    public void update() {
        size += 5 * Input.mouse.getVerticalScroll();
        size = Math.max(10, size);
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.drawCircleThin(size, 30, x, y, deg, sclX, sclY);
    }

    @Override
    public void onSelect() {

    }

    @Override
    public void onDeselect() {

    }

    public enum Mode {
        ADD,
        SUB,
        ;
    }

}
