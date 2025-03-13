package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;

@Deprecated
public class MapTokenHouseCity extends MapToken {

    public static final String[] regionsHouseBlueLeft = {
            "assets/app-city/house_4.png",
            "assets/app-city/house_5.png",
    };

    public static final String[] regionsHouseBlueMiddle = {
            "assets/app-city/house_0.png",
            "assets/app-city/house_1.png",
            "assets/app-city/house_2.png",
            "assets/app-city/house_3.png",
    };

    public static final String[] regionsHouseBlueVertical = {
            "assets/app-city/house_6.png",
    };

    public static final String[] regionsHouseBlueRight = regionsHouseBlueLeft;

    ////////////////////////////

    public static final String[] regionsHouseGreyLeft = {
            "assets/app-city/house_7.png",
            "assets/app-city/house_12.png",
    };


    public static final String[] regionsHouseGreyMiddle = {
            "assets/app-city/house_8.png",
            "assets/app-city/house_9.png",
            "assets/app-city/house_10.png",
            "assets/app-city/house_11.png",
    };

    public static final String[] regionsHouseGreyVertical = {
            "assets/app-city/house_13.png",
    };

    public static final String[] regionsHouseGreyRight = regionsHouseGreyLeft;

    ////////////////////////////

    public static final String[] regionsHouseRedLeft = {
            "assets/app-city/house_14.png",
            "assets/app-city/house_17.png",
            "assets/app-city/house_22.png",
            "assets/app-city/house_23.png",
            "assets/app-city/house_27.png",
    };


    public static final String[] regionsHouseRedMiddle = {
            "assets/app-city/house_15.png",
            "assets/app-city/house_16.png",
            "assets/app-city/house_18.png",
            "assets/app-city/house_19.png",
            "assets/app-city/house_20.png",
            "assets/app-city/house_21.png",
            "assets/app-city/house_25.png",
    };

    public static final String[] regionsHouseRedVertical = {
            "assets/app-city/house_24.png",
            "assets/app-city/house_26.png",
    };

    public static final String[] regionsHouseRedRight = regionsHouseRedLeft;

    ////////////////////////////

    public static final String[] regionsHouseRoyalLeft = {
            "assets/app-castles/castle-building-block_1.png",
            "assets/app-castles/castle-building-block_2.png",
            "assets/app-castles/castle-building-block_3.png",
            "assets/app-castles/castle-building-block_4.png",
    };


    public static final String[] regionsHouseRoyalMiddle = {
            "assets/app-castles/castle-building-block_24.png",
            "assets/app-castles/castle-building-block_26.png",
    };

    public static final String[] regionsHouseRoyalVertical = {
            "assets/app-castles/castle-building-block_29.png",
            "assets/app-castles/castle-building-block_30.png",
            "assets/app-castles/castle-building-block_31.png",
            "assets/app-castles/castle-building-block_32.png",
            "assets/app-castles/castle-building-block_33.png",
    };

    public static final String[] regionsHouseRoyalRight = regionsHouseRoyalLeft;

    public final HouseType type;
    public final int index;
    private final TextureRegion region;

    public MapTokenHouseCity(TexturePack props, HouseType type, int index) {
        super(MapToken.Type.CASTLE_BLOCK);
        this.type = type;
        this.index = index;
        this.region = HouseType.getRegion(props, type, index);

    }

    @Override
    public void render(Renderer2D renderer2D) {
        renderer2D.drawTextureRegion(region, x, y, deg, sclX, sclY); // base should never be null.
    }

    // TODO: add windmill.
    public enum HouseType {

        BLUE_LEFT,
        BLUE_MIDDLE,
        BLUE_VERTICAL,
        BLUE_RIGHT,

        GREY_LEFT,
        GREY_MIDDLE,
        GREY_VERTICAL,
        GREY_RIGHT,

        RED_LEFT,
        RED_MIDDLE,
        RED_VERTICAL,
        RED_RIGHT,

        ROYAL_LEFT,
        ROYAL_MIDDLE,
        ROYAL_VERTICAL,
        ROYAL_RIGHT,
        ;

        public boolean isRight() {
            if (this == BLUE_RIGHT) return true;
            if (this == GREY_RIGHT) return true;
            if (this == RED_RIGHT) return true;
            if (this == ROYAL_RIGHT) return true;
            return false;
        }

        public static TextureRegion getRegion(TexturePack props, HouseType type, int index) {
            return switch (type) {
                case BLUE_LEFT -> props.getRegion(regionsHouseBlueLeft[index % regionsHouseBlueLeft.length]);
                case BLUE_MIDDLE -> props.getRegion(regionsHouseBlueMiddle[index % regionsHouseBlueMiddle.length]);
                case BLUE_VERTICAL -> props.getRegion(regionsHouseBlueVertical[index % regionsHouseBlueVertical.length]);
                case BLUE_RIGHT -> props.getRegion(regionsHouseBlueRight[index % regionsHouseBlueRight.length]);

                case GREY_LEFT -> props.getRegion(regionsHouseGreyLeft[index % regionsHouseGreyLeft.length]);
                case GREY_MIDDLE -> props.getRegion(regionsHouseGreyMiddle[index % regionsHouseGreyMiddle.length]);
                case GREY_VERTICAL -> props.getRegion(regionsHouseGreyVertical[index % regionsHouseGreyVertical.length]);
                case GREY_RIGHT -> props.getRegion(regionsHouseGreyRight[index % regionsHouseGreyRight.length]);

                case RED_LEFT -> props.getRegion(regionsHouseRedLeft[index % regionsHouseRedLeft.length]);
                case RED_MIDDLE -> props.getRegion(regionsHouseRedMiddle[index % regionsHouseRedMiddle.length]);
                case RED_VERTICAL -> props.getRegion(regionsHouseRedVertical[index % regionsHouseRedVertical.length]);
                case RED_RIGHT -> props.getRegion(regionsHouseRedRight[index % regionsHouseRedRight.length]);

                case ROYAL_LEFT -> props.getRegion(regionsHouseRoyalLeft[index % regionsHouseRoyalLeft.length]);
                case ROYAL_MIDDLE -> props.getRegion(regionsHouseRoyalMiddle[index % regionsHouseRoyalMiddle.length]);
                case ROYAL_VERTICAL -> props.getRegion(regionsHouseRoyalVertical[index % regionsHouseRoyalVertical.length]);
                case ROYAL_RIGHT -> props.getRegion(regionsHouseRoyalRight[index % regionsHouseRoyalRight.length]);
            };
        }

    }

}
