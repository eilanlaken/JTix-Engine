package com.heavybox.jtix.widgets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.z_old_gui.GUIException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class Widget {

    public    final int           id       = Widgets.getID();
    protected final Array<Region> included = new Array<>(false, 1);
    protected final Array<Region> excluded = new Array<>(false, 1);
    protected        Widget       parent   = null;
    protected final Set<Widget>   children = new HashSet<>();

    /* box-styling */
    public final Style style = new Style();

    /* global attributes - set by the programmer directly */
    public boolean active = false;
    public int     zIndex   = 0;
    public float   x        = 0;
    public float   y        = 0;
    public float   deg      = 0;
    public float   sclX     = 1;
    public float   sclY     = 1;
    public float   width    = 0;
    public float   height   = 0;

    /* calculated private attributes - computed every frame from the container, the style, etc. */
    private int   screenZIndex = 0;
    private float screenX      = 0;
    private float screenY      = 0;
    private float screenDeg    = 0;
    private float screenSclX   = 1;
    private float screenSclY   = 1;
    private float boxWidth     = 0;
    private float boxHeight    = 0;

    /* input handling */
    private boolean mouseInside         = false;
    private boolean mouseJustEntered    = false;
    private boolean mouseJustLeft       = false;
    private boolean mouseRegisterClicks = false;
    private boolean dragJustEntered     = false;

    /* callbacks */
    protected Widget() {

    }

    // 99% cases usage
    protected Widget(@NotNull Region region) {
        included.add(region);
    }

    // 1% cases usage (for example, a button that is a hollow circle, for some reason)
    protected Widget(@NotNull Region[] toInclude, @Nullable Region[] toExclude) {
        if (toInclude == null || toInclude.length == 0) throw new WidgetsException("Must include at least 1 region in toInclude array. To create regions, you can use helper methods in the class " + Widgets.class.getSimpleName());
        included.addAll(toInclude);
        if (toExclude != null && toExclude.length != 0) excluded.addAll(toExclude);
    }

    public void frameUpdate(float delta) { // will be used to detect double clicks. TODO.
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

        /* invoke event callbacks */
        if (mouseRegisterClicks && Input.mouse.isButtonClicked(Mouse.Button.LEFT) && mouseInside) {
            onClick();
        }
        if (mouseJustEntered) {
            onMouseOver();
        }
        if (mouseJustLeft) {
            onMouseOut();
        }
    }

    public void fixedUpdate(float delta) {
        update(delta);

        /* update Region or Regions (included, excluded) based on border radius, padding, clip-paths etc. */ // TODO.

        /* update screen positions */
        if (style.position == Style.Position.ABSOLUTE || parent == null) {
            this.screenZIndex = zIndex;
            this.screenX = x;
            this.screenY = y;
            this.screenDeg = deg;
            this.screenSclX = sclX;
            this.screenSclY = sclY;
        } else if (style.position == Style.Position.RELATIVE) {
            float cos = MathUtils.cosDeg(parent.screenDeg);
            float sin = MathUtils.sinDeg(parent.screenDeg);
            float offsetX = this.x * cos - this.y * sin;
            float offsetY = this.x * sin + this.y * cos;
            this.screenX = parent.screenX + offsetX * parent.screenSclX;
            this.screenY = parent.screenY + offsetY * parent.screenSclY;
            this.screenZIndex = parent.screenZIndex + zIndex;
            this.screenDeg  = this.deg + parent.screenDeg;
            this.screenSclX = this.sclX * parent.screenSclX;
            this.screenSclY = this.sclY * parent.screenSclY;
        }

        /* apply transform */
        for (Region region : included) {
            region.applyTransform(screenX, screenY, screenDeg, screenSclX, screenSclY);
        }
        for (Region region : excluded) {
            region.applyTransform(screenX, screenY, screenDeg, screenSclX, screenSclY);
        }
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

    public void draw(Renderer2D renderer2D) {
        render(renderer2D, screenX, screenY, screenDeg, screenSclX, screenSclY);
        if (Widgets.debug) {
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

    protected abstract void update(float delta);
    protected abstract void render(Renderer2D renderer2D, float screenX, float screenY, float screenDeg, float screenSclX, float screenSclY);

    public void addChild(Widget widget) {
        if (widget == null)                   throw new GUIException(Widget.class.getSimpleName() + " element cannot be null.");
        if (widget == this)                   throw new GUIException("Trying to parent a " + Widget.class.getSimpleName() + " to itself.");
        if (widget.descendantOf(this)) throw new GUIException(Widget.class.getSimpleName() + " element is already a descendant of parent.");
        if (this.descendantOf(widget))        throw new GUIException("Trying to create circular dependency in Widgets elements tree.");

        if (widget.parent != null) widget.parent.removeChild(widget);
        children.add(widget);
        widget.parent = this;
    }

    protected void removeChild(Widget widget) {
        if (widget.parent != this) throw new GUIException(Widget.class.getSimpleName() + " element is not a child of this element to detach.");
        widget.parent = null;
        children.remove(widget);
    }

    public boolean descendantOf(Widget widget) {
        if (widget.children.contains(this)) return true;
        boolean result = false;
        for (Widget child : widget.children) {
            result = result || descendantOf(child);
        }
        return result;
    }

    /* Triggered when the element is clicked. */
    protected void onClick() {}
    /* Triggered when the mouse button is pressed down. */
    protected void onMouseDown() {}
    /* Triggered when the mouse button is released. */
    protected void onMouseUp() {}
    /* Triggered when the mouse hovers over the element. */
    protected void onMouseOver() {}
    /* Triggered when the mouse moves out of the element. */
    protected void onMouseOut() {}
    /* Triggered when the mouse moves over the element. */
    protected void onMouseMove() {}

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " id: " + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Widget widget = (Widget) o;
        return id == widget.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
