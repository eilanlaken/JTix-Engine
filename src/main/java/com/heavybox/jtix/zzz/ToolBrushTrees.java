package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;

public class ToolBrushTrees extends Tool {

    public static final String[] regionsRegular = {
            "assets/app-trees/tree_regular_1.png",
            "assets/app-trees/tree_regular_2.png",
            "assets/app-trees/tree_regular_3.png",
            "assets/app-trees/tree_regular_4.png",
            "assets/app-trees/tree_regular_5.png",
            "assets/app-trees/tree_regular_6.png",
    };
    public static final String[] regionsCypress = {
            "assets/app-trees/tree_cypress_1.png",
            "assets/app-trees/tree_cypress_2.png",
            "assets/app-trees/tree_cypress_3.png",
            "assets/app-trees/tree_cypress_4.png",
    };
    public static final String[] regionsTrunks = {
            "assets/app-trees/tree_regular_trunk_1.png",
            "assets/app-trees/tree_regular_trunk_2.png",
            "assets/app-trees/tree_regular_trunk_3.png",
            "assets/app-trees/tree_regular_trunk_4.png",
            "assets/app-trees/tree_regular_trunk_5.png",
            "assets/app-trees/tree_regular_trunk_6.png",
            "assets/app-trees/tree_regular_trunk_7.png",
            "assets/app-trees/tree_regular_trunk_8.png",
            "assets/app-trees/tree_regular_trunk_9.png",
            "assets/app-trees/tree_regular_trunk_10.png",
    };
    public static final String regionFruitsRegular = "assets/app-trees/tree_regular_fruits.png";
    public static final String regionFruitsCypress = "assets/app-trees/tree_cypress_fruits.png";


    public MapTokenTree.Species species = MapTokenTree.Species.REGULAR;
    public float flux = 1;
    public float density = 1;
    public float scaleRange = 0.05f;
    public float scale = 0.20f;
    public boolean addFruits = false; // change to probability

    public float lastCreatedX = Float.NEGATIVE_INFINITY;
    public float lastCreatedY = Float.NEGATIVE_INFINITY;

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.drawCircleThin(flux * 20, 30, x, y, deg, sclX, sclY);
    }

}
