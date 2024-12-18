package com.heavybox.jtix.ui;


import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input_2.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class UI {

    private static int uiCount = 0; // TODO: why do I need this?

    protected UIContainer container;
    public final int id;
    public float x, y, deg, sclX, sclY;
    protected float[] bounds; // represent the flat [x0,y0, x1,y1, ...] polygon that bounds the UI element.

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

    protected UI(float x, float y, float deg, float sclX, float sclY, float[] bounds) {
        this.id = uiCount;
        this.x = x;
        this.y = y;
        this.deg = deg;
        this.sclX = sclX;
        this.sclY = sclY;


        this.bounds = bounds;
        updateBounds();
        uiCount++;
    }

    // tests if the "Pointer" (mouse curse, controller marker, tap-finger etc.) is within the bounds of the UI element.
    public boolean pointerInBounds(float x, float y) {
        return MathUtils.polygonContainsPoint(bounds, x, y);
    }

    private void updateBounds() {
        Vector2 vertex = new Vector2();
        for (int i = 0; i < bounds.length; i += 2) {
            float poly_x = bounds[i];
            float poly_y = bounds[i + 1];

            vertex.set(poly_x, poly_y);
            vertex.scl(sclX, sclY);
            vertex.rotateDeg(deg);
            vertex.add(x, y);
            bounds[i] = vertex.x;
            bounds[i + 1] = vertex.y;

            // update bounding box
            min_x = (int) Math.min(min_x, vertex.x);
            min_y = (int) Math.min(min_y, vertex.y);
            max_x = (int) Math.max(max_x, vertex.x);
            max_y = (int) Math.max(max_y, vertex.y);
        }
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

    private void updateInternal() {
        // TODO ...
        update();
    }

    public void update() {}

    public abstract void render(Renderer2D renderer2D);



}
