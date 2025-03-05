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

    public static final String[] regionsBuildingTallLeft = {
            "assets/app-castles/castle-building-block_5.png",
            "assets/app-castles/castle-building-block_6.png",
            "assets/app-castles/castle-building-block_7.png",
            "assets/app-castles/castle-building-block_8.png",
            "assets/app-castles/castle-building-block_9.png",
            "assets/app-castles/castle-building-block_10.png",
    };

    public static final String[] regionsBuildingTallMiddle = {
            "assets/app-castles/castle-building-block_11.png",
            "assets/app-castles/castle-building-block_12.png",
            "assets/app-castles/castle-building-block_13.png",
            "assets/app-castles/castle-building-block_14.png",
            "assets/app-castles/castle-building-block_15.png",
            "assets/app-castles/castle-building-block_16.png",
    };

    public static final String[] regionsBuildingTallWideMiddle = {
            "assets/app-castles/castle-building-block_17.png",
            "assets/app-castles/castle-building-block_18.png",
            "assets/app-castles/castle-building-block_19.png",
            "assets/app-castles/castle-building-block_20.png",
            "assets/app-castles/castle-building-block_21.png",
            "assets/app-castles/castle-building-block_22.png",
            "assets/app-castles/castle-building-block_23.png",
            "assets/app-castles/castle-building-block_27.png",
            "assets/app-castles/castle-building-block_28.png",
    };

    public static final String[] regionsBuildingTallRight = regionsBuildingTallLeft;

    public static final String[] regionsBuildingShortLeft = {
            "assets/app-castles/castle-building-block_1.png",
            "assets/app-castles/castle-building-block_2.png",
            "assets/app-castles/castle-building-block_3.png",
            "assets/app-castles/castle-building-block_4.png",
    };

    public static final String[] regionsBuildingShortMiddle = {
            "assets/app-castles/castle-building-block_24.png",
            "assets/app-castles/castle-building-block_25.png",
            "assets/app-castles/castle-building-block_26.png",
    };

    public static final String[] regionsBuildingShortRight = regionsBuildingShortLeft;

    public static final String[] regionsWallFrontLeft = {
            "assets/app-castles/castle-wall-front-block_2.png",
            "assets/app-castles/castle-wall-front-block_4.png",
            "assets/app-castles/castle-wall-front-block_6.png",
            "assets/app-castles/castle-wall-front-block_8.png",
            "assets/app-castles/castle-wall-front-block_10.png",
    };

    public static final String[] regionsWallFrontMiddle = regionsBuildingTallWideMiddle;

    public static final String[] regionsWallFrontRight = regionsWallFrontLeft;

    public static final String[] regionsWallBackLeft = {
            "assets/app-castles/castle-wall-back-block_2.png",
            "assets/app-castles/castle-wall-back-block_4.png",
            "assets/app-castles/castle-wall-back-block_6.png",
            "assets/app-castles/castle-wall-back-block_8.png",
    };

    public static final String[] regionsWallBackMiddle = regionsBuildingTallMiddle;

    public static final String[] regionsWallBackRight = regionsWallBackLeft;

    public final BlockType type;
    public final int index;

    private final TextureRegion region;

    public MapTokenCastleBlock(TexturePack props, BlockType type, int index) {
        super(Type.CASTLE_BLOCK);
        this.type = type;
        this.index = index;

        this.region = BlockType.getRegion(props, type, index);

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
        ;

        public boolean isRight() {
            if (this == WALL_BACK_RIGHT) return true;
            if (this == WALL_FRONT_RIGHT) return true;
            if (this == BUILDING_SHORT_RIGHT) return true;
            if (this == BUILDING_TALL_RIGHT) return true;
            return false;
        }

        public static TextureRegion getRegion(TexturePack props, BlockType type, int index) {
            return switch (type) {
                case TOWER_TALL -> props.getRegion(regionsTowerTall[index % regionsTowerTall.length]);
                case TOWER_SHORT -> props.getRegion(regionsTowerShort[index % regionsTowerShort.length]);

                case BUILDING_TALL_LEFT -> props.getRegion(regionsBuildingTallLeft[index % regionsBuildingTallLeft.length]);
                case BUILDING_TALL_MIDDLE -> props.getRegion(regionsBuildingTallMiddle[index % regionsBuildingTallMiddle.length]);
                case BUILDING_TALL_RIGHT -> props.getRegion(regionsBuildingTallRight[index % regionsBuildingTallLeft.length]);

                case BUILDING_SHORT_LEFT -> props.getRegion(regionsBuildingShortLeft[index % regionsBuildingShortLeft.length]);
                case BUILDING_SHORT_MIDDLE -> props.getRegion(regionsBuildingShortMiddle[index % regionsBuildingShortMiddle.length]);
                case BUILDING_SHORT_RIGHT -> props.getRegion(regionsBuildingShortRight[index % regionsBuildingShortLeft.length]);

                case WALL_BACK_LEFT -> props.getRegion(regionsWallBackLeft[index % regionsWallBackLeft.length]);
                case WALL_BACK_MIDDLE -> props.getRegion(regionsWallBackMiddle[index % regionsWallBackMiddle.length]);
                case WALL_BACK_RIGHT -> props.getRegion(regionsWallBackRight[index % regionsWallBackLeft.length]);

                case WALL_FRONT_LEFT -> props.getRegion(regionsWallFrontLeft[index % regionsWallFrontLeft.length]);
                case WALL_FRONT_MIDDLE -> props.getRegion(regionsWallFrontMiddle[index % regionsWallFrontMiddle.length]);
                case WALL_FRONT_RIGHT -> props.getRegion(regionsWallFrontRight[index % regionsWallFrontLeft.length]);
            };
        }

    }

}
