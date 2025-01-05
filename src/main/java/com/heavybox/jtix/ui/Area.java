package com.heavybox.jtix.ui;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.memory.MemoryPool;

public final class Area {

    final Array<Region> regionsIn  = new Array<>(false, 1);
    final Array<Region> regionsOut = new Array<>(false, 1);

    public boolean containsPoint(float x, float y) {
        for (Region region : regionsOut) {
            if (region.containsPoint(x, y)) return false;
        }
        for (Region region : regionsIn) {
            if (region.containsPoint(x, y)) return true;
        }
        return false;
    }

    public final void applyTransform(float x, float y, float deg, float sclX, float sclY) {
        for (Region region : regionsIn) {
            region.applyTransform(x, y, deg, sclX, sclY);
        }
        for (Region region : regionsOut) {
            region.applyTransform(x, y, deg, sclX, sclY);
        }
    }

    public void draw(Renderer2D renderer2D) {
        renderer2D.setColor(Color.GREEN);
        for (Region include : regionsIn) {
            renderer2D.drawPolygonThin(include.pointsTransformed.items, false,0,0,0, 1,1); // transform is already applied
        }
        renderer2D.setColor(Color.RED);
        for (Region exclude : regionsOut) {
            renderer2D.drawPolygonThin(exclude.pointsTransformed.items, false,0,0,0, 1,1); // transform is already applied
        }
    }

    /*
      Represents a polygonal region.
    */
    public static final class Region implements MemoryPool.Reset {

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
}
