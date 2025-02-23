package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;

public class ToolTerrain extends Tool {

    public int mask = CommandTerrainPaint.GRASS_MASK;
    public float r = 1;

    public ToolTerrain() {
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }
}
