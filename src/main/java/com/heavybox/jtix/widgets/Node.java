package com.heavybox.jtix.widgets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;

import java.util.Objects;

/*
Follows CSS' box model, without borders.
Overflow happen whenever the content exceeds
the rectangular bounds set by [p0, p1, p2, p3].

draw():
 ----------------------------------------------------------------
|                            border                              |
|          --------------------------------------                |
|         |               padding top            |               |
|         |          p0----------------p1        |               |
|         |  padding  |                | padding |               |
| border  |   left    |                |  right  |    border     |
|         |           |    content     |         |               |
|         |           |    render()    |         |               |
|         |           |                |         |               |
|         |          p3----------------p2        |               |
|         |             padding bottom           |               |
|          --------------------------------------                |
|                           border                               |
 ----------------------------------------------------------------

 */
public abstract class Node {

    public final int id = Widgets.getID();
    public final Region region = new Region();
    protected NodeContainer container = null;

    public boolean active = true;

    /* box-styling */
    public final Style style = Widgets.getGlobalTheme();
    public final Array<Style.Animation> animations = new Array<>(); // TODO.

    /* calculated private attributes - computed every frame from the container, the style, etc. */
    protected int innerOffsetX = 0;
    protected int innerOffsetY = 0;


    /* input handling */
    private boolean mouseInside         = false;
    private boolean mouseJustEntered    = false;
    private boolean mouseJustLeft       = false;
    private boolean mouseRegisterClicks = false;
    private boolean dragJustEntered     = false;

    /* calculated */
    public float boxWidth = 0;
    public float boxHeight = 0;
    public float boxX = 0;
    public float boxY = 0;
    public float boxDeg = 0;
    public float boxSclX = 1;
    public float boxSclY = 1;

    public float contentWidth = 0;
    public float contentHeight = 0;
    public float contentX = 0;
    public float contentY = 0;
    public float contentDeg = 0;
    public float contentSclX = 1;
    public float contentSclY = 1;

    private boolean shouldApplyMasking;
    private float overflowX;
    private float overflowY;

    /* callbacks */
    protected Node() {
        setDefaultStyle();
    }

    protected Node(final Style inherited) {
        this.style.set(inherited);
        setDefaultStyle();
    }

    public void update(float delta) {
        fixedUpdate(delta);

        // masking

        if (container == null) {
            //boxRegionWidth = Graphics.getWindowWidth();
            //boxRegionHeight = Graphics.getWindowHeight();
        }



        switch (style.sizingWidth) {
            case AUTO: // make it so that the component container conforms to its content
                contentWidth = getContentWidth();
                contentWidth = MathUtils.clampFloat(contentWidth, style.sizeWidthMin, style.sizeWidthMax); // clamp based on styling
                boxWidth = contentWidth + style.boxPaddingLeft + style.boxPaddingRight; // add padding.
//                backgroundX = boxCenterX;
                contentX = boxX + style.boxPaddingLeft - (style.boxPaddingLeft + style.boxPaddingRight) * 0.5f;
                break;
            case ABSOLUTE: // the component size is fixed.
                contentWidth = style.sizeWidth;
                contentWidth = MathUtils.clampFloat(contentWidth, style.sizeWidthMin, style.sizeWidthMax); // clamp based on styling
                boxWidth = contentWidth + style.boxPaddingLeft + style.boxPaddingRight; // add padding.
//                backgroundX = boxCenterX;
                contentX = boxX + style.boxPaddingLeft - (style.boxPaddingLeft + style.boxPaddingRight) * 0.5f;
                break;
        }

        switch (style.sizingHeight) {
            case AUTO: // make it so that the component container conforms to its content
                contentHeight = getContentHeight();
                contentHeight = MathUtils.clampFloat(contentHeight, style.sizeHeightMin, style.sizeHeightMax); // clamp based on styling
                boxHeight = contentHeight + style.boxPaddingBottom + style.boxPaddingTop; // add padding.
//                backgroundY = boxCenterY;
                contentY = boxY + style.boxPaddingBottom - (style.boxPaddingBottom + style.boxPaddingTop) * 0.5f;
                break;
            case ABSOLUTE: // the component size is fixed.
                contentHeight = style.sizeHeight;
                contentHeight = MathUtils.clampFloat(contentHeight, style.sizeHeightMin, style.sizeHeightMax); // clamp based on styling
                boxHeight = contentHeight + style.boxPaddingBottom + style.boxPaddingTop; // add padding.
//                backgroundY = boxCenterY;
                contentY = boxY + style.boxPaddingBottom - (style.boxPaddingBottom + style.boxPaddingTop) * 0.5f;
                break;
        }


        /* update Region or Regions (included, excluded) based on border radius, padding, clip-paths etc. */ // TODO.

        /* update screen positions */




        /* apply transform */
        region.applyTransform(boxX, boxY, boxDeg, boxSclX, boxSclY);


    }

    protected final int getMaskingInteger() {
        if (container == null) return 1;
        return 1 + container.getMaskingInteger();
    }

