package com.heavybox.jtix.widgets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

public abstract class NodeContainer extends Node {

    protected final Array<Node> children = new Array<>(true,5);

    public float childrenMargins = 10;

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        for (Node child : children) {
            child.draw(renderer2D);
        }
    }

    @Override
    public void fixedUpdate(float delta) {
        setChildrenBoxes();
        for (Node child : children) {
            child.update(delta);
        }
    }

    @Override
    public void handleInput() {
        super.handleInput();
        for (Node child : children) {
            child.handleInput();
        }
    }

    public abstract void setChildrenBoxes();

    @Override
    protected void setDefaultStyle() {
        style.boxBackgroundEnabled = true;
        style.contentOverflowY = Style.Overflow.SCROLLBAR;
        style.contentOverflowX = Style.Overflow.SCROLLBAR;
        style.boxBackgroudColor = Color.YELLOW;
        style.boxPaddingRight = 10;
        style.boxPaddingLeft = 10;
        style.boxPaddingTop = 10;
        style.boxPaddingBottom = 10;
    }

    public void addChild(Node node) {
        if (node == null)                   throw new WidgetsException(Node.class.getSimpleName() + " element cannot be null.");
        if (node == this)                   throw new WidgetsException("Trying to parent a " + Node.class.getSimpleName() + " to itself.");
        if (node instanceof NodeContainer) {
            if (this.descendantOf((NodeContainer) node)) throw new WidgetsException("Trying to create circular dependency in Widgets elements tree.");
        }

        if (node.container != null) node.container.removeChild(node);
        children.add(node);
        node.container = this;
    }

    protected void removeChild(Node node) {
        if (node.container != this) throw new WidgetsException(Node.class.getSimpleName() + " node is not a child of this node to detach.");
        node.container = null;
        children.removeValue(node, true);
    }

}
