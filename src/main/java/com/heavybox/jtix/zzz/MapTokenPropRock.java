package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;

public class MapTokenPropRock extends MapToken {

    public static final String[] regions = {
            "assets/app-terrain/rock_big_0.png",
            "assets/app-terrain/rock_big_1.png",
            "assets/app-terrain/rock_big_2.png",
            "assets/app-terrain/rock_small_0.png",
            "assets/app-terrain/rock_small_1.png",
            "assets/app-terrain/rock_small_2.png",
            "assets/app-terrain/rock_small_3.png",
            "assets/app-terrain/rock_small_4.png",
            "assets/app-terrain/rock_small_5.png",
    };

    public final int index;
    private final TextureRegion region;

    public MapTokenPropRock(TexturePack props, int index) {
        super(Type.PROP);
        this.index = index;
        this.region = props.getRegion(regions[index % regions.length]);
    }

    @Override
    public void render(Renderer2D renderer2D) {
        renderer2D.drawTextureRegion(region, x, y, deg, sclX, sclY); // base should never be null.
    }

}
