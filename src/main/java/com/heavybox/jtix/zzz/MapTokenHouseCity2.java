package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;

public class MapTokenHouseCity2 extends MapToken {

    public static final String[] regions = {
            "assets/app-city-houses/house-diagonal-big-foundation.png",
            "assets/app-city-houses/house-diagonal-big-foundation-overlay_0.png",
            "assets/app-city-houses/house-diagonal-big-foundation-overlay_1.png",
            "assets/app-city-houses/house-diagonal-big-foundation-overlay_2.png",
            "assets/app-city-houses/house-diagonal-big-foundation-overlay_3.png",
            "assets/app-city-houses/house-diagonal-big-foundation-overlay_4.png",
            "assets/app-city-houses/house-diagonal-big-foundation-overlay_5.png",
            "assets/app-city-houses/house-diagonal-big-foundation-overlay_6.png",
            "assets/app-city-houses/house-diagonal-big-roof-base_blue.png",
            "assets/app-city-houses/house-diagonal-big-roof-base_grey.png",
            "assets/app-city-houses/house-diagonal-big-roof-base_purple.png",
            "assets/app-city-houses/house-diagonal-big-roof-base_red.png",
            "assets/app-city-houses/house-diagonal-big-roof-base_yellow.png",
            "assets/app-city-houses/house-diagonal-big-roof-overlay_0.png",
            "assets/app-city-houses/house-diagonal-big-roof-overlay_1.png",
            "assets/app-city-houses/house-diagonal-big-roof-overlay_2.png",
            "assets/app-city-houses/house-diagonal-big-roof-overlay_3.png",
            "assets/app-city-houses/house-diagonal-big-roof-overlay_4.png",

            "assets/app-city-houses/house-diagonal-small-foundation.png",
            "assets/app-city-houses/house-diagonal-small-foundation-overlay_0.png",
            "assets/app-city-houses/house-diagonal-small-foundation-overlay_1.png",
            "assets/app-city-houses/house-diagonal-small-foundation-overlay_2.png",
            "assets/app-city-houses/house-diagonal-small-foundation-overlay_3.png",
            "assets/app-city-houses/house-diagonal-small-foundation-overlay_4.png",
            "assets/app-city-houses/house-diagonal-small-foundation-overlay_5.png",
            "assets/app-city-houses/house-diagonal-small-foundation-overlay_6.png",
            "assets/app-city-houses/house-diagonal-small-foundation-overlay_7.png",
            "assets/app-city-houses/house-diagonal-small-roof-base_blue.png",
            "assets/app-city-houses/house-diagonal-small-roof-base_grey.png",
            "assets/app-city-houses/house-diagonal-small-roof-base_purple.png",
            "assets/app-city-houses/house-diagonal-small-roof-base_red.png",
            "assets/app-city-houses/house-diagonal-small-roof-base_yellow.png",
            "assets/app-city-houses/house-diagonal-small-roof-overlay_0.png",
            "assets/app-city-houses/house-diagonal-small-roof-overlay_1.png",
            "assets/app-city-houses/house-diagonal-small-roof-overlay_2.png",
            "assets/app-city-houses/house-diagonal-small-roof-overlay_3.png",
            "assets/app-city-houses/house-diagonal-small-roof-overlay_4.png",

            "assets/app-city-houses/house-horizontal-big-foundation.png",
            "assets/app-city-houses/house-horizontal-big-foundation-overlay_0.png",
            "assets/app-city-houses/house-horizontal-big-foundation-overlay_1.png",
            "assets/app-city-houses/house-horizontal-big-foundation-overlay_2.png",
            "assets/app-city-houses/house-horizontal-big-foundation-overlay_3.png",
            "assets/app-city-houses/house-horizontal-big-foundation-overlay_4.png",
            "assets/app-city-houses/house-horizontal-big-foundation-overlay_5.png",
            "assets/app-city-houses/house-horizontal-big-foundation-overlay_6.png",
            "assets/app-city-houses/house-horizontal-big-foundation-overlay_7.png",
            "assets/app-city-houses/house-horizontal-big-roof-base_blue.png",
            "assets/app-city-houses/house-horizontal-big-roof-base_purple.png",
            "assets/app-city-houses/house-horizontal-big-roof-base_grey.png",
            "assets/app-city-houses/house-horizontal-big-roof-base_red.png",
            "assets/app-city-houses/house-horizontal-big-roof-base_yellow.png",
            "assets/app-city-houses/house-horizontal-big-roof-overlay_0.png",
            "assets/app-city-houses/house-horizontal-big-roof-overlay_1.png",
            "assets/app-city-houses/house-horizontal-big-roof-overlay_2.png",
            "assets/app-city-houses/house-horizontal-big-roof-overlay_3.png",
            "assets/app-city-houses/house-horizontal-big-roof-overlay_4.png",
            "assets/app-city-houses/house-horizontal-big-roof-overlay_5.png",
            "assets/app-city-houses/house-horizontal-big-roof-overlay_6.png",
            "assets/app-city-houses/house-horizontal-big-roof-overlay_7.png",
            "assets/app-city-houses/house-horizontal-big-roof-overlay_8.png",

            "assets/app-city-houses/house-horizontal-small-foundation.png",
            "assets/app-city-houses/house-horizontal-small-foundation-overlay_0.png",
            "assets/app-city-houses/house-horizontal-small-foundation-overlay_1.png",
            "assets/app-city-houses/house-horizontal-small-foundation-overlay_2.png",
            "assets/app-city-houses/house-horizontal-small-foundation-overlay_3.png",
            "assets/app-city-houses/house-horizontal-small-foundation-overlay_4.png",
            "assets/app-city-houses/house-horizontal-small-foundation-overlay_5.png",
            "assets/app-city-houses/house-horizontal-small-foundation-overlay_6.png",
            "assets/app-city-houses/house-horizontal-small-roof-base_blue.png",
            "assets/app-city-houses/house-horizontal-small-roof-base_grey.png",
            "assets/app-city-houses/house-horizontal-small-roof-base_purple.png",
            "assets/app-city-houses/house-horizontal-small-roof-base_red.png",
            "assets/app-city-houses/house-horizontal-small-roof-base_yellow.png",
            "assets/app-city-houses/house-horizontal-small-roof-overlay_0.png",
            "assets/app-city-houses/house-horizontal-small-roof-overlay_1.png",
            "assets/app-city-houses/house-horizontal-small-roof-overlay_2.png",
            "assets/app-city-houses/house-horizontal-small-roof-overlay_3.png",
            "assets/app-city-houses/house-horizontal-small-roof-overlay_4.png",
            "assets/app-city-houses/house-horizontal-small-roof-overlay_5.png",
            "assets/app-city-houses/house-horizontal-small-roof-overlay_6.png",
            "assets/app-city-houses/house-horizontal-small-roof-overlay_7.png",
            "assets/app-city-houses/house-horizontal-small-roof-overlay_8.png",

            "assets/app-city-houses/house-vertical-big-foundation.png",
            "assets/app-city-houses/house-vertical-big-foundation-overlay_0.png",
            "assets/app-city-houses/house-vertical-big-foundation-overlay_1.png",
            "assets/app-city-houses/house-vertical-big-foundation-overlay_2.png",
            "assets/app-city-houses/house-vertical-big-foundation-overlay_3.png",
            "assets/app-city-houses/house-vertical-big-foundation-overlay_4.png",
            "assets/app-city-houses/house-vertical-big-foundation-overlay_5.png",
            "assets/app-city-houses/house-vertical-big-roof-base_blue.png",
            "assets/app-city-houses/house-vertical-big-roof-base_grey.png",
            "assets/app-city-houses/house-vertical-big-roof-base_purple.png",
            "assets/app-city-houses/house-vertical-big-roof-base_red.png",
            "assets/app-city-houses/house-vertical-big-roof-base_yellow.png",
            "assets/app-city-houses/house-vertical-big-roof-overlay_0.png",
            "assets/app-city-houses/house-vertical-big-roof-overlay_1.png",
            "assets/app-city-houses/house-vertical-big-roof-overlay_2.png",
            "assets/app-city-houses/house-vertical-big-roof-overlay_3.png",
            "assets/app-city-houses/house-vertical-big-roof-overlay_4.png",

            "assets/app-city-houses/house-vertical-small-foundation.png",
            "assets/app-city-houses/house-vertical-small-foundation-overlay_0.png",
            "assets/app-city-houses/house-vertical-small-foundation-overlay_1.png",
            "assets/app-city-houses/house-vertical-small-foundation-overlay_2.png",
            "assets/app-city-houses/house-vertical-small-foundation-overlay_3.png",
            "assets/app-city-houses/house-vertical-small-foundation-overlay_4.png",
            "assets/app-city-houses/house-vertical-small-foundation-overlay_5.png",
            "assets/app-city-houses/house-vertical-small-roof-base_blue.png",
            "assets/app-city-houses/house-vertical-small-roof-base_grey.png",
            "assets/app-city-houses/house-vertical-small-roof-base_purple.png",
            "assets/app-city-houses/house-vertical-small-roof-base_red.png",
            "assets/app-city-houses/house-vertical-small-roof-base_yellow.png",
            "assets/app-city-houses/house-vertical-small-roof-overlay_0.png",
            "assets/app-city-houses/house-vertical-small-roof-overlay_1.png",
            "assets/app-city-houses/house-vertical-small-roof-overlay_2.png",
            "assets/app-city-houses/house-vertical-small-roof-overlay_3.png",
            "assets/app-city-houses/house-vertical-small-roof-overlay_4.png",

    };

