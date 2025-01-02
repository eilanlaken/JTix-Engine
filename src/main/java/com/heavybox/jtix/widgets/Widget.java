package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.z_old_gui.GUIException;
import com.heavybox.jtix.z_old_gui.UI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class Widget {

    public  final int         id       = Widgets.getID();
    private final Set<Region> included = new HashSet<>();
    private final Set<Region> excluded = new HashSet<>();
    private       Widget      parent   = null;
    private final Set<Widget> children = new HashSet<>();

    /* box-styling */
    public final Style style = new Style();

    /* global attributes - set by the programmer directly */
    public boolean hidden   = false;
    public boolean drawBg   = true;
    public boolean drawText = true;
    public String  text     = "";
    public int     zIndex   = 0;
    public float   x        = 0;
    public float   y        = 0;
    public float   deg      = 0;
    public float   sclX     = 1;
    public float   sclY     = 1;
    public float   width    = 0;
    public float   height   = 0;

    /* calculated private attributes - computed every frame from the container, the style, etc. */
    int   screenZIndex    = 0;
    float screenX         = 0;
    float screenY         = 0;
    float screenDeg       = 0;
    float screenSclX      = 1;
    float screenSclY      = 1;
    float boxWidth        = 0;
    float boxHeight       = 0;

    /* input handling */
    private boolean mouseInside         = false;
    private boolean mouseJustEntered    = false;
    private boolean mouseJustLeft       = false;
    private boolean mouseRegisterClicks = false;
    private boolean dragJustEntered     = false;

    /* callbacks */


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
        setChildrenPositions(); // only after resolving its own position, we can set the position of the children. Note: only touch the x,y,... not the screenX, screenY,... .

        /* apply transform */
        for (Region region : included) {
            region.applyTransform(screenX, screenY, screenDeg, screenSclX, screenSclY);
        }
        for (Region region : excluded) {
            region.applyTransform(screenX, screenY, screenDeg, screenSclX, screenSclY);
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

        /* invoke event callbacks */
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

    protected abstract void render(Renderer2D renderer2D);

    public void draw(Renderer2D renderer2D) {
        // we probably don't need this.
        // we can also remove useless bg attributes like border radius etc.
        if (drawBg) {

        }
        if (drawText) {

        }

        if (style.overflow == Style.Overflow.TRIM) renderer2D.pushPixelBounds(0, 0, 1800, 1800); // TODO: calc min and max.
        render(renderer2D);
        for (Widget widget : children) {
            widget.render(renderer2D);
        }
        if (style.overflow == Style.Overflow.TRIM) renderer2D.popPixelBounds();
    }

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

    public void insert(Widget widget) {
        if (widget == null)                throw new GUIException(Widget.class.getSimpleName() + " element cannot be null.");
        if (widget == this)                throw new GUIException("Trying to parent a " + Widget.class.getSimpleName() + " to itself.");
        if (widget.belongsTo(this)) throw new GUIException(Widget.class.getSimpleName() + " element is already a descendant of parent.");
        if (this.belongsTo(widget))        throw new GUIException("Trying to create circular dependency in Widgets elements tree.");

        if (widget.parent != null) widget.parent.remove(widget);
        children.add(widget);
        widget.parent = this;
    }

    protected void setChildrenPositions() {

    }

    protected void remove(Widget widget) {
        if (widget.parent != this) throw new GUIException(Widget.class.getSimpleName() + " element is not a child of this element to detach.");
        widget.parent = null;
        children.remove(widget);
    }

    public boolean belongsTo(Widget widget) {
        if (widget.children.contains(this)) return true;
        boolean result = false;
        for (Widget child : widget.children) {
            result = result || belongsTo(child);
        }
        return result;
    }

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
