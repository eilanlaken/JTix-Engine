package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.memory.MemoryPool;

public final class Region {

    final Array<Polygon> polygonsIn  = new Array<>(false, 1);
    final Array<Polygon> polygonsOut = new Array<>(false, 1);

    public boolean containsPoint(float x, float y) {
        for (Polygon polygon : polygonsOut) {
            if (polygon.containsPoint(x, y)) return false;
        }
        for (Polygon polygon : polygonsIn) {
            if (polygon.containsPoint(x, y)) return true;
        }
        return false;
    }

    public void applyTransform(float x, float y, float deg, float sclX, float sclY) {
        for (Polygon polygon : polygonsIn) {
            polygon.applyTransform(x, y, deg, sclX, sclY);
        }
        for (Polygon polygon : polygonsOut) {
            polygon.applyTransform(x, y, deg, sclX, sclY);
        }
    }

    public void draw(Renderer2D renderer2D) {
        renderer2D.setColor(Color.GREEN);
        for (Polygon include : polygonsIn) {
            renderer2D.drawPolygonThin(include.pointsTransformed.items, false,0,0,0, 1,1); // transform is already applied
        }
        renderer2D.setColor(Color.RED);
        for (Polygon exclude : polygonsOut) {
            renderer2D.drawPolygonThin(exclude.pointsTransformed.items, false,0,0,0, 1,1); // transform is already applied
        }
    }

    /*
      Represents a polygonal region.
    */
    public static final class Polygon implements MemoryPool.Reset {

        final ArrayFloat pointsOriginal    = new ArrayFloat(true, 8);
        final ArrayFloat pointsTransformed = new ArrayFloat(true, 8);

        Polygon(float[] points) {
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
