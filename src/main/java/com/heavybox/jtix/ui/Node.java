package com.heavybox.jtix.ui;

import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.z_old_gui.GUIException;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class Node {

    public final int           id       = UI.getID();
    public final Area area = new Area();
    protected Node parent   = null;
    protected final Set<Node>   children = new HashSet<>();
    public boolean active = true;

    /* box-styling */
    protected final Style style = getDefaultStyle();

    /* calculated private attributes - computed every frame from the container, the style, etc. */
    private int   screenZIndex = 0;
    private float screenX      = 0;
    private float screenY      = 0;
    private float screenDeg    = 0;
    private float screenSclX   = 1;
    private float screenSclY   = 1;

    // this will be calculated from the style paddings etc.
    private int innerWidth = 0;
    private int innerHeight = 0;
    protected int innerOffsetX = 0;
    protected int innerOffsetY = 0;
    protected int outerWidth = 0; // TODO: change back to private.
    protected int outerHeight = 0;

    /* input handling */
    private boolean mouseInside         = false;
    private boolean mouseJustEntered    = false;
    private boolean mouseJustLeft       = false;
    private boolean mouseRegisterClicks = false;
    private boolean dragJustEntered     = false;

    /* callbacks */
    protected Node() {

    }



    public void fixedUpdate(float delta) {
        update(delta);

        /* calculate the total box width of the node */
        outerWidth = (style.width >= 0 ? style.width : getInnerWidth()); // calculate internal width based on style and component specifics
        outerWidth = MathUtils.clampInt(outerWidth, style.widthMin, style.widthMax); // clamp based on styling
        outerHeight += style.paddingLeft + style.paddingRight; // add padding.
        /* calculate the total box height of the node */
        outerHeight = (style.height >= 0 ? style.height : getInnerHeight());
        outerHeight = MathUtils.clampInt(outerHeight, style.heightMin, style.heightMax);
        outerHeight += style.paddingTop + style.paddingBottom;

        innerOffsetX = style.paddingLeft - (style.paddingLeft + style.paddingRight) / 2;
        innerOffsetY = style.paddingBottom - (style.paddingBottom + style.paddingTop) / 2;

        /* update Region or Regions (included, excluded) based on border radius, padding, clip-paths etc. */ // TODO.

        /* update screen positions */
        if (style.position == Style.Position.ABSOLUTE || parent == null) {
            this.screenZIndex = style.zIndex;
            this.screenX = style.x;
            this.screenY = style.y;
            this.screenDeg = style.deg;
            this.screenSclX = style.sclX;
            this.screenSclY = style.sclY;
        } else if (style.position == Style.Position.RELATIVE) { // TODO: wrong. In the case of containers, this ignores their effect.
            float cos = MathUtils.cosDeg(parent.screenDeg);
            float sin = MathUtils.sinDeg(parent.screenDeg);
            float offsetX = this.style.x * cos - this.style.y * sin;
            float offsetY = this.style.x * sin + this.style.y * cos;
            this.screenX = parent.screenX + offsetX * parent.screenSclX;
            this.screenY = parent.screenY + offsetY * parent.screenSclY;
            this.screenZIndex = parent.screenZIndex + style.zIndex;
            this.screenDeg  = this.style.deg + parent.screenDeg;
            this.screenSclX = this.style.sclX * parent.screenSclX;
            this.screenSclY = this.style.sclY * parent.screenSclY;
        }

        /* apply transform */
        area.applyTransform(screenX, screenY, screenDeg, screenSclX, screenSclY);

        // handle children input
        for (Node child : children) {
            child.fixedUpdate(delta);
        }
    }

    public void handleInput() { // TODO: delta will be used to detect double clicks.
        float delta = Graphics.getDeltaTime();
        /* handle input */
        float xMouse = Input.mouse.getX() - Graphics.getWindowWidth() * 0.5f;
        float yMouse = Graphics.getWindowHeight() * 0.5f - Input.mouse.getY();
        float xMousePrev = Input.mouse.getXPrev() - Graphics.getWindowWidth() * 0.5f;
        float yMousePrev = Graphics.getWindowHeight() * 0.5f - Input.mouse.getYPrev();
        mouseInside = area.containsPoint(xMouse, yMouse);
        boolean mousePrevInside = area.containsPoint(xMousePrev, yMousePrev);
        mouseJustEntered = !mousePrevInside && mouseInside;
        mouseJustLeft = !mouseInside && mousePrevInside;
        if (Input.mouse.isButtonJustPressed(Mouse.Button.LEFT)) {
            mouseRegisterClicks = mouseInside;
        }

        /* invoke event callbacks */
        // TODO
        if (mouseRegisterClicks && Input.mouse.isButtonClicked(Mouse.Button.LEFT) && mouseInside) {
            onClick();
        }
        if (mouseJustEntered) {
            onMouseOver();
        }
        if (mouseJustLeft) {
            onMouseOut();
        }

        // handle children input
        for (Node child : children) {
            child.handleInput();
        }
    }

    public void draw(Renderer2D renderer2D) {
        if (style.renderBackground) {
            renderer2D.setColor(style.backgroudColor);
            renderer2D.drawRectangleFilled(outerWidth, outerHeight,

                    style.borderRadiusTopLeft, style.borderRefinementTopLeft,
                    style.borderRadiusTopRight, style.borderRefinementTopRight,
                    style.borderRadiusBottomRight, style.borderRefinementBottomRight,
                    style.borderRadiusBottomLeft, style.borderRefinementBottomLeft,

                    screenX, screenY, screenDeg, screenSclX, screenSclY);
        }

        // TODO: apply content clipping using glScissors.
        // push pixel bounds if overflow != IGNORE
        render(renderer2D, screenX + innerOffsetX, screenY + innerOffsetY, screenDeg, screenSclX, screenSclY);
        for (Node child : children) {
            child.draw(renderer2D);
        }
        // pop pixel bounds if overflow != IGNORE

        if (UI.debug) area.draw(renderer2D);
    }

    // containers will override this.
    protected void setChildrenTransforms() {
        for (Node child : children) {
            float cos = MathUtils.cosDeg(this.screenDeg);
            float sin = MathUtils.sinDeg(this.screenDeg);
            float offsetX = child.style.x * cos - child.style.y * sin;
            float offsetY = child.style.x * sin + child.style.y * cos;
            child.screenX = this.screenX + offsetX * this.screenSclX;
            child.screenY = this.screenY + offsetY * this.screenSclY;
            child.screenZIndex = this.screenZIndex + style.zIndex;
            child.screenDeg  = child.style.deg + this.screenDeg;
            child.screenSclX = child.style.sclX * this.screenSclX;
            child.screenSclY = child.style.sclY * this.screenSclY;
        }
    }

    protected void update(float delta) {};
    protected abstract void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    protected abstract int getInnerWidth();
    protected abstract int getInnerHeight();
    protected abstract Style getDefaultStyle();

    public void addChild(Node widget) {
        if (widget == null)                   throw new GUIException(Node.class.getSimpleName() + " element cannot be null.");
        if (widget == this)                   throw new GUIException("Trying to parent a " + Node.class.getSimpleName() + " to itself.");
        if (widget.descendantOf(this)) throw new GUIException(Node.class.getSimpleName() + " element is already a descendant of parent.");
        if (this.descendantOf(widget))        throw new GUIException("Trying to create circular dependency in Widgets elements tree.");

        if (widget.parent != null) widget.parent.removeChild(widget);
        children.add(widget);
        widget.parent = this;
    }

    protected void removeChild(Node widget) {
        if (widget.parent != this) throw new GUIException(Node.class.getSimpleName() + " element is not a child of this element to detach.");
        widget.parent = null;
        children.remove(widget);
    }

    public boolean descendantOf(Node widget) {
        if (widget.children.contains(this)) return true;
        boolean result = false;
        for (Node child : widget.children) {
            result = result || descendantOf(child);
        }
        return result;
    }

    // TODO: replace with callback lambda expression attributes
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
        Node widget = (Node) o;
        return id == widget.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
