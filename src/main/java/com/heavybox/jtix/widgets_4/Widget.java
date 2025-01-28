package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.Vector2;

/*
TODO: you would be able to anchor a canvas to one of the screen corners / CENTER.
TODO: this should probably be named :: Widget.java.
TODO: represents a RECTANGULAR, AXIS ALIGNED board that contains a logical group
TODO: of UI elements. For example:
    Widget: [TopBar: [button, button, ...]]
    Widget: [Tools : [button, button, ...]]
 */

/*
TODO: ECS
Canvas is just like any other entity.
There will be a canvas 2d and canvas 3d. (or rather Widget2D and Widget3D).
You will use a camera2d or camera3d to render the entire canvas.
A canvas entity will have a transform.
It will be applied to the internal elements and the polygon.
 */
public class Widget {

    public boolean debug = true;

    public  Anchor anchor  = Anchor.CENTER_CENTER; // see what anchor does
    public  int    anchorX = 50;
    public  int    anchorY = 0;
    private float  offsetX = 0; // calculated
    private float  offsetY = 0; // calculated

    private final Array<Node> nodes = new Array<>(false, 5); // a list of ROOT nodes with no parent.

    private final Vector2 center            = new Vector2();
    private final Vector2 cornerTopLeft     = new Vector2();
    private final Vector2 cornerTopRight    = new Vector2();
    private final Vector2 cornerBottomRight = new Vector2();
    private final Vector2 cornerBottomLeft  = new Vector2();

    float min_x = Float.POSITIVE_INFINITY;
    float max_x = Float.NEGATIVE_INFINITY;
    float min_y = Float.POSITIVE_INFINITY;
    float max_y = Float.NEGATIVE_INFINITY;

    public final void fixedUpdate(float delta) {
        // calculate metrics: width, height, center x, center y
        min_x = Float.POSITIVE_INFINITY;
        max_x = Float.NEGATIVE_INFINITY;
        min_y = Float.POSITIVE_INFINITY;
        max_y = Float.NEGATIVE_INFINITY;
        for (Node node : nodes) {
            node.parentWidth = Graphics.getWindowWidth();
            node.parentHeight = Graphics.getWindowHeight();
            min_x = Math.min(node.x - node.getWidth() * 0.5f, min_x);
            max_x = Math.max(node.x + node.getWidth() * 0.5f, max_x);
            min_y = Math.min(node.y - node.getHeight() * 0.5f, min_y);
            max_y = Math.max(node.y + node.getHeight() * 0.5f, max_y);
        }
        cornerTopLeft.set(min_x, max_y);
        cornerTopRight.set(max_x, max_y);
        cornerBottomRight.set(max_x, min_y);
        cornerBottomLeft.set(min_x, min_y);
        center.set(min_x + (max_x - min_x) * 0.5f, min_y + (max_y - min_y) * 0.5f);

        if (anchor == Anchor.CENTER_LEFT) {
            float screen_min_x = min_x + Graphics.getWindowWidth() * 0.5f;

            offsetX = anchorX - screen_min_x;
            for (Node node : nodes) {
                System.out.println(offsetX);

                node.parentX = offsetX;
                node.parentY = 0;
                node.parentDeg = 0;
                node.parentSclX = 1;
                node.parentSclY = 1;
            }
        }

        for (Node node : nodes) {
            node.fixedUpdate(delta);
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
//            for (Node node : nodes) {
//                renderer2D.drawPolygonThin(node.polygon.points, false,0,0,0, 1,1); // transform is already applied
//            }
//            renderer2D.drawRectangleThin(max_x - min_x, max_y - min_y, center.x + offsetX, center.y + offsetY, 0, 1, 1);
        }
    }

    public final void addNode(Node node) {
        if (nodes.contains(node,true)) throw new WidgetsException("Node " + node + " already contained in Canvas.");
        if (node.container != null) throw new WidgetsException("Node " + node + " has a parent container. Add only root elements to a Widget.");
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
