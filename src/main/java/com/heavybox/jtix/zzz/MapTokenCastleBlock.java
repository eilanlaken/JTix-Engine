package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;

public class MapTokenCastleBlock extends MapToken {

    public static final String[] regionsTowerTall = {
            "assets/app-castles/castle-tower-block_1.png",
            "assets/app-castles/castle-tower-block_2.png",
            "assets/app-castles/castle-tower-block_3.png",
            "assets/app-castles/castle-tower-block_4.png",
            "assets/app-castles/castle-tower-block_5.png",
            "assets/app-castles/castle-tower-block_6.png",
            "assets/app-castles/castle-tower-block_16.png",
    };

    public static final String[] regionsTowerShort = {
            "assets/app-castles/castle-tower-block_7.png",
            "assets/app-castles/castle-tower-block_8.png",
            "assets/app-castles/castle-tower-block_9.png",
            "assets/app-castles/castle-tower-block_10.png",
            "assets/app-castles/castle-tower-block_11.png",
            "assets/app-castles/castle-tower-block_12.png",
            "assets/app-castles/castle-tower-block_13.png",
            "assets/app-castles/castle-tower-block_14.png",
            "assets/app-castles/castle-tower-block_15.png",
    };

    public final BlockType type;
    public final int index;

    private TextureRegion region;

    public MapTokenCastleBlock(TexturePack props, BlockType type, int index) {
        super(Type.CASTLE_BLOCK);
        this.type = type;
        this.index = index;

        if (type == BlockType.TOWER_TALL)
            this.region = props.getRegion(regionsTowerTall[index % regionsTowerTall.length]);
        if (type == BlockType.TOWER_SHORT)
            this.region = props.getRegion(regionsTowerShort[index % regionsTowerShort.length]);
    }

    @Override
    public void render(Renderer2D renderer2D) {
        // TODO: flip sclY for short buildings.
        // TODO: set sclY to sclY * 0.5f for short middle buildings.
        renderer2D.drawTextureRegion(region, x, y, deg, sclX, sclY); // base should never be null.
    }

    public enum BlockType {
        TOWER_TALL,
        TOWER_SHORT,

        BUILDING_TALL_LEFT,
        BUILDING_TALL_MIDDLE,
        BUILDING_TALL_RIGHT,

        BUILDING_SHORT_LEFT,
        BUILDING_SHORT_MIDDLE,
        BUILDING_SHORT_RIGHT,

        WALL_BACK_LEFT,
        WALL_BACK_MIDDLE,
        WALL_BACK_RIGHT,

        WALL_FRONT_LEFT,
        WALL_FRONT_MIDDLE,
        WALL_FRONT_RIGHT,
    }

}
