package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;

public class ToolTerrainPaint extends Tool {

    public int mask = CommandTerrainPaint.GRASS_MASK;
    public float r = 6;

    public ToolTerrainPaint() {
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }
}
