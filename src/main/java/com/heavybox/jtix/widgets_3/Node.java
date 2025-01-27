package com.heavybox.jtix.widgets_3;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;

import java.util.Objects;

public abstract class Node {

    protected final int id = Widgets.getID();
    final Polygon polygon = new Polygon();

    Node        parent   = null;
    Array<Node> children = new Array<>(false, 5);

    /* can be explicitly set by user */
    public int   zIndex = 0;
    public float x      = 0;
    public float y      = 0;
    public float deg    = 0;
    public float sclX   = 1;
    public float sclY   = 1;

    /* set by parent / parent-container */
    public int   parentZIndex = 0;
    public float parentX      = 0;
    public float parentY      = 0;
    public float parentDeg    = 0;
    public float parentSclX   = 1;
    public float parentSclY   = 1;

    /* calculated by container and Transform */
    public int   screenZIndex = 0;
    public float screenX    = 0;
    public float screenY    = 0;
    public float screenDeg  = 0;
    public float screenSclX = 1;
    public float screenSclY = 1;

    protected void fixedUpdate(float delta) {}
    protected final void draw(Renderer2D renderer2D) { render(renderer2D, screenX, screenY, screenDeg, screenSclX, screenSclY); }
    protected abstract void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    protected abstract float getContentWidth();
    protected abstract float getContentHeight();
    final void updatePolygon() { updatePolygon(polygon); }

    final void transform() {
        float cos = MathUtils.cosDeg(parentDeg);
        float sin = MathUtils.sinDeg(parentDeg);
        float x = this.x * cos - this.y * sin;
        float y = this.x * sin + this.y * cos;
        screenZIndex = parentZIndex + this.zIndex;
        screenX = parentX + x * parentSclX;
        screenY = parentY + y * parentSclY;
        screenDeg  = this.deg + parentDeg;
        screenSclX = this.sclX * parentSclX;
        screenSclY = this.sclY * parentSclY;

        polygon.applyTransform(screenX, screenY, screenDeg, screenSclX, screenSclY);
    }

    // kind of a default implementation
    protected void updatePolygon(final Polygon polygon) {
        polygon.setToRectangle(getContentWidth(), getContentHeight());
    }

    public void addChild(Node child) {
        if (child == null)            throw new WidgetsException(Node.class.getSimpleName() + " element cannot be null.");
        if (child == this)            throw new WidgetsException("Trying to parent a " + Node.class.getSimpleName() + " to itself.");
        if (this.descendantOf(child)) throw new WidgetsException("Trying to create circular dependency in Widgets elements tree.");
        if (child.parent != null) child.parent.removeChild(child);
        children.add(child);
        child.parent = this;
    }

    protected void removeChild(Node node) {
        if (node.parent != this) throw new WidgetsException(Node.class.getSimpleName() + " node is not a child of this node to detach.");
        node.parent = null;
        children.removeValue(node, true);
    }

    public boolean descendantOf(Node node) {
        if (node.children.contains(this, true)) return true;
        boolean result = false;
        for (Node child : node.children) {
            result = result || descendantOf(child);
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
        Node widget = (Node) o;
        return id == widget.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // TODO: probably belongs in container.
    public enum Sizing {
        ABSOLUTE, // explicitly set by width and height
        RELATIVE, // relative to the container's calculated dimensions
        AUTO, // conforms to fit content
    }
}
