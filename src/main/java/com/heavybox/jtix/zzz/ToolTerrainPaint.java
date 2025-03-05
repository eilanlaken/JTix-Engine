package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;

// TODO: optimize.
// The terrain paint will maintain a texture (terrain texture) that is updated with the current frame buffer every step-bundle =  ~200 draw operations.
// Then, to construct the current terrain, it will be: render_terrain_texture, draw last operations since terrain texture was updated.
// maybe even step-bundle = 1.
// use gl sub pixels.
public class ToolTerrainPaint extends Tool {

    public int mask = CommandTerrainPaint.GRASS_MASK;
    public float r = 6;

    public ToolTerrainPaint() {
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }
}
