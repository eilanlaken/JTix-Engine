package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.widgets_3.WidgetsException;

public class NodeContainer extends Node {

    final Array<Node> children = new Array<>(false, 5);

    public Overflow  contentOverflowX             = Overflow.IGNORE;
    public Overflow  contentOverflowY             = Overflow.IGNORE;
    public Alignment contentAlignmentX            = Alignment.MIDDLE;
    public Alignment contentAlignmentY            = Alignment.MIDDLE;
    public Color     boxBackgroudColor            = Color.valueOf("#007BFF");
    public boolean   boxBackgroundEnabled         = true;
    public int       boxPaddingTop                = 5;
    public int       boxPaddingBottom             = 5;
    public int       boxPaddingLeft               = 5;
    public int       boxPaddingRight              = 5;
    public int       boxCornerRadiusTopLeft       = 0;
    public int       boxCornerRadiusTopRight      = 0;
    public int       boxCornerRadiusBottomRight   = 0;
    public int       boxCornerRadiusBottomLeft    = 0;
    public int       boxCornerSegmentsTopLeft     = 10;
    public int       boxCornerSegmentsTopRight    = 10;
    public int       boxCornerSegmentsBottomRight = 10;
    public int       boxCornerSegmentsBottomLeft  = 10;
    public int       boxBorderSize                = 0;
    public Color     boxBorderColor               = Color.RED.clone();

    // sizing
    public Sizing boxSizingWidth = Sizing.AUTO;
    public float boxWidth;
    public Sizing boxSizingHeight = Sizing.AUTO;
    public float boxHeight;

    // state
    public float calculatedWidth;
    public float calculatedHeight;

    float backgroundWidth;
    float backgroundHeight;
    float innerOffsetX;

    public void addChild(Node child) {
        if (child == null) throw new WidgetsException(Node.class.getSimpleName() + " element cannot be null.");
        if (child == this) throw new WidgetsException("Trying to parent a " + Node.class.getSimpleName() + " to itself.");
        children.add(child);
        child.container = this;
    }

    protected void removeChild(Node node) {
        node.container = null;
        children.removeValue(node, true);
    }

    @Override
    protected void fixedUpdate(float delta) {

        backgroundWidth = getWidth() - boxBorderSize * 2;
        backgroundHeight = getHeight() - boxBorderSize * 2;
        for (Node child : children) {
            child.parentWidth = backgroundWidth - boxPaddingLeft - boxPaddingRight;
            child.parentHeight = backgroundHeight - boxPaddingTop - boxPaddingBottom;
        }
    }

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (boxBackgroundEnabled) {
            renderer2D.setColor(boxBackgroudColor);
            renderer2D.drawRectangleFilled(backgroundWidth, backgroundHeight,
                    boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                    boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                    boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                    boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft,
                    screenX, screenY, screenDeg, screenSclX, screenSclY);
        }
        // begin mask
        // draw mask
        // end mask
        // enable masking
        for (Node child : children) {
            child.draw(renderer2D);
        }
        // disable masking
        // begin mask
        // erase mask
        // end mask
    }

    public float getContentWidth() {
        float min_x = Float.POSITIVE_INFINITY;
        float max_x = Float.NEGATIVE_INFINITY;
        for (Node node : children) {
            if (node instanceof NodeContainer) {
                NodeContainer child = (NodeContainer) node;
                if (child.boxSizingWidth == Sizing.RELATIVE) {
                    min_x = Math.min(node.x - child.getContentWidth() * 0.5f, min_x);
                    max_x = Math.max(node.x + child.getContentWidth() * 0.5f, max_x);
                    continue;
                }
            }
            min_x = Math.min(node.x - node.getWidth() * 0.5f, min_x);
            max_x = Math.max(node.x + node.getWidth() * 0.5f, max_x);
        }
        return Math.abs(max_x - min_x) + boxPaddingLeft + boxPaddingRight + boxBorderSize + boxBorderSize;
    }

    public float getContentHeight() {
        float min_y = Float.POSITIVE_INFINITY;
        float max_y = Float.NEGATIVE_INFINITY;
        for (Node node : children) {
            if (node instanceof NodeContainer) {
                NodeContainer child = (NodeContainer) node;
                if (child.boxSizingHeight == Sizing.RELATIVE) {
                    min_y = Math.min(node.y - child.getContentHeight() * 0.5f, min_y);
                    max_y = Math.max(node.y + child.getContentHeight() * 0.5f, max_y);
                    continue;
                }
            }
            min_y = Math.min(node.y - node.getHeight() * 0.5f, min_y);
            max_y = Math.max(node.y + node.getHeight() * 0.5f, max_y);
        }
        return Math.abs(max_y - min_y) + boxPaddingLeft + boxPaddingRight + boxBorderSize + boxBorderSize;
    }

    @Override
    public float getWidth() {
        float width = 0;
        switch (boxSizingWidth) {
            case ABSOLUTE:
                width = boxWidth + boxPaddingLeft + boxPaddingRight + boxBorderSize + boxBorderSize;
                break;
            case RELATIVE:
                width = boxWidth * parentWidth;
                break;
            case AUTO:
                width = getContentWidth();
                break;
        };
        return width;
    }

    @Override
    public float getHeight() {
        float height = 0;
        switch (boxSizingHeight) {
            case ABSOLUTE:
                height = boxHeight + boxPaddingTop + boxPaddingBottom + boxBorderSize + boxBorderSize;
                break;
            case RELATIVE:
                height = boxHeight * (parentHeight - boxPaddingTop + boxPaddingBottom + boxBorderSize + boxBorderSize);
                break;
            case AUTO:
                height = getContentHeight();
                break;
        };
        return height;
    }

    protected void updatePolygon(final Polygon polygon) {
        polygon.setToRectangle(
                calculatedWidth, calculatedHeight,
                boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft
        );
    }

    /* controls how it renders the contents of the widget that overflow the box */
    public enum Overflow {
        IGNORE,    // does nothing, renders while ignoring the bounds
        TRIM,      // uses glScissors to clip the content, so only the pixels that land inside the box render. The rest get trimmed.
        SCROLLBAR, // trims the content and adds scrollbars
    }

    public enum Alignment {
        START,  // Equivalent to TOP or LEFT
        MIDDLE, // Works for both vertical and horizontal centering
        END     // Equivalent to BOTTOM or RIGHT
    }

    public enum Sizing {
        ABSOLUTE, // explicitly set by width and height
        RELATIVE, // relative to the container's calculated dimensions
        AUTO,     // conforms to fit content
    }

}
