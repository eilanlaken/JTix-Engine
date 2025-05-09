package com.heavybox.jtix.zzz_planes;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.Vector3;

public class Path {

    private boolean closed = true; // for this project, assume closed paths only.
    public Array<Vector3> points = new Array<>(true, 10);
    public Array<Vector3> dirs = new Array<>(true, 10);

    // TODO
    public Path joinLine(Vector3 p0, Vector3 p1, int subdivisions) {
        Vector3 segment = new Vector3(p1).sub(p0);
        segment.scl(1f / subdivisions);
        for (int i = 0; i < subdivisions; i++) {
            Vector3 p = new Vector3(p0);
            p.x += segment.x * i;
            p.y += segment.y * i;
            points.add(p);
        }
        return this;
    }

    public Path joinBezierQuadratic(Vector3 p0, Vector3 p1, Vector3 p2, int subdivisions) {
        float t = 0f;
        do {
            Vector3 p = new Vector3();
            p.x = (1 - t) * (1 - t) * p0.x + 2 * (1 - t) * t * p1.x + t * t * p2.x;
            p.y = (1 - t) * (1 - t) * p0.y + 2 * (1 - t) * t * p1.y + t * t * p2.y;
            t += 1f / subdivisions;
            points.add(p);
        } while (t <= 1.0f);

        return this;
    }

    public void update() {
        // recalculate directions
        dirs.clear();
        for (int i = 0; i < points.size; i++) {
            Vector3 prev = points.getCyclic(i - 1);
            Vector3 current = points.get(i);
            Vector3 next = points.getCyclic(i + 1);

            Vector3 dir_prev = new Vector3(current).sub(prev).nor();
            Vector3 dir_next = new Vector3(next).sub(current).nor();
            Vector3 dir = new Vector3(dir_prev).add(dir_next).nor();
            dirs.add(dir);
        }
    }

}
