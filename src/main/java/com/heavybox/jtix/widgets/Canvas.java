package com.heavybox.jtix.widgets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Renderer2D;
import org.jetbrains.annotations.NotNull;

/*
TODO: you would be able to anchor a canvas to one of the screen corners / CENTER.
 */
public class Canvas {

    public @NotNull Anchor anchorWindow = Anchor.CENTER_CENTER;
    public @NotNull Anchor anchorPivot  = Anchor.CENTER_CENTER;
    public          int    positionX    = 0;
    public          int    positionY    = 0;

    private final Array<Node> nodes = new Array<>(false, 5);

    public final void fixedUpdate(float delta) {
        for (Node node : nodes) {

        }
    }

    private void fixedUpdateContainer(float delta) {

    }

    private void fixedUpdateTerminal(float delta) {

    }

    // frame update
    public final void handleInput(float delta) {

    }

    // frame update
    public final void draw(Renderer2D renderer2D) {

    }

    public final void addNode(Node node) {
        if (nodes.contains(node, true)) throw new WidgetsException("Node " + node + " already contained in Canvas.");
        nodes.add(node);
    }

    public final void removeNode(Node node) {
        nodes.removeValue(node, true);
    }

    public enum Anchor {
        TOP_LEFT,    TOP_CENTER,    TOP_RIGHT,
        CENTER_LEFT, CENTER_CENTER, CENTER_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT,
    }

}
