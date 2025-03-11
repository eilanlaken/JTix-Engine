package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;

public class MapTokenHouseVillage extends MapToken {

    public static final String[] regionsHouseLongLeft = {
            "assets/app-village/house_0.png",
            "assets/app-village/house_1.png",
            "assets/app-village/house_2.png",
    };

    public static final String[] regionsHouseLongRight = regionsHouseLongLeft;

    public static final String[] regionsHouseLongMiddle = {
            "assets/app-village/house_3.png",
            "assets/app-village/house_4.png",
            "assets/app-village/house_5.png",
            "assets/app-village/house_6.png",
            "assets/app-village/house_7.png",
            "assets/app-village/house_8.png",
            "assets/app-village/house_9.png",
            "assets/app-village/house_10.png",
    };

    public static final String[] regionsHouseWideMiddle = {
            "assets/app-village/house_11.png",
            "assets/app-village/house_12.png",
            "assets/app-village/house_13.png",
            "assets/app-village/house_14.png",
            "assets/app-village/house_15.png",
            "assets/app-village/house_16.png",
            "assets/app-village/house_17.png",
            "assets/app-village/house_18.png",
    };

    public static final String[] regionsHouseSmallLeft = {
            "assets/app-village/house_19.png",
            "assets/app-village/house_20.png",
            "assets/app-village/house_21.png",
            "assets/app-village/house_22.png",
            "assets/app-village/house_23.png",
            "assets/app-village/house_24.png",
            "assets/app-village/house_32.png",
            "assets/app-village/house_33.png",
            "assets/app-village/house_34.png",
    };

    public static final String[] regionsHouseSmallRight = regionsHouseSmallLeft;

    public static final String[] regionsHouseSmallMiddle = {
            "assets/app-village/house_25.png",
            "assets/app-village/house_26.png",
            "assets/app-village/house_27.png",
            "assets/app-village/house_28.png",
            "assets/app-village/house_29.png",
            "assets/app-village/house_30.png",
            "assets/app-village/house_31.png",
            "assets/app-village/house_40.png",
            "assets/app-village/house_41.png",
            "assets/app-village/house_42.png",
            "assets/app-village/house_43.png",
    };

    public static final String[] regionsHouseWideLowMiddle = {
            "assets/app-village/house_35.png",
            "assets/app-village/house_36.png",
            "assets/app-village/house_37.png",
            "assets/app-village/house_38.png",
            "assets/app-village/house_39.png",
    };

    public static final String[] regionsHouseWindmills = {
            "assets/app-village/house_44.png",
            "assets/app-village/house_45.png",
    };

    public final HouseType type;
    public final int index;
    private final TextureRegion region;

    public MapTokenHouseVillage(TexturePack props, HouseType type, int index) {
        super(MapToken.Type.CASTLE_BLOCK);
        this.type = type;
        this.index = index;
        this.region = HouseType.getRegion(props, type, index);

    }

    @Override
    public void render(Renderer2D renderer2D) {
        // render shadow?
        renderer2D.setColor(0,0,0,0.2f);
        renderer2D.drawTextureRegion(region, x - 1, y - 1, deg, sclX * 1.05f, sclY * 1.05f); // base should never be null.
        // render house
        renderer2D.setColor(1,1,1,1);
        renderer2D.drawTextureRegion(region, x, y, deg, sclX, sclY); // base should never be null.
    }

    // TODO: add windmill.
    public enum HouseType {

        LONG_LEFT,
        LONG_MIDDLE,
        WIDE_MIDDLE,
        LONG_RIGHT,

        SMALL_LEFT,
        SMALL_MIDDLE,
        LOW_WIDE_MIDDLE,
        SMALL_RIGHT,

        WINDMILL,
        ;

        public boolean isRight() {
            if (this == LONG_RIGHT) return true;
            if (this == SMALL_RIGHT) return true;
            return false;
        }

        public static TextureRegion getRegion(TexturePack props, HouseType type, int index) {
            return switch (type) {
                case LONG_LEFT -> props.getRegion(regionsHouseLongLeft[index % regionsHouseLongLeft.length]);
                case LONG_MIDDLE -> props.getRegion(regionsHouseLongMiddle[index % regionsHouseLongMiddle.length]);
                case WIDE_MIDDLE -> props.getRegion(regionsHouseWideMiddle[index % regionsHouseWideMiddle.length]);
                case LONG_RIGHT -> props.getRegion(regionsHouseLongRight[index % regionsHouseLongRight.length]);

                case SMALL_LEFT -> props.getRegion(regionsHouseSmallLeft[index % regionsHouseSmallLeft.length]);
                case SMALL_MIDDLE -> props.getRegion(regionsHouseSmallMiddle[index % regionsHouseSmallMiddle.length]);
                case LOW_WIDE_MIDDLE -> props.getRegion(regionsHouseWideLowMiddle[index % regionsHouseWideLowMiddle.length]);
                case SMALL_RIGHT -> props.getRegion(regionsHouseSmallRight[index % regionsHouseSmallRight.length]);

                case WINDMILL -> props.getRegion(regionsHouseWindmills[index % regionsHouseWindmills.length]);
            };
        }

    }


}
