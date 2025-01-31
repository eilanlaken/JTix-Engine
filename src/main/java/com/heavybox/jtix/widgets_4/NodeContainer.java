package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.widgets_3.WidgetsException;

/*
Follows CSS' box model, more or less -

draw():
 -----------------------------------------------------------
|                           border                         |
|          --------------------------------------          |
|         |               padding top            |         |
|         |          p0----------------p1        |         |
|         |  padding  |                | padding |         |
| border  |   left    |                |  right  |  border |
|         |           |    content     |         |         |
|         |           |    render()    |         |         |
|         |           |                |         |         |
|         |          p3----------------p2        |         |
|         |             padding bottom           |         |
|          --------------------------------------          |
|                           border                         |
 -----------------------------------------------------------

 */
public class NodeContainer extends Node {

    private final Array<Node> children = new Array<>(false, 5);

    public Sizing    boxWidthSizing               = Sizing.DYNAMIC;
    public float     boxWidthMin                  = 0;
    public float     boxWidthMax                  = Float.POSITIVE_INFINITY;
    public float     boxWidth                     = 1;
    public Sizing    boxHeightSizing              = Sizing.DYNAMIC;
    public float     boxHeightMin                 = 0;
    public float     boxHeightMax                 = Float.POSITIVE_INFINITY;
    public float     boxHeight                    = 1;
    public Overflow  contentOverflowX             = Overflow.IGNORE;
    public Overflow  contentOverflowY             = Overflow.IGNORE;
    public Color     boxBackgroudColor            = Color.valueOf("#007BFF");
    public boolean   boxBackgroundEnabled         = true;
    public int       boxPaddingTop                = 15;
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
    public int       boxBorderSize                = 8;
    public Color     boxBorderColor               = Color.RED.clone();

    // inner state
    private float calculatedWidth;
    private float calculatedHeight;
    private float backgroundWidth;
    private float backgroundHeight;
    private float innerOffsetX;

    public NodeContainer() {

    }

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
        setChildrenLayout(children);
        calculatedWidth = getWidth();
        calculatedHeight = getHeight();
        backgroundWidth = calculatedWidth - boxBorderSize * 2;
        backgroundHeight = calculatedHeight - boxBorderSize * 2;
        for (Node child : children) {
            child.update(delta);
        }
    }

    protected void setChildrenLayout(final Array<Node> children) {
        for (Node child : children) {
            child.refZIndex = screenZIndex;
            child.refX = screenX;
            child.refY = screenY;
            child.refDeg = screenDeg;
            child.refSclX = screenSclX;
            child.refSclY = screenSclY;
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
        if (boxBorderSize > 0) {
            renderer2D.setColor(boxBorderColor);
            renderer2D.drawRectangleBorder(backgroundWidth, backgroundHeight, boxBorderSize,
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

        // draw scrollbars
    }

    public float getContentWidth() {
        float max_x = 0;
        for (Node node : children) {
            max_x = Math.max(node.getWidth(), max_x);
        }
        return max_x;
    }

    public float getContentHeight() {
        float max_y = Float.NEGATIVE_INFINITY;
        for (Node node : children) {
            max_y = Math.max(node.getHeight(), max_y);
        }
        return max_y;
    }

    @Override
    public float getWidth() {
        float width = switch (boxWidthSizing) {
            case STATIC   -> boxWidth;
            case VIEWPORT -> boxWidth * Graphics.getWindowWidth();
            case DYNAMIC  -> getContentWidth() + boxPaddingLeft + boxPaddingRight + boxBorderSize + boxBorderSize;
        };
        return MathUtils.clampFloat(width, boxWidthMin, boxWidthMax);
    }

    @Override
    public float getHeight() {
        float height = switch (boxHeightSizing) {
            case STATIC   -> boxHeight;
            case VIEWPORT -> boxHeight * Graphics.getWindowHeight();
            case DYNAMIC  -> getContentHeight() + boxPaddingTop + boxPaddingBottom + boxBorderSize + boxBorderSize;
        };
        return MathUtils.clampFloat(height, boxHeightMin, boxHeightMax);
    }

    protected void setPolygon(final Polygon polygon) {
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

    public enum Sizing {
        STATIC,   // explicitly set by width and height
        DYNAMIC,  // conforms to fit content
        VIEWPORT, // relative to the viewport (width or height)
    }

}
