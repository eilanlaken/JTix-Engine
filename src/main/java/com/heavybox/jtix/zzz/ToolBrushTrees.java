package com.heavybox.jtix.zzz;

public class ToolBrushTrees extends Tool {

    public static final float BASE_SCALE = 0.25f;

    public Mode mode = Mode.REGULAR;
    public float density = 1;
    public float scaleRange = 0.1f;
    public boolean addFruits = false; // change to probability

    public float lastCreatedX = Float.NEGATIVE_INFINITY;
    public float lastCreatedY = Float.NEGATIVE_INFINITY;

    public enum Mode {
        REGULAR,
        CYPRESS
    }

}
