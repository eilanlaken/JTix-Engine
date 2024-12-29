package com.heavybox.jtix.widgets;

// TODO: add: polygons, rectangles with rounded corners, circles with refinement
public final class Widgets {

    public static Region regionCreateRectangle(float width, float height) {
        float[] rectangle = new float[] {
          -width, -height,
           width, -height,
           width,  height,
          -width,  height,
        };
        return new Region(rectangle);
    }

}
