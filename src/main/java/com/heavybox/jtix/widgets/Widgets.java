package com.heavybox.jtix.widgets;

import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.memory.MemoryPool;

// TODO: add: polygons, rectangles with rounded corners, circles with refinement
public final class Widgets {

    private static final MemoryPool<Vector2> vectors2Pool = new MemoryPool<>(Vector2.class, 4);

    public static Region regionCreateRectangle(float width, float height) {
        float[] rectangle = new float[] {
          -width, -height,
           width, -height,
           width,  height,
          -width,  height,
        };
        return new Region(rectangle);
    }

    public static Region regionCreateCircle(float r, int refinement) {
        refinement = Math.max(refinement, 3);

        float[] circle = new float[refinement * 2];
        float da = 360f / refinement;
        for (int i = 0; i < refinement; i++) {
            circle[2*i]     = r * MathUtils.cosDeg(da * i);
            circle[2*i + 1] = r * MathUtils.sinDeg(da * i);
        }

        return new Region(circle);
    }

}
