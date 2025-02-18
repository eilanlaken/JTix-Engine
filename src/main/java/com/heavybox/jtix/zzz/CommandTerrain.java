package com.heavybox.jtix.zzz;

public class CommandTerrain extends Command {

//    public static final int DRAW_OUTLINE = 1;
    public static final int WATER_MASK = 0;
    public static final int GRASS_MASK = 2;
    public static final int ROAD_MASK = 3;
    public static final int WHEAT_MASK = 4;

    public int mask = 1;

    // brush type circle
    public float r = 40;
    public int refinement = 100;

}
