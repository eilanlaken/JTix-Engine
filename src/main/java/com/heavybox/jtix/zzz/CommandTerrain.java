package com.heavybox.jtix.zzz;

public class CommandTerrain extends Command {

    public static final int DRAW_WATER = 0;
    public static final int DRAW_GRASS = 1;
    public static final int DRAW_ROAD = 2;
    public static final int DRAW_WHEAT = 3;

    public int mask = 1;

    // brush type circle
    public float r = 40;
    public int refinement = 100;
    public float x, y, deg, sclX = 1, sclY = 1;

}