    public void handleInput() { // TODO: delta will be used to detect double clicks.
        float delta = Graphics.getDeltaTime();
        /* handle input */
        float xMouse = Input.mouse.getX() - Graphics.getWindowWidth() * 0.5f;
        float yMouse = Graphics.getWindowHeight() * 0.5f - Input.mouse.getY();
        float xMousePrev = Input.mouse.getXPrev() - Graphics.getWindowWidth() * 0.5f;
        float yMousePrev = Graphics.getWindowHeight() * 0.5f - Input.mouse.getYPrev();
        mouseInside = region.containsPoint(xMouse, yMouse);
        boolean mousePrevInside = region.containsPoint(xMousePrev, yMousePrev);
        mouseJustEntered = !mousePrevInside && mouseInside;
        mouseJustLeft = !mouseInside && mousePrevInside;
        if (Input.mouse.isButtonJustPressed(Mouse.Button.LEFT)) {
            mouseRegisterClicks = mouseInside;
        }

        /* invoke event callbacks */
        // TODO
        if (mouseRegisterClicks && Input.mouse.isButtonClicked(Mouse.Button.LEFT) && mouseInside) {
            onClick();
        }
        if (mouseJustEntered) {
            onMouseOver();
        }
        if (mouseJustLeft) {
            onMouseOut();
        }


    }

    public void draw(Renderer2D renderer2D) {
        renderBackground(renderer2D);
        renderBorder(renderer2D);

        shouldApplyMasking = (style.contentOverflowX != Style.Overflow.IGNORE || style.contentOverflowY != Style.Overflow.IGNORE)
                && (overflowX > 0 || overflowY > 0);

        shouldApplyMasking = true;
        if (shouldApplyMasking) {
            maskWrite(renderer2D);
            renderer2D.enableMasking();
            renderer2D.setMaskingFunctionEquals(1); // TODO: instead of 1, put the correct value for masking.
            render(renderer2D, contentX, contentY, contentDeg, contentSclX, contentSclY);
            renderer2D.disableMasking();
            maskErase(renderer2D);
        } else {
            render(renderer2D, contentX, contentY, contentDeg, contentSclX, contentSclY);
        }



        if (Widgets.debug) region.draw(renderer2D);
    }

    private void maskWrite(Renderer2D renderer2D) {
        renderer2D.beginStencil();
        renderer2D.setStencilModeIncrement();
        renderer2D.drawRectangleFilled(boxWidth, boxHeight,
                style.boxCornerRadiusTopLeft, style.boxCornerSegmentsTopLeft,
                style.boxCornerRadiusTopRight, style.boxCornerSegmentsTopRight,
                style.boxCornerRadiusBottomRight, style.boxCornerSegmentsBottomRight,
                style.boxCornerRadiusBottomLeft, style.boxCornerSegmentsBottomLeft,
                boxX, boxY, boxDeg, boxSclX, boxSclY);
        renderer2D.endStencil();
    }

    private void maskErase(Renderer2D renderer2D) {
        renderer2D.beginStencil();
        renderer2D.setStencilModeDecrement();
        renderer2D.drawRectangleFilled(boxWidth, boxHeight,
                style.boxCornerRadiusTopLeft, style.boxCornerSegmentsTopLeft,
                style.boxCornerRadiusTopRight, style.boxCornerSegmentsTopRight,
                style.boxCornerRadiusBottomRight, style.boxCornerSegmentsBottomRight,
                style.boxCornerRadiusBottomLeft, style.boxCornerSegmentsBottomLeft,
                boxX, boxY, boxDeg, boxSclX, boxSclY);
        renderer2D.endStencil();
    }

    protected void renderBackground(Renderer2D renderer2D) {
        if (style.boxBackgroundEnabled) {
            renderer2D.setColor(style.boxBackgroudColor);
            renderer2D.drawRectangleFilled(boxWidth, boxHeight,
                    style.boxCornerRadiusTopLeft, style.boxCornerSegmentsTopLeft,
                    style.boxCornerRadiusTopRight, style.boxCornerSegmentsTopRight,
                    style.boxCornerRadiusBottomRight, style.boxCornerSegmentsBottomRight,
                    style.boxCornerRadiusBottomLeft, style.boxCornerSegmentsBottomLeft,
                    boxX, boxY, boxDeg, boxSclX, boxSclY);
        }
    }

    protected void renderBorder(Renderer2D renderer2D) {
        if (style.boxBorderSize > 0) {
            renderer2D.setColor(style.boxBorderColor);
            renderer2D.drawRectangleBorder(boxWidth, boxHeight, style.boxBorderSize,
                    style.boxCornerRadiusTopLeft, style.boxCornerSegmentsTopLeft,
                    style.boxCornerRadiusTopRight, style.boxCornerSegmentsTopRight,
                    style.boxCornerRadiusBottomRight, style.boxCornerSegmentsBottomRight,
                    style.boxCornerRadiusBottomLeft, style.boxCornerSegmentsBottomLeft,
                    boxX, boxY, boxDeg, boxSclX, boxSclY);
        }
    }

    protected void fixedUpdate(float delta) {}
    protected abstract void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    protected abstract int getContentWidth();
    protected abstract int getContentHeight();
    protected abstract void setDefaultStyle();

    public boolean descendantOf(NodeContainer container) {
        if (container.children.contains(this, true)) return true;
        boolean result = false;
        for (Node child : container.children) {
            if (child instanceof NodeContainer) {
                result = result || descendantOf((NodeContainer) child);
            }
        }
        return result;
    }

    // TODO: replace with callback lambda expression attributes
    /* Triggered when the element is clicked. */
    protected void onClick() {}
    /* Triggered when the mouse button is pressed down. */
    protected void onMouseDown() {}
    /* Triggered when the mouse button is released. */
    protected void onMouseUp() {}
    /* Triggered when the mouse hovers over the element. */
    protected void onMouseOver() {}
    /* Triggered when the mouse moves out of the element. */
    protected void onMouseOut() {}
    /* Triggered when the mouse moves over the element. */
    protected void onMouseMove() {}

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " id: " + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node widget = (Node) o;
        return id == widget.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
