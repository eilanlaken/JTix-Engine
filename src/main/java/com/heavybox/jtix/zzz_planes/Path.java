package com.heavybox.jtix.zzz_planes;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.Vector3;

public class Path {

    private boolean closed = true; // for this project, assume closed paths only.
    public Array<Vector3> points = new Array<>(true, 10);
    public Array<Vector3> dirs = new Array<>(true, 10);

    // TODO
    public Path joinLine(Vector3 p0, Vector3 p1, int subdivision) {

        return this;
    }

    public Path joinBezierQuadratic(Vector3 p0, Vector3 p1, Vector3 p2, int subdivision) {

        return this;
    }

    public void update() {
        // recalculate directions
    }

}
