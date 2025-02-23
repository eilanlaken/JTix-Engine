package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;

public class MapTokenRuralProp extends MapToken {

    public static final String[] regionsProps = {
            "assets/app-rural/rural_prop_fence_bar.png",
            "assets/app-rural/rural_prop_fence_post.png",
            "assets/app-rural/rural_prop_log_1.png",
            "assets/app-rural/rural_prop_log_2.png",
            "assets/app-rural/rural_prop_log_3.png",
            "assets/app-rural/rural_prop_trunk_chopped.png",
            "assets/app-rural/rural_prop_well.png",
            "assets/app-rural/rural_prop_windmill.png",
            "assets/app-rural/rural_tower_1.png",
            "assets/app-rural/rural_tower_2.png",
            "assets/app-rural/rural_tower_3.png",
            "assets/app-rural/rural_tower_4.png",
            "assets/app-rural/rural_tower_5.png",
    };

    public final int index;
    public final TextureRegion region;

    public MapTokenRuralProp(TexturePack props, int index) {
        super(Type.PROP);
        this.index = index;

        this.region = props.getRegion(regionsProps[index % regionsProps.length]);
    }

    @Override
    public void render(Renderer2D renderer2D) {
        renderer2D.setColor(Color.WHITE);
        renderer2D.drawTextureRegion(region, x, y, deg, sclX, sclY); // base should never be null.
    }

}
