package com.heavybox.jtix.physics2d;

import com.heavybox.jtix.memory.MemoryPool;

public final class RayCastingRay implements MemoryPool.Reset {

    public float originX;
    public float originY;
    public float dirX;
    public float dirY;
    public float dst;
    public int   bitmask;

    public RayCastingRay() {}

    @Override
    public void reset() {
        dst = Float.POSITIVE_INFINITY;
        bitmask = 0;
    }

}