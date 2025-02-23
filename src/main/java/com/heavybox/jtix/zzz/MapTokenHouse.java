package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;

public class MapTokenHouse extends MapToken {

    public static final String[] regionsSmallVillageHouses = {
            "assets/app-rural/rural_house_13.png",
            "assets/app-rural/rural_house_14.png",
            "assets/app-rural/rural_house_15.png",
            "assets/app-rural/rural_house_16.png",
            "assets/app-rural/rural_house_17.png",
            "assets/app-rural/rural_house_18.png",
            "assets/app-rural/rural_house_19.png",
            "assets/app-rural/rural_house_20.png",
    };

    public static final String[] regionsBigVillageHouses = {
            //"assets/app-rural/rural_house_1.png",
            //"assets/app-rural/rural_house_2.png",
            //"assets/app-rural/rural_house_3.png",
            //"assets/app-rural/rural_house_4.png",
            "assets/app-rural/rural_house_5.png",
            "assets/app-rural/rural_house_6.png",
            "assets/app-rural/rural_house_7.png",
            "assets/app-rural/rural_house_8.png",
            "assets/app-rural/rural_house_9.png",
            "assets/app-rural/rural_house_10.png",
            "assets/app-rural/rural_house_11.png",
            "assets/app-rural/rural_house_12.png",
            "assets/app-rural/rural_house_21.png",
            "assets/app-rural/rural_house_22.png",
            "assets/app-rural/rural_house_23.png",
            "assets/app-rural/rural_house_24.png",
            "assets/app-rural/rural_house_25.png",
    };

    @Deprecated public static final String[] regionsCity = { // break down into long and short
            // CHANGES
            "assets/app-rural/rural_house_1.png",
            "assets/app-rural/rural_house_2.png",
            "assets/app-rural/rural_house_3.png",
            "assets/app-rural/rural_house_4.png",

            "assets/app-city/city-house_1.png",
            "assets/app-city/city-house_2.png",
            "assets/app-city/city-house_3.png",
            "assets/app-city/city-house_4.png",
            "assets/app-city/city-house_5.png",
            "assets/app-city/city-house_6.png",
            "assets/app-city/city-house_7.png",
            "assets/app-city/city-house_8.png",
            "assets/app-city/city-house_9.png",
            "assets/app-city/city-house_10.png",
            "assets/app-city/city-house_11.png",
            "assets/app-city/city-house_12.png",
            "assets/app-city/city-house_13.png",
            "assets/app-city/city-house_14.png",
            "assets/app-city/city-house_15.png",
            "assets/app-city/city-house_16.png",
            "assets/app-city/city-house_17.png",
            "assets/app-city/city-house_18.png",
            "assets/app-city/city-house_19.png",
            "assets/app-city/city-house_20.png",
            "assets/app-city/city-house_21.png",
            "assets/app-city/city-house_22.png",
            "assets/app-city/city-house_23.png",
            "assets/app-city/city-house_24.png",
            "assets/app-city/city-house_25.png",
            "assets/app-city/city-house_26.png",
            "assets/app-city/city-house_27.png",
            "assets/app-city/city-house_28.png",
            "assets/app-city/city-house_29.png",
            "assets/app-city/city-house_30.png",
            "assets/app-city/city-house_31.png",
            "assets/app-city/city-house_32.png",
    };

    public final HouseType type;
    public final int index;
    public final TextureRegion region;

    public MapTokenHouse(TexturePack props, HouseType type, int index) {
        super(Type.HOUSE);
        this.type = type;
        this.index = index;

        this.region = switch (type) {
            case BIG_VILLAGE_HOUSE -> props.getRegion(regionsBigVillageHouses[index % regionsBigVillageHouses.length]);
            case SMALL_VILLAGE_HOUSE -> props.getRegion(regionsSmallVillageHouses[index % regionsSmallVillageHouses.length]);

            // TODO: big and small city houses.
            case BIG_CITY_HOUSE -> props.getRegion(regionsCity[index % regionsCity.length]);
            case SMALL_CITY_HOUSE -> props.getRegion(regionsCity[index % regionsCity.length]);
        };
    }

    @Override
    public void render(Renderer2D renderer2D) {
        renderer2D.setColor(Color.WHITE);
        renderer2D.drawTextureRegion(region, x, y, deg, sclX, sclY); // base should never be null.
    }

    public enum HouseType {
        BIG_VILLAGE_HOUSE,
        SMALL_VILLAGE_HOUSE,
        BIG_CITY_HOUSE,
        SMALL_CITY_HOUSE,
    }

}
