package com.heavybox.jtix.widgets_3;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Renderer2D;
import org.jetbrains.annotations.NotNull;

/*
TODO: you would be able to anchor a canvas to one of the screen corners / CENTER.
TODO: this should probably be named :: Widget.java.
TODO: represents a RECTANGULAR, AXIS ALIGNED board that contains a logical group
TODO: of UI elements. For example:
    Widget: [TopBar: [button, button, ...]]
    Widget: [Tools : [button, button, ...]]
 */
public class Canvas {

    public boolean debug = true;

    public  Anchor anchor    = Anchor.CENTER_CENTER; // see what anchor does
    public  int    positionX = 0;
    public  int    positionY = 0;
    private float  offsetX   = 0; // calculated
    private float  offsetY   = 0; // calculated

    private final Array<Node> nodes = new Array<>(false, 5); // a list of ROOT nodes with no parent.

    public final void fixedUpdate(float delta) {
        // calculate metrics: width, height, center x, center y
        float min_x = Float.POSITIVE_INFINITY;
        float max_x = Float.NEGATIVE_INFINITY;
        float min_y = Float.POSITIVE_INFINITY;
        float max_y = Float.NEGATIVE_INFINITY;
        for (Node node : nodes) {
            min_x = Math.min(node.x - node.getWidth(), min_x);
            max_x = Math.max(node.x + node.getWidth(), max_x);
            min_y = Math.min(node.y - node.getHeight(), min_y);
            max_y = Math.max(node.y + node.getHeight(), max_y);
        }




    }

    // frame update
    public final void handleInput(float delta) {
        for (Node node : nodes) {
            node.updatePolygon();
            node.transform();
        }
    }

    // frame update
    public final void draw(Renderer2D renderer2D) {
        for (Node node : nodes) {
            node.draw(renderer2D);
        }
        if (debug) {
            for (Node node : nodes) {
                renderer2D.drawPolygonThin(node.polygon.points, false,0,0,0, 1,1); // transform is already applied
            }
        }
    }

    public final void addNode(Node node) {
        if (nodes.contains(node, true)) throw new WidgetsException("Node " + node + " already contained in Canvas.");
        if (node.parent != null) throw new WidgetsException("Only root nodes (nodes without a parent) can be added to " + Canvas.class.getSimpleName());
        nodes.add(node);
    }

    public final void removeNode(Node node) {
        nodes.removeValue(node,true);
    }

    // anchors ensure spacing between each node and screen pivots are constant.
    // this is important to make the ui responsive.
    public enum Anchor {
        TOP_LEFT,    TOP_CENTER,    TOP_RIGHT,
        CENTER_LEFT, CENTER_CENTER, CENTER_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT,
    }

}
