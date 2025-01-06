package com.heavybox.jtix.ui;

import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.memory.MemoryPool;

// TODO: add: polygons, rectangles with rounded corners, circles with refinement
public final class UI {

    private static int widgetsCount = 0;
    public  static boolean debug = true;

    private static final MemoryPool<Vector2> vectors2Pool = new MemoryPool<>(Vector2.class, 4);

    public static Region.Polygon regionCreateRectangle(float width, float height) {
        float[] rectangle = new float[] {
          -width, -height,
           width, -height,
           width,  height,
          -width,  height,
        };
        return new Region.Polygon(rectangle);
    }

    public static void regionSetToRectangle(float width, float height, Region.Polygon out) {
        out.pointsOriginal.clear();
        out.pointsTransformed.clear();

        out.pointsOriginal.add(-width, -height);
        out.pointsOriginal.add( width, -height);
        out.pointsOriginal.add( width,  height);
        out.pointsOriginal.add(-width,  height);

        out.pointsOriginal.pack();
        out.pointsTransformed.addAll(out.pointsOriginal);
        out.pointsTransformed.pack();
    }

    public static Region.Polygon regionCreateRectangle(float width, float height, float cornerRadius, int refinement) {
        refinement = Math.max(2, refinement);

        float widthHalf  = width  * 0.5f;
        float heightHalf = height * 0.5f;
        float da = 90.0f / (refinement - 1);
        float[] rectangleRC = new float[refinement * 4 * 2]; // round corners rectangle: 4 corners, 2 components for each vertex (x, y) and 'refinement' vertices for every corner

        Vector2 corner = vectors2Pool.allocate();
        int index = 0;
        // add upper left corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(-cornerRadius, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadius, heightHalf - cornerRadius);
            rectangleRC[index] = corner.x;
            rectangleRC[index + 1] = corner.y;
            index += 2;
        }

        // add upper right corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(0, cornerRadius);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - cornerRadius, heightHalf - cornerRadius);
            rectangleRC[index] = corner.x;
            rectangleRC[index + 1] = corner.y;
            index += 2;
        }

        // add lower right corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(cornerRadius, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - cornerRadius, -heightHalf + cornerRadius);
            rectangleRC[index] = corner.x;
            rectangleRC[index + 1] = corner.y;
            index += 2;
        }

        // add lower left corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(0, -cornerRadius);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadius, -heightHalf + cornerRadius);
            rectangleRC[index] = corner.x;
            rectangleRC[index + 1] = corner.y;
            index += 2;
        }

        vectors2Pool.free(corner);
        return new Region.Polygon(rectangleRC);
    }

    public static Region.Polygon regionCreateRectangle(float width, float height,
                                                       float cornerRadiusTopLeft, int refinementTopLeft,
                                                       float cornerRadiusTopRight, int refinementTopRight,
                                                       float cornerRadiusBottomRight, int refinementBottomRight,
                                                       float cornerRadiusBottomLeft, int refinementBottomLeft) {

        refinementTopLeft = Math.max(2, refinementTopLeft);
        refinementTopRight = Math.max(2, refinementTopRight);
        refinementBottomRight = Math.max(2, refinementBottomRight);
        refinementBottomLeft = Math.max(2, refinementBottomLeft);

        int totalRefinement = refinementTopLeft + refinementTopRight
                + refinementBottomRight + refinementBottomLeft;

        float widthHalf  = width  * 0.5f;
        float heightHalf = height * 0.5f;
        float da = 90.0f / (refinementTopLeft - 1);
        float[] rectangleRC = new float[totalRefinement * 2]; // round corners rectangle: 4 corners, 2 components for each vertex (x, y) and 'refinement' vertices for every corner

        Vector2 corner = vectors2Pool.allocate();
        int index = 0;
        // add upper left corner vertices
        for (int i = 0; i < refinementTopLeft; i++) {
            corner.set(-cornerRadiusTopLeft, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadiusTopLeft,heightHalf - cornerRadiusTopLeft);
            rectangleRC[index] = corner.x;
            rectangleRC[index + 1] = corner.y;
            index += 2;
        }

        // add upper right corner vertices
        for (int i = 0; i < refinementTopRight; i++) {
            corner.set(0, cornerRadiusTopRight);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - cornerRadiusTopRight, heightHalf - cornerRadiusTopRight);
            rectangleRC[index] = corner.x;
            rectangleRC[index + 1] = corner.y;
            index += 2;
        }

        // add lower right corner vertices
        for (int i = 0; i < refinementBottomRight; i++) {
            corner.set(cornerRadiusBottomRight, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - cornerRadiusBottomRight, -heightHalf + cornerRadiusBottomRight);
            rectangleRC[index] = corner.x;
            rectangleRC[index + 1] = corner.y;
            index += 2;
        }

        // add lower left corner vertices
        for (int i = 0; i < refinementBottomLeft; i++) {
            corner.set(0, -cornerRadiusBottomLeft);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadiusBottomLeft, -heightHalf + cornerRadiusBottomLeft);
            rectangleRC[index] = corner.x;
            rectangleRC[index + 1] = corner.y;
            index += 2;
        }

        vectors2Pool.free(corner);
        return new Region.Polygon(rectangleRC);
    }

    public static Region.Polygon regionCreateCircle(float r, int refinement) {
        refinement = Math.max(refinement, 3);

        float[] circle = new float[refinement * 2];
        float da = 360f / refinement;
        for (int i = 0; i < refinement; i++) {
            circle[2*i]     = r * MathUtils.cosDeg(da * i);
            circle[2*i + 1] = r * MathUtils.sinDeg(da * i);
        }

        return new Region.Polygon(circle);
    }



    public static int getID() {
        widgetsCount++;
        return widgetsCount - 1;
    }

}
