package com.heavybox.jtix.zzz;

import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.graphics.Color;

public class CommandTerrainDrawWheat extends Command {

    public float linesAngle = 60;
    public Color color;
    public float[] polygon;
    public float[] outline;

    public CommandTerrainDrawWheat(ArrayFloat p, Color color) {
        this.polygon = new float[p.size];
        for (int i = 0; i < p.size; i++) {
            this.polygon[i] = p.get(i);
        }

        // create a closed path
        this.outline = new float[p.size + 2];
        for (int i = 0; i < p.size; i++) {
            this.outline[i] = p.get(i);
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
