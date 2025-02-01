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

    public Anchor anchor  = Anchor.CENTER_CENTER; // see what anchor does
    public float  anchorX = 50;
    public float  anchorY = 100;

    private final Array<Node> nodes = new Array<>(false, 5); // a list of ROOT nodes with no parent.

    public final void update(float delta) {
        // calculate bounds
        float min_x = Float.POSITIVE_INFINITY;
        float max_x = Float.NEGATIVE_INFINITY;
        float min_y = Float.POSITIVE_INFINITY;
        float max_y = Float.NEGATIVE_INFINITY;
        for (Node node : nodes) {
            min_x = Math.min(node.x - node.getWidth() * 0.5f, min_x);
            max_x = Math.max(node.x + node.getWidth() * 0.5f, max_x);
            min_y = Math.min(node.y - node.getHeight() * 0.5f, min_y);
            max_y = Math.max(node.y + node.getHeight() * 0.5f, max_y);
        }

        float offsetX = 0; // calculated
        float offsetY = 0; // calculated
        float screen_max_x;
        float screen_min_x;
        float screen_max_y;
        float screen_min_y;
        switch (anchor) {
            case CENTER_RIGHT:
                screen_max_x = Graphics.getWindowWidth() * 0.5f - max_x;
                offsetX = screen_max_x - anchorX;
                offsetY = 0;
                break;
            case CENTER_LEFT:
                screen_min_x = min_x + Graphics.getWindowWidth() * 0.5f;
                offsetX = anchorX - screen_min_x;
                offsetY = 0;
                break;
            case TOP_CENTER:
                screen_max_y = Graphics.getWindowHeight() * 0.5f - max_y;
                offsetX = 0;
                offsetY = screen_max_y - anchorY;
                break;
            case BOTTOM_CENTER:
                screen_min_y = min_y + Graphics.getWindowHeight() * 0.5f;
                offsetX = 0;
                offsetY = anchorY - screen_min_y;
                break;
            case TOP_LEFT:
                screen_min_x = min_x + Graphics.getWindowWidth() * 0.5f;
                screen_max_y = Graphics.getWindowHeight() * 0.5f - max_y;
                offsetX = anchorX - screen_min_x;
                offsetY = screen_max_y - anchorY;
                break;
            case TOP_RIGHT:
                screen_max_x = Graphics.getWindowWidth() * 0.5f - max_x;
                screen_max_y = Graphics.getWindowHeight() * 0.5f - max_y;
                offsetX = screen_max_x - anchorX;
                offsetY = screen_max_y - anchorY;
                break;
            case BOTTOM_RIGHT:
                screen_max_x = Graphics.getWindowWidth() * 0.5f - max_x;
                screen_min_y = min_y + Graphics.getWindowHeight() * 0.5f;
                offsetX = screen_max_x - anchorX;
                offsetY = anchorY - screen_min_y;
                break;
            case BOTTOM_LEFT:
                screen_min_x = min_x + Graphics.getWindowWidth() * 0.5f;
                screen_min_y = min_y + Graphics.getWindowHeight() * 0.5f;
                offsetX = anchorX - screen_min_x;
                offsetY = anchorY - screen_min_y;
                break;
            case CENTER_CENTER:
                // TODO
                break;
        }

        for (Node node : nodes) {
            node.refX = offsetX;
            node.refY = offsetY;
            node.refDeg = 0;
            node.refSclX = 1;
            node.refSclY = 1;
        }

        for (Node node : nodes) {
            node.update(delta);
        }

    }

    // frame update
    public final void handleInput(float delta) {
        for (Node node : nodes) {
            node.setInputRegion();
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
        if (nodes.contains(node,true)) throw new WidgetsException("Node " + node + " already contained in " + Widget.class.getSimpleName() + ".");
        if (node.container != null)           throw new WidgetsException("Node " + node + " has a parent container. Add only root elements to a " + Widget.class.getSimpleName() + ".");
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
