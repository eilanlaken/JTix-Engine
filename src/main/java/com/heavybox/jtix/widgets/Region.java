package com.heavybox.jtix.widgets;

import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.memory.MemoryPool;

/*
Represents
 */
public class Region implements MemoryPool.Reset {

    final ArrayFloat pointsOriginal    = new ArrayFloat(true, 8);
    final ArrayFloat pointsTransformed = new ArrayFloat(true, 8);

    Region(float[] points) {
        MathUtils.polygonRemoveDegenerateVertices(points, this.pointsOriginal);
        pointsOriginal.pack();
        pointsTransformed.addAll(pointsOriginal);
        pointsTransformed.pack();
    }

    void applyTransform(float x, float y, float deg, float sclX, float sclY) {
        /* apply transform */
        Vector2 point = new Vector2();
        for (int i = 0; i < pointsOriginal.size - 1; i += 2) {
            float point_x = pointsOriginal.get(i);
            float point_y = pointsOriginal.get(i + 1);
            point.x = point_x;
            point.y = point_y;
            point.scl(sclX, sclY).rotateDeg(deg).add(x, y);
            pointsTransformed.set(i, point.x);
            pointsTransformed.set(i + 1, point.y);
        }
    }

    public boolean containsPoint(float x, float y) {
        return MathUtils.polygonContainsPoint(pointsTransformed.items, x, y);
    }

    @Override
    public void reset() {
        pointsOriginal.clear();
        pointsTransformed.clear();
    }

}
