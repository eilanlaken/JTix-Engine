package com.heavybox.jtix.zzz.v1;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;

public class ToolTerrainMask extends Tool {

    public Texture brushTexture;
    public float size = 10;

    public ToolTerrainMask(SceneRPGMapMaker scene) {
        super(scene);
        this.brushTexture = Assets.get("assets/app-brushes/terrain-brush_0.png");
    }

    @Override
    public void update() {

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

}
