package com.heavybox.jtix.zzz;

public class ToolBrushTrees extends Tool {

    public static final float BASE_SCALE = 0.20f;
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


    public Mode mode = Mode.REGULAR;
    public float density = 1;
    public float scaleRange = 0.1f;
    public boolean addFruits = false; // change to probability

    public float lastCreatedX = Float.NEGATIVE_INFINITY;
    public float lastCreatedY = Float.NEGATIVE_INFINITY;

    public ToolBrushTrees() {
    }

    @Override
    public void update() {

    }

    public enum Mode {
        REGULAR,
        CYPRESS
    }

}
