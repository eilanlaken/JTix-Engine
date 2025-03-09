package com.heavybox.jtix.zzz;

import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.graphics.Color;

public class CommandTerrainDrawWheat extends Command {

    public float linesAngle = 30;
    public Color color;
    public float[] polygon;

    public CommandTerrainDrawWheat(ArrayFloat p, Color color) {
        this.polygon = new float[p.size];
        for (int i = 0; i < p.size; i++) {
            this.polygon[i] = p.get(i);
        }
        this.color = new Color(color);
    }

    @Override
    protected void execute() {

    }

    @Override
    protected void undo() {

    }

}
