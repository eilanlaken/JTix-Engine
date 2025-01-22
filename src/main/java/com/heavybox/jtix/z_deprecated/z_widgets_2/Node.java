package com.heavybox.jtix.z_deprecated.z_widgets_2;

import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;

import java.util.Objects;

public abstract class Node {

    protected final int    id = Widgets.getID();
    protected final Region region = new Region();
    protected NodeContainer container = null;


    private boolean mouseRegisterClicks = false;
    private boolean dragJustEntered     = false;

    public Transform transform       = Transform.RELATIVE;
    public int       transformZIndex = 0;
    public float     transformX      = 0;
    public float     transformY      = 0;
    public float     transformDeg    = 0;
    public float     transformSclX   = 1;
    public float     transformSclY   = 1;

    /* calculated */
    public float boxWidth = 0;
    public float boxHeight = 0;
    public float boxX = 0;
    public float boxY = 0;
    public float boxDeg = 0;
    public float boxSclX = 1;
    public float boxSclY = 1;


    /* callbacks */


    public void handleInput() { // TODO: delta will be used to detect double clicks.
        float delta = Graphics.getDeltaTime();
        /* handle input */
        float xMouse = Input.mouse.getX() - Graphics.getWindowWidth() * 0.5f;
        float yMouse = Graphics.getWindowHeight() * 0.5f - Input.mouse.getY();
        float xMousePrev = Input.mouse.getXPrev() - Graphics.getWindowWidth() * 0.5f;
        float yMousePrev = Graphics.getWindowHeight() * 0.5f - Input.mouse.getYPrev();
        /* input handling */
        boolean mouseInside = region.containsPoint(xMouse, yMouse);
        boolean mousePrevInside = region.containsPoint(xMousePrev, yMousePrev);
        boolean mouseJustEntered = !mousePrevInside && mouseInside;
        boolean mouseJustLeft = !mouseInside && mousePrevInside;
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


    }

    final void draw(Renderer2D renderer2D) {
        render(renderer2D, boxX, boxY, boxDeg, boxSclX, boxSclY);
    }


    protected void fixedUpdate(float delta) {}
    protected abstract void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    protected abstract float getWidth();
    protected abstract float getHeight();

    public boolean descendantOf(NodeContainer container) {
        if (container.children.contains(this, true)) return true;
        boolean result = false;
        for (Node child : container.children) {
            if (child instanceof NodeContainer) {
                result = result || descendantOf((NodeContainer) child);
            }
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

    /* controls how the final transform of the widget is calculated. */
    public enum Transform {
        ABSOLUTE,  // positioned x, y, deg, sclX, sclY from the container's center (or window, if container is null).
        RELATIVE,  // positioned x, y, deg, sclX, sclY relative to the position calculated by its container. If the container is null, behaves like ABSOLUTE.
        AUTO, // calculated by container
    }

    public enum Sizing {
        ABSOLUTE, // explicitly set by width and height
        RELATIVE, // relative to the container's calculated dimensions
        AUTO, // conforms to fit content
    }
}
