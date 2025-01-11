package com.heavybox.jtix.ui_3;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.z_old_gui.GUIException;

import java.util.HashSet;
import java.util.Set;

public abstract class NodeContainer extends Node {

    protected final Set<Node> children = new HashSet<>();

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        for (Node child : children) {
            child.draw(renderer2D);
        }
    }

    public abstract void setChildrenMetrics();

    @Override
    protected int getContentWidth() {
        return 0;
    }

    @Override
    protected int getContentHeight() {
        return 0;
    }

    @Override
    protected void setDefaultStyle() {
        style.backgroundEnabled = true;
        style.contentOverflowY = Style.Overflow.SCROLLBAR;
        style.contentOverflowX = Style.Overflow.SCROLLBAR;
        style.backgroudColor = Color.YELLOW;
        style.paddingRight = 10;
        style.paddingLeft = 10;
        style.paddingTop = 10;
        style.paddingBottom = 10;
    }

    public void addChild(Node node) {
        if (node == null)                   throw new GUIException(Node.class.getSimpleName() + " element cannot be null.");
        if (node == this)                   throw new GUIException("Trying to parent a " + Node.class.getSimpleName() + " to itself.");
        if (node instanceof NodeContainer) {
            if (this.descendantOf((NodeContainer) node)) throw new GUIException("Trying to create circular dependency in Widgets elements tree.");
        }

        if (node.container != null) node.container.removeChild(node);
        children.add(node);
        node.container = this;
    }

    protected void removeChild(Node node) {
        if (node.container != this) throw new GUIException(Node.class.getSimpleName() + " node is not a child of this node to detach.");
        node.container = null;
        children.remove(node);
    }

    // add vertical and horizontal scrollbars.
    protected void renderForeground(Renderer2D renderer2D) {

    }

}
