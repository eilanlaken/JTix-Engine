package com.heavybox.jtix.widgets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Renderer2D;

public class Canvas {

    private final Array<Node> nodes = new Array<>(false, 5);

    public final void fixedUpdate(float delta) {

    }

    public final void frameUpdate(float delta) {

    }

    public final void render(Renderer2D renderer2D) {

    }

    public void addNode(Node node) {
        if (nodes.contains(node, true)) throw new WidgetsException("Node " + node + " already contained in Canvas.");
        nodes.add(node);
        node.setCanvas(this);
    }

    public void removeNode(Node node) {
        node.setCanvas(null);
        nodes.removeValue(node, true);
    }

}
