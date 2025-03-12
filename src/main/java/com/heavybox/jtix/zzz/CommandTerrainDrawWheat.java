package com.heavybox.jtix.zzz;

import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.graphics.Color;

import java.util.Arrays;

public class CommandTerrainDrawWheat extends Command {

    public float linesAngle = 60;
    public Color color;
    public float[] polygon;
    public float[] outline;

    public CommandTerrainDrawWheat(float[] p, Color color) {
        polygon = Arrays.copyOf(p, p.length);
        // create a closed path
        this.outline = new float[p.length + 2];
        for (int i = 0; i < p.length; i++) {
            this.outline[i] = p[i];
        }
        this.outline[outline.length - 2] = outline[0];
        this.outline[outline.length - 1] = outline[1];

        this.color = new Color(color);
    }

    @Override
    protected void execute() {

    }

    @Override
    protected void undo() {

    }

}
