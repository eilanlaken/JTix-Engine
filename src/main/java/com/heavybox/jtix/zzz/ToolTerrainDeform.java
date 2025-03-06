package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;

public class ToolTerrainDeform extends Tool {

    private static final CommandTerrainDeform.GroundType[] allTypes = CommandTerrainDeform.GroundType.values();

    public float scale = 1;
    public float angle = 0;
    public int index = 0;

    public CommandTerrainDeform.GroundType currentType = CommandTerrainDeform.GroundType.LINE;

    private final TexturePack props;
    private TextureRegion region;

    // TODO: in the overlay, draw a preview with 0.5f opacity.
    public ToolTerrainDeform(TexturePack props) {
        this.props = props;
        region = CommandTerrainDeform.GroundType.getRegion(props, currentType, index);
    }

    public void toggleType() {
        int type_ordinal = currentType.ordinal();
        type_ordinal++;
        type_ordinal %= CommandTerrainDeform.GroundType.values().length;
        currentType = CommandTerrainDeform.GroundType.values()[type_ordinal];
        index = 0;
        region = CommandTerrainDeform.GroundType.getRegion(props, currentType, index);
    }

    public void selectNext() {
        //index = MathUtils.randomUniformInt(0, MapTokenHouse.regionsBigVillageHouses.length);
        if (currentType == CommandTerrainDeform.GroundType.LINE) {
            index += 1;
            index %= CommandTerrainDeform.regionsGroundLine.length;
        } else if (currentType == CommandTerrainDeform.GroundType.BUMP_RIGHT) {
            index += 1;
            index %= CommandTerrainDeform.regionsGroundBumpRight.length;
        } else if (currentType == CommandTerrainDeform.GroundType.BUMP_LEFT) {
            index += 1;
            index %= CommandTerrainDeform.regionsGroundBumpRight.length;
        } else if (currentType == CommandTerrainDeform.GroundType.BUMP_MIDDLE) {
            index += 1;
            index %= CommandTerrainDeform.regionsGroundBumpMiddle.length;
        }
        region = CommandTerrainDeform.GroundType.getRegion(props, currentType, index);
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(1,1,1,0.5f);
        if (currentType == CommandTerrainDeform.GroundType.LINE) {
            renderer2D.drawTextureRegion(region, x, y, angle, scale, scale);
        } else {
            float realScaleX = scale * (currentType.isRight() ? -1 : 1);
            renderer2D.drawTextureRegion(region, x, y, 0, realScaleX, scale);
        }
        renderer2D.setColor(Color.WHITE);
    }

}
