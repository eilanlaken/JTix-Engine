package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;

public class ToolStampHouses extends Tool {

    private static final MapTokenHouse.HouseType[] allTypes = MapTokenHouse.HouseType.values();

    public float randomScaleRange = 0;
    public float scale = 1;
    public int index = 0;

    public MapTokenHouse.HouseType currentType = MapTokenHouse.HouseType.SMALL_VILLAGE_HOUSE;

    // TODO: in the overlay, draw a preview with 0.5f opacity.

    public void selectRandom() {
        //index = MathUtils.randomUniformInt(0, MapTokenHouse.regionsBigVillageHouses.length);
        if (currentType == MapTokenHouse.HouseType.SMALL_VILLAGE_HOUSE) {
            index += 1;
            index %= MapTokenHouse.regionsSmallVillageHouses.length;
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
