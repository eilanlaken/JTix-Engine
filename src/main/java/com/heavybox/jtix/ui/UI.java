package com.heavybox.jtix.ui;


import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Renderer2D_3;
import com.heavybox.jtix.input_2.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class UI {

    private static int uiCount = 0;

    protected UIContainer parent;
    public final int id;
    public float x, y, deg, sclX, sclY;
    public float global_x, global_y, global_deg, global_sclX, global_sclY;
    protected float[] bounds; // represent the flat [x0,y0, x1,y1, ...] polygon that bounds the UI element.

    protected float[] boundsTransformed;

    // global attributes
    public boolean visible = true;
    public Consumer<UI> onMouseEnter = ui -> {};
    public Consumer<UI> onMouseLeave = ui -> {};
    public BiConsumer<UI, Mouse.Button> onMouseClick = (ui, btn) -> {};
    public BiConsumer<UI, Mouse.Button> onMousePress = (ui, btn) -> {};
    public BiConsumer<UI, Mouse.Button> onMouseRelease = (ui, btn) -> {};

    // style
    public Color styleColorBackground;
    public Color styleColorText;
    public Font  styleFont;
    public float stylePadding;
    public float styleMargin;

    protected UI(float x, float y, float deg, float sclX, float sclY, float[] bounds) {
        this.id = uiCount;
        this.x = x;
        this.y = y;
        this.deg = deg;
        this.sclX = sclX;
        this.sclY = sclY;

        this.global_x = x;
        this.global_y = y;
        this.global_deg = deg;
        this.global_sclX = sclX;
        this.global_sclY = sclY;

        this.bounds = bounds;
        this.boundsTransformed = new float[bounds.length];
        updateBoundsTransformed();

        uiCount++;
    }

    // tests if the "Pointer" (mouse curse, controller marker, tap-finger etc.) is within the bounds of the UI element.
    public boolean pointerInBounds(float x, float y) {
        return MathUtils.polygonContainsPoint(boundsTransformed, x, y);
    }

    private void updateBoundsTransformed() {
        Vector2 vertex = new Vector2();
        for (int i = 0; i < bounds.length; i += 2) {
            float poly_x = bounds[i];
            float poly_y = bounds[i + 1];

            vertex.set(poly_x, poly_y);
            vertex.scl(sclX, sclY);
            vertex.rotateDeg(deg);
            vertex.add(x, y);
            boundsTransformed[i] = vertex.x;
            boundsTransformed[i + 1] = vertex.y;
        }
    }

    public boolean belongsTo(UIContainer container) {
        if (container.children.contains(this)) return true;
        boolean result = false;
        for (UI child : container.children) {
            if (child instanceof UIContainer) {
                UIContainer childContainer = (UIContainer) child;
                result = result || belongsTo(childContainer);
            }
        }
        return result;
    }

    private void updateInternal() {

        update();
    }

    public void update() {}

    public abstract void render(Renderer2D_3 renderer2D);



}
