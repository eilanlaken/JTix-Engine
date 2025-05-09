package com.heavybox.jtix.zzz_planes;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.Vector3;

// TODO: move this to the math package after properly formatting.
public class Path {

    private boolean closed = false; // for this project, assume closed paths only.
    public Array<Vector3> points = new Array<>(true, 10);
    public Array<Vector3> dirs = new Array<>(true, 10);
    private boolean betweenBeginAndEnd = false;

    public Path clear() {
        points.clear();
        dirs.clear();
        return this;
    }

    public Path connect(final Path path) {
        if (path.points.isEmpty()) return this;

        if (this.points.isEmpty()) {
            this.points.addAll(path.points);
        } else if (!this.points.last().epsilonEquals(path.points.first())) {
            this.points.addAll(path.points);
        } else {
            // add everything but the first point.
            this.points.addAll(path.points, 1, path.points.size - 1);
        }
        return this;
    }

    // returns a floating point number that represents the point on the path
    // : integer part represents the segment index and the decimal part represents t.
    public float calculatePointOnPath(float t, float distance) {

        int current_s = (int) Math.floor(t);
        float current_t = t - (float) Math.floor(t);

        // calculate current segment length:
        Vector3 p_t = points.get(current_s);
        Vector3 p_tNext = points.getCyclic(current_s + 1);

        float L = Vector3.dst(p_t, p_tNext);
        if (L * current_t + distance < L) {
            return current_s + (L * current_t + distance) / L;
        }

        distance = distance - L * current_t;
        int n = current_s + 1 % points.size;
        do {

        } while (distance >= 0);

        return 0;
    }

    // TODO
    public Vector3 getPosition(float t, Vector3 out) {
        int segment = (int) Math.floor(t);
        Vector3 p0 = points.getCyclic(segment);
        Vector3 p1 = points.getCyclic(segment + 1);
        float fraction = t - (int) Math.floor(t);
        out.x = (1 - fraction) * p0.x + fraction * p1.x;
        out.y = (1 - fraction) * p0.y + fraction * p1.y;
        return out;
    }

    public Path begin() {
        if (betweenBeginAndEnd) throw new IllegalStateException("Must end() the path first.");

        points.clear();
        dirs.clear();
        betweenBeginAndEnd = true;
        return this;
    }

    public void end(boolean closed) {
        // recalculate directions
        if (!betweenBeginAndEnd) throw new IllegalStateException("Must call path.begin() before calling path.end()");

        this.closed = closed;
        if (closed) {
            if (points.first().epsilonEquals(points.last())) points.pop();
        }
        dirs.clear();
        if (closed) {
            for (int i = 0; i < points.size; i++) {
                Vector3 prev = points.getCyclic(i - 1);
                Vector3 current = points.get(i);
                Vector3 next = points.getCyclic(i + 1);

                Vector3 dir_prev = new Vector3(current).sub(prev).nor();
                Vector3 dir_next = new Vector3(next).sub(current).nor();
                Vector3 dir = new Vector3(dir_prev).add(dir_next).nor();
                dirs.add(dir);
            }
        } else {
            Vector3 dirFirst = new Vector3(points.get(1)).sub(points.get(0)).nor();
            dirs.add(dirFirst);
            for (int i = 1; i < points.size - 1; i++) {
                Vector3 prev = points.getCyclic(i - 1);
                Vector3 current = points.get(i);
                Vector3 next = points.getCyclic(i + 1);

                Vector3 dir_prev = new Vector3(current).sub(prev).nor();
                Vector3 dir_next = new Vector3(next).sub(current).nor();
                Vector3 dir = new Vector3(dir_prev).add(dir_next).nor();
                dirs.add(dir);
            }
            Vector3 dirLast = new Vector3(points.get(points.size - 1)).sub(points.get(points.size - 2)).nor();
            dirs.add(dirLast);
        }
        betweenBeginAndEnd = false;
    }

    public static Path ofBezierLinear(Vector3 p0, Vector3 p1, int subdivisions) {
        Path path = new Path();
        float t = 0f;
        do {
            Vector3 p = new Vector3();
            p.x = (1 - t) * p0.x + t * p1.x;
            p.y = (1 - t) * p0.y + t * p1.y;
            t += 1f / subdivisions;
            path.points.add(p);
        } while (t <= 1.0f);
        return path;
    }

    public static Path ofBezierQuadratic(Vector3 p0, Vector3 p1, Vector3 p2, int subdivisions) {
        Path path = new Path();
        float t = 0f;
        do {
            Vector3 p = new Vector3();
            p.x = (1 - t) * (1 - t) * p0.x + 2 * (1 - t) * t * p1.x + t * t * p2.x;
            p.y = (1 - t) * (1 - t) * p0.y + 2 * (1 - t) * t * p1.y + t * t * p2.y;
            t += 1f / subdivisions;
            path.points.add(p);
        } while (t <= 1.0f);
        return path;
    }

}
