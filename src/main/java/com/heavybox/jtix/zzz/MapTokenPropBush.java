package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;

public class MapTokenPropBush extends MapToken {

    public static final String[] regions = {
            "assets/app-trees/bush_1.png",
            "assets/app-trees/bush_2.png",
            "assets/app-trees/bush_3.png",
            "assets/app-trees/bush_4.png",
            "assets/app-trees/bush_5.png",
            "assets/app-trees/bush_6.png",
    };

    public final int index;
    private final TextureRegion region;

    public MapTokenPropBush(TexturePack props, int index) {
        super(Type.PROP);
        this.index = index;
        this.region = props.getRegion(regions[index % regions.length]);
    }

    @Override
    public void render(Renderer2D renderer2D) {
        renderer2D.drawTextureRegion(region, x, y, deg, sclX, sclY); // base should never be null.
    }

}
