package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class Widget {

    private final Set<Region> included = new HashSet<>();
    private final Set<Region> excluded = new HashSet<>();

    public float x    = 0;
    public float y    = 0;
    public float deg  = 0;
    public float sclX = 1;
    public float sclY = 1;

    // should be either set by container or calculated.
    float boxWidth;
    float boxHeight;

    /* input handling */
    private boolean mouseInside = false;
    private boolean mouseJustEntered = false;
    private boolean mouseJustLeft = false;
    private boolean mouseRegisterClicks = false;

    // 99% cases usage
    protected Widget(@NotNull Region region) {
        included.add(region);
    }

    // 1% cases usage (for example, a button that is a hollow circle, for some reason)
    protected Widget(@NotNull Region[] toInclude, @Nullable Region[] toExclude) {
        if (toInclude == null || toInclude.length == 0) throw new WidgetsException("Must include at least 1 region in toInclude array. To create regions, you can use helper methods in the class " + Widgets.class.getSimpleName());
        included.addAll(Arrays.asList(toInclude));
        if (toExclude != null && toExclude.length != 0) excluded.addAll(Arrays.asList(toExclude));
    }

    public void frameUpdate() {
        /* apply transform */
        for (Region region : included) {
            region.applyTransform(x, y, deg, sclX, sclY);
        }
        for (Region region : excluded) {
            region.applyTransform(x, y, deg, sclX, sclY);
        }

        /* handle input */
        float xMouse = Input.mouse.getX() - Graphics.getWindowWidth() * 0.5f;
        float yMouse = Graphics.getWindowHeight() * 0.5f - Input.mouse.getY();
        float xMousePrev = Input.mouse.getXPrev() - Graphics.getWindowWidth() * 0.5f;
        float yMousePrev = Graphics.getWindowHeight() * 0.5f - Input.mouse.getYPrev();

        mouseInside = containsPoint(xMouse, yMouse);
        boolean mousePrevInside = containsPoint(xMousePrev, yMousePrev);
        mouseJustEntered = !mousePrevInside && mouseInside;
        mouseJustLeft = !mouseInside && mousePrevInside;
        if (Input.mouse.isButtonJustPressed(Mouse.Button.LEFT)) {
            mouseRegisterClicks = mouseInside;
        }
        if (mouseRegisterClicks && Input.mouse.isButtonClicked(Mouse.Button.LEFT) && mouseInside) {
            System.out.println("clicked"); // callback
        }
        if (mouseJustEntered) {
            System.out.println("entered");
        }
        if (mouseJustLeft) {
            System.out.println("left");
        }

    }

    public void fixedUpdate(float delta) {

    }

    public final boolean containsPoint(float x, float y) {
        for (Region region : excluded) {
            if (region.containsPoint(x, y)) return false;
        }
        for (Region region : included) {
            if (region.containsPoint(x, y)) return true;
        }
        return false;
    }

    public abstract void render(Renderer2D renderer2D);

    public void renderDebug(Renderer2D renderer2D) {
        /* render included regions */
        renderer2D.setColor(Color.GREEN);
        for (Region include : included) {
            renderer2D.drawPolygonThin(include.pointsTransformed.items, false,0,0,0, 1,1); // transform is already applied
        }
        renderer2D.setColor(Color.RED);
        for (Region exclude : excluded) {
            renderer2D.drawPolygonThin(exclude.pointsTransformed.items, false,0,0,0, 1,1); // transform is already applied
        }
    }

}
