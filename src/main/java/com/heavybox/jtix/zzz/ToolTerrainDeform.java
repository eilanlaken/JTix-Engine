package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

public class ToolTerrainDeform extends Tool {

    private static final CommandTerrainDeform.GroundType[] allTypes = CommandTerrainDeform.GroundType.values();

    public float scale = 0.20f;
    public int index = 0;

    public CommandTerrainDeform.GroundType currentType = CommandTerrainDeform.GroundType.LINE;

    // TODO: in the overlay, draw a preview with 0.5f opacity.

    public void selectRandom() {
        //index = MathUtils.randomUniformInt(0, MapTokenHouse.regionsBigVillageHouses.length);
        if (currentType == CommandTerrainDeform.GroundType.LINE) {
            index += 1;
            index %= CommandTerrainDeform.regionsGroundLine.length;
        }
    }

    public void nextType() {
        int nextIndex = (currentType.ordinal() + 1) % allTypes.length;
        currentType = allTypes[nextIndex];
        System.out.println(currentType.name());
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(0,1,0,1);
        renderer2D.drawCircleThin(20, 30, x, y, deg, sclX, sclY);
        renderer2D.setColor(Color.WHITE);
    }

}