    public Direction direction;
    public Size size;
    public Look look;

    private TexturePack props;

    public TextureRegion foundation;
    public TextureRegion foundationOverlay;
    public TextureRegion roof;
    public TextureRegion roofOverlay;

    public MapTokenHouseCity2(TexturePack props, Direction direction, Size size, Look look) {
        super(Type.HOUSE);
        this.props = props;
        this.direction = direction;
        this.size = size;
        this.look = look;
    }

    @Override
    public void render(Renderer2D renderer2D) {

    }

    public static String getRegionFoundation(Direction direction, Size size) {
        return "assets/app-city-houses/house-" + direction.regionStr + "-" + size.regionStr + "-foundation.png";
    }

    public static String getRegionFoundationOverlay(Direction direction, Size size, int index) {
        return "assets/app-city-houses/house-" + direction.regionStr + "-" + size.regionStr + "-foundation-overlay_" + index + ".png";
    }

    public static String getRegionRoof(Direction direction, Size size, Look look) {
        return "assets/app-city-houses/house-" + direction.regionStr + "-" + size.regionStr + "-roof-base_" + look.regionStr + ".png";
    }

    public static String getRegionRoofOverlay(Direction direction, Size size, int index) {
        return "assets/app-city-houses/house-" + direction.regionStr + "-" + size.regionStr + "-roof-overlay_" + index + ".png";
    }

    public enum Direction {
        LEFT("diagonal"),
        RIGHT("diagonal"),
        HORIZONTAL("horizontal"),
        VERTICAL("vertical"),
        ;

        String regionStr;

        Direction(String regionStr) {
            this.regionStr = regionStr;
        }

    }

    public enum Size {
        BIG("big"),
        SMALL("small"),
        ;

        String regionStr;

        Size(String regionStr) {
            this.regionStr = regionStr;
        }
    }

    public enum Look {
        RED("red"),
        YELLOW("yellow"),
        GREY("grey"),
        BLUE("blue"),
        PURPLE("purple"),
        ;

        String regionStr;

        Look(String regionStr) {
            this.regionStr = regionStr;
        }
    }

}
