package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;

public class ToolCastleGenerator extends Tool {

    private static final MapTokenCastleBlock.BlockType[] allTypes = MapTokenCastleBlock.BlockType.values();

    public float scale = 0.20f;
    public MapTokenCastleBlock.BlockType currentType = allTypes[0];
    public int baseIndex = 0;

    public void selectNext() {
        int nextIndex = (currentType.ordinal() + 1) % allTypes.length;
        currentType = allTypes[nextIndex];
        System.out.println(currentType.name());
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

}
