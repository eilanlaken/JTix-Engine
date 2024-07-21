package com.heavybox.jtix.physics2d;

import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.memory.MemoryPool;

public final class RayCastingIntersection implements MemoryPool.Reset {

    public BodyCollider collider  = null;
    public Vector2      point     = new Vector2();
    public Vector2      direction = new Vector2();
    public float        dst2      = 0;

    public RayCastingIntersection() {}

    @Override
    public void reset() {
        collider = null;
    }

}
