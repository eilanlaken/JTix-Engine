package com.heavybox.jtix.z_deprecated.z_widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

/*
Follows CSS' box model, without borders.
Overflow happen whenever the content exceeds
the rectangular bounds set by [p0, p1, p2, p3].

render():
 ----------------------------------------------------------------
|                            border                              |
|          --------------------------------------                |
|         |               padding top            |               |
|         |          p0----------------p1        |               |
|         |  padding  |                | padding |               |
| border  |   left    |                |  right  |    border     |
|         |           |   children's   |         |               |
|         |           |    render()    |         |               |
|         |           |                |         |               |
|         |          p3----------------p2        |               |
|         |             padding bottom           |               |
|          --------------------------------------                |
|                           border                               |
 ----------------------------------------------------------------

 */
public abstract class NodeContainer extends Node {

    protected final Array<Node> children = new Array<>(true,5);
    public float childrenMargins = 10;

    /* box-styling */

    public Node.Sizing sizingWidth     = Node.Sizing.AUTO;
    public int       sizeWidth       = 100;
    public int       sizeWidthMin    = 0;
    public int       sizeWidthMax    = Integer.MAX_VALUE;
    public Node.Sizing sizingHeight    = Node.Sizing.AUTO;
    public int       sizeHeight      = 100;
    public int       sizeHeightMin   = 0;
    public int       sizeHeightMax   = Integer.MAX_VALUE;

    /* calculated private attributes - computed every frame from the container, the style, etc. */
    protected int innerOffsetX = 0;
    protected int innerOffsetY = 0;

    public Theme.Overflow contentOverflowX             = Theme.Overflow.IGNORE;
    public Theme.Overflow contentOverflowY             = Theme.Overflow.IGNORE;
    public Theme.Alignment contentAlignmentX            = Theme.Alignment.MIDDLE;
    public Theme.Alignment contentAlignmentY            = Theme.Alignment.MIDDLE;
    public Color boxBackgroudColor            = Color.valueOf("#007BFF");
    public boolean    boxBackgroundEnabled         = true;
    public int        boxPaddingTop                = 5;
    public int        boxPaddingBottom             = 5;
    public int        boxPaddingLeft               = 5;
    public int        boxPaddingRight              = 5;
    public int        boxCornerRadiusTopLeft       = 0;
    public int        boxCornerRadiusTopRight      = 0;
    public int        boxCornerRadiusBottomRight   = 0;
    public int        boxCornerRadiusBottomLeft    = 0;
    public int        boxCornerSegmentsTopLeft     = 10;
    public int        boxCornerSegmentsTopRight    = 10;
    public int        boxCornerSegmentsBottomRight = 10;
    public int        boxCornerSegmentsBottomLeft  = 10;
    public int        boxBorderSize                = 0;
    public Color      boxBorderColor               = Color.RED.clone();


    /* callbacks */

    @Override
    public void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderBackground(renderer2D);
        renderBorder(renderer2D);

//        boolean shouldApplyMasking = (contentOverflowX != Theme.Overflow.IGNORE || contentOverflowY != Theme.Overflow.IGNORE)
//                && (overflowX > 0 || overflowY > 0);
//
//        shouldApplyMasking = true;
//        if (shouldApplyMasking) {
//            maskWrite(renderer2D);
//            renderer2D.enableMasking();
//            renderer2D.setMaskingFunctionEquals(1); // TODO: instead of 1, put the correct value for masking.
//            render(renderer2D, contentX, contentY, contentDeg, contentSclX, contentSclY);
//            renderer2D.disableMasking();
//            maskErase(renderer2D);
//        } else {
//            render(renderer2D, contentX, contentY, contentDeg, contentSclX, contentSclY);
//        }



        if (Widgets.debug) region.draw(renderer2D);
    }

    private void maskWrite(Renderer2D renderer2D) {
        renderer2D.beginStencil();
        renderer2D.setStencilModeIncrement();
        renderer2D.drawRectangleFilled(boxWidth, boxHeight,
                boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft,
                boxX, boxY, boxDeg, boxSclX, boxSclY);
        renderer2D.endStencil();
    }

    private void maskErase(Renderer2D renderer2D) {
        renderer2D.beginStencil();
        renderer2D.setStencilModeDecrement();
        renderer2D.drawRectangleFilled(boxWidth, boxHeight,
                boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft,
                boxX, boxY, boxDeg, boxSclX, boxSclY);
        renderer2D.endStencil();
    }

    protected void renderBackground(Renderer2D renderer2D) {
        if (boxBackgroundEnabled) {
            renderer2D.setColor(boxBackgroudColor);
            renderer2D.drawRectangleFilled(boxWidth, boxHeight,
                    boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                    boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                    boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                    boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft,
                    boxX, boxY, boxDeg, boxSclX, boxSclY);
        }
    }

    protected void renderBorder(Renderer2D renderer2D) {
        if (boxBorderSize > 0) {
            renderer2D.setColor(boxBorderColor);
            renderer2D.drawRectangleBorder(boxWidth, boxHeight, boxBorderSize,
                    boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                    boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                    boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                    boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft,
                    boxX, boxY, boxDeg, boxSclX, boxSclY);
        }
    }

    protected abstract int getContentWidth();
    protected abstract int getContentHeight();

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
