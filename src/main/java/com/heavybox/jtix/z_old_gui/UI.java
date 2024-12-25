package com.heavybox.jtix.z_old_gui;


import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.memory.MemoryPool;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class UI {

    private static final MemoryPool<Vector2> vector2Pool = new MemoryPool<>(Vector2.class, 1);
    private static int uiCount = 0; // TODO: why do I need this?
    public final int id;

    protected UIContainer container;

    protected int min_x;
    protected int min_y;
    protected int max_x;
    protected int max_y;

    // common attributes
    public boolean visible = true;
    public boolean draggable = false;
    public Consumer<UI> onMouseEnter = ui -> {};
    public Consumer<UI> onMouseLeave = ui -> {};
    public BiConsumer<UI, Mouse.Button> onMouseClick = (ui, btn) -> {};
    public BiConsumer<UI, Mouse.Button> onMousePress = (ui, btn) -> {};
    public BiConsumer<UI, Mouse.Button> onMouseRelease = (ui, btn) -> {};

    public Style style = new Style();

    public String text;

    // calculated: polygon, transform, size
    private final ArrayFloat polygon = new ArrayFloat();
    float x;
    float y;
    float sclX;
    float sclY;
    float deg;
    float width;
    float height;

    protected UI() {
        this.id = uiCount;
        uiCount++;
    }

    public boolean belongsTo(UIContainer container) {
        if (container.contents.contains(this)) return true;
        boolean result = false;
        for (UI child : container.contents) {
            if (child instanceof UIContainer) {
                UIContainer childContainer = (UIContainer) child;
                result = result || belongsTo(childContainer);
            }
        }
        return result;
    }

    public float getWidth() {
        return Math.abs(max_x - min_x);
    }

    public float getHeight() {
        return Math.abs(max_y - min_y);
    }


    public void update() {
        calculateTransform();
        calculateDimensions();
        calculateBounds();
    }

    protected void render(Renderer2D renderer2D) {

    }

    private void calculateTransform() {

    }

    private void calculateDimensions() {

    }

    // TODO: update min and max.
    // TODO: consider padding.
    private void calculateBounds() {
        polygon.clear();

        float[] clipPath = style.clipPath;
        if (clipPath != null) {
            if (clipPath.length < 6) throw new GUIException("style.clipPath length should be a flat array representing the polygon positions. " + "Format: [x0,y1,  x1,y1,  x2,y2,  ...]. Must have at least 3 vertices, so clipPath.length should be greater than 6. Got: " + clipPath.length);
            if (clipPath.length % 2 != 0) throw new GUIException("style.clipPath length should be a flat array representing the polygon positions. " + "Format: [x0,y1,  x1,y1,  x2,y2,  ...]. Must have an even number of vertices. Got: " + clipPath.length);

            Vector2 vertex = vector2Pool.allocate();
            for (int i = 0; i < clipPath.length; i += 2) {
                vertex.x = clipPath[i];
                vertex.y = clipPath[i + 1];
                vertex.scl(sclX, sclY).rotateDeg(deg).add(x, y);
                polygon.add(vertex.x);
                polygon.add(vertex.y);
            }
            vector2Pool.free(vertex);

        } else {
            float borderRadiusTopLeft = Math.max(0, style.borderRadiusTopLeft);
            float borderRadiusTopRight = Math.max(0, style.borderRadiusTopRight);
            float borderRadiusBottomRight = Math.max(0, style.borderRadiusBottomRight);
            float borderRadiusBottomLeft = Math.max(0, style.borderRadiusBottomLeft);

            int borderRefinementTopLeft = Math.max(2, style.borderRefinementTopLeft);
            int borderRefinementTopRight = Math.max(2, style.borderRefinementTopRight);
            int borderRefinementBottomRight = Math.max(2, style.borderRefinementBottomRight);
            int borderRefinementBottomLeft = Math.max(2, style.borderRefinementBottomLeft);

            /* Set bounds using the corner radius and refinement. */

            float widthHalf  = width  * sclX * 0.5f;
            float heightHalf = height * sclY * 0.5f;
            float da = 90.0f / (borderRefinementTopLeft - 1);

            Vector2 corner = vector2Pool.allocate();
            // add upper left corner vertices
            for (int i = 0; i < borderRefinementTopLeft; i++) {
                corner.set(-borderRadiusTopLeft, 0);
                corner.rotateDeg(-da * i); // rotate clockwise
                corner.add(-widthHalf + borderRadiusTopLeft,heightHalf - borderRadiusTopLeft);
                corner.scl(sclX, sclY).rotateDeg(deg).add(x, y);
                polygon.add(corner.x);
                polygon.add(corner.y);
            }

            // add upper right corner vertices
            for (int i = 0; i < borderRefinementTopRight; i++) {
                corner.set(0, borderRadiusTopRight);
                corner.rotateDeg(-da * i); // rotate clockwise
                corner.add(widthHalf - borderRadiusTopRight, heightHalf - borderRadiusTopRight);
                corner.scl(sclX, sclY).rotateDeg(deg).add(x, y);
                polygon.add(corner.x);
                polygon.add(corner.y);
            }

            // add lower right corner vertices
            for (int i = 0; i < borderRefinementBottomRight; i++) {
                corner.set(borderRadiusBottomRight, 0);
                corner.rotateDeg(-da * i); // rotate clockwise
                corner.add(widthHalf - borderRadiusBottomRight, -heightHalf + borderRadiusBottomRight);
                corner.scl(sclX, sclY).rotateDeg(deg).add(x, y);
                polygon.add(corner.x);
                polygon.add(corner.y);
            }

            // add lower left corner vertices
            for (int i = 0; i < borderRefinementBottomLeft; i++) {
                corner.set(0, -borderRadiusBottomLeft);
                corner.rotateDeg(-da * i); // rotate clockwise
                corner.add(-widthHalf + borderRadiusBottomLeft, -heightHalf + borderRadiusBottomLeft);
                corner.scl(sclX, sclY).rotateDeg(deg).add(x, y);
                polygon.add(corner.x);
                polygon.add(corner.y);
            }
            vector2Pool.free(corner);
        }
    }

}

/*

// tests if the "Pointer" (mouse curse, controller marker, tap-finger etc.) is within the bounds of the UI element.
    public boolean pointerInBounds(float x, float y) {
        return MathUtils.polygonContainsPoint(bounds, x, y);
    }

    protected float[] bounds; // represent the flat [x0,y0, x1,y1, ...] polygon that bounds the UI element.

    private void updateBounds() {
        Vector2 vertex = new Vector2();
        for (int i = 0; i < bounds.length; i += 2) {
            float poly_x = bounds[i];
            float poly_y = bounds[i + 1];

            vertex.set(poly_x, poly_y);
            vertex.scl(style.sclX, style.sclY);
            vertex.rotateDeg(style.deg);
            vertex.add(style.x, style.y);
            bounds[i] = vertex.x;
            bounds[i + 1] = vertex.y;

            // update bounding box
            min_x = (int) Math.min(min_x, vertex.x);
            min_y = (int) Math.min(min_y, vertex.y);
            max_x = (int) Math.max(max_x, vertex.x);
            max_y = (int) Math.max(max_y, vertex.y);
        }
    }

 */