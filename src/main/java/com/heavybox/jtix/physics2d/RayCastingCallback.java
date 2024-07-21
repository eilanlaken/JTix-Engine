package com.heavybox.jtix.physics2d;

import com.heavybox.jtix.collections.Array;

public interface RayCastingCallback {

    void intersected(final Array<RayCastingIntersection> results);

}
