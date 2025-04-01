package com.heavybox.jtix.zzz.v1;

import com.heavybox.jtix.graphics.Texture;

public class CommandTerrainCarve extends Command {

    public static final int WATER_MASK = 0;
    public static final int GRASS_MASK = 1;

    public Texture texture;
    public int mask = WATER_MASK;

    @Override
    protected void execute() {

    }

    @Override
    protected void undo() {

    }
}
