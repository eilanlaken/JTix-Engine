package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;

// TODO: optimize.
// The terrain paint will maintain a texture (terrain texture) that is updated with the current frame buffer every step-bundle =  ~200 draw operations.
// Then, to construct the current terrain, it will be: render_terrain_texture, draw last operations since terrain texture was updated.
// maybe even step-bundle = 1.
// use gl sub pixels.
public class ToolTerrainBrush extends Tool {

    public Mode currentMode;

    // brush
    public int brush_mask = CommandTerrainPaint.WATER_MASK;
    public float brush_r = 22;

    // props
    public float prop_scale = 1;
    public float prop_angle = 0;
    public int prop_index = 0;

    public ToolTerrainBrush() {

    }

    public void nextMode() {
        int nextIndex = (currentMode.ordinal() + 1) % Mode.values().length;
        currentMode = Mode.values()[nextIndex];
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

    public enum Mode {

        BRUSH_WATER,
        BRUSH_ROAD,
        BRUSH_ERASE,

        DEFORM_SINGLES_LINES,
        DEFORM_SINGLES_BUMPS,
        DEFORM_SINGLES_OBJECTS,

        DEFORM_PRESETS_LINES,
        DEFORM_PRESETS_BUMPS,

    }

}
