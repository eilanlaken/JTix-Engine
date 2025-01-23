package com.heavybox.jtix.widgets_3;

import com.heavybox.jtix.graphics.Renderer2D;

import java.util.Objects;

public abstract class Node {

    protected final int id = Widgets.getID();

    public Transform transform       = Transform.RELATIVE;
    public int       transformZIndex = 0;
    public float     transformX      = 0;
    public float     transformY      = 0;
    public float     transformDeg    = 0;
    public float     transformSclX   = 1;
    public float     transformSclY   = 1;

    final Polygon polygon = new Polygon();

    /* calculated by container and Sizing */
    public float boxWidth = 0;
    public float boxHeight = 0;

    /* calculated by container and Transform */
    public float boxX = 0;
    public float boxY = 0;
    public float boxDeg = 0;
    public float boxSclX = 1;
    public float boxSclY = 1;

    protected void fixedUpdate(float delta) {}
    protected final void draw(Renderer2D renderer2D) { render(renderer2D, boxX, boxY, boxDeg, boxSclX, boxSclY); }
    protected abstract void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    protected abstract float getWidth();
    protected abstract float getHeight();

    protected final void updatePolygon() {
        polygon.reset();
        updatePolygon(polygon);
    };

    // kind of a default implementation
    protected void updatePolygon(final Polygon polygon) {
        polygon.setToRectangle(getWidth(), getHeight());
    }

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

    // TODO: probably belongs in container.
    public enum Sizing {
        ABSOLUTE, // explicitly set by width and height
        RELATIVE, // relative to the container's calculated dimensions
        AUTO, // conforms to fit content
    }
}
