package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;

public class MapTokenPropVillage extends MapToken {

    public static final String[] regions = {
            "assets/app-village/prop_log_1.png",
            "assets/app-village/prop_log_2.png",
            "assets/app-village/prop_log_3.png",
            "assets/app-village/prop_trunk_chopped.png",
            "assets/app-village/prop_pile_1.png",
            "assets/app-village/prop_pile_2.png",
            "assets/app-village/prop_tower_1.png",
            "assets/app-village/prop_tower_2.png",
            "assets/app-village/prop_sack_of_flour.png",
    };

    public final int index;
    private final TextureRegion region;

    public MapTokenPropVillage(TexturePack props, int index) {
        super(MapToken.Type.CASTLE_BLOCK);
        this.index = index;
        this.region = props.getRegion(regions[index % regions.length]);
    }

    @Override
    public void render(Renderer2D renderer2D) {
        renderer2D.drawTextureRegion(region, x, y, deg, sclX, sclY); // base should never be null.
    }

}
