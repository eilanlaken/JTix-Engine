package com.heavybox.jtix.ui_2;

import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

import java.util.Objects;

/*
Follows CSS' box model, without borders.
Overflow happen whenever the content exceeds
the rectangular bounds set by [p0, p1, p2, p3].

draw():
 ----------------------------------------------------------------
|                         margin top                             |
|          --------------------------------------                |
|         |               padding top            |               |
|         |          p0----------------p1        |               |
|         |  padding  |                | padding |               |
| margin  |   left    |                |  right  |   margin      |
|  left   |           |    content     |         |    right      |
|         |           |    render()    |         |               |
|         |           |                |         |               |
|         |          p3----------------p2        |               |
|         |             padding bottom           |               |
|          --------------------------------------                |
|                                                                |
|                       margin bottom                            |
 ----------------------------------------------------------------

 */
public abstract class Node {

    public final int    id     = UI.getID();
    public final Region region = new Region();
    protected NodeContainer container = null;

    public boolean active = true;

    /* box-styling */
    public final Style style = new Style();

    /* calculated private attributes - computed every frame from the container, the style, etc. */
    protected int   screenZIndex = 0;
    protected float screenX      = 0;
    protected float screenY      = 0;
    protected float screenDeg    = 0;
    protected float screenSclX   = 1;
    protected float screenSclY   = 1;


    protected int screenWidth = 0; // TODO: change back to private.
    protected int screenHeight = 0;
    protected int boxWidth = 0; // total box width: screen + marginLeft + marginRight
    protected int boxHeight = 0; // total box height: screen + marginTop + marginBottom

    // this will be calculated from the style paddings etc.
    protected Vector2 p0 = new Vector2();
    protected Vector2 p1 = new Vector2();
    protected Vector2 p2 = new Vector2();
    protected Vector2 p3 = new Vector2();

    protected int innerOffsetX = 0;
    protected int innerOffsetY = 0;


    /* input handling */
    private boolean mouseInside         = false;
    private boolean mouseJustEntered    = false;
    private boolean mouseJustLeft       = false;
    private boolean mouseRegisterClicks = false;
    private boolean dragJustEntered     = false;

    /* set by parent */
    public int boxRegionWidth = 0;
    public int boxRegionHeight = 0;
    public int boxRegionCenterX = 0;
    public int boxRegionCenterY = 0;

    /* calculated locally */
    public int actualBackgroundWidth = 0;
    public int actualBackgroundHeight = 0;
    public int actualBackgroundX = 0;
    public int actualBackgroundY = 0;
    public int actualContentWidth = 0;
    public int actualContentHeight = 0;
    public int actualContentX = 0;
    public int actualContentY = 0;

    /* callbacks */
    protected Node() {
        setDefaultStyle();
    }

    protected Node(final Style inherited) {
        this.style.set(inherited);
        setDefaultStyle();
    }


    public void fixedUpdate(float delta) {
        update(delta);


        if (container == null) {
            //boxRegionWidth = Graphics.getWindowWidth();
            //boxRegionHeight = Graphics.getWindowHeight();
        }



        switch (style.sizeWidth) {
            case GAS: // make it so that the component fill the space
                actualBackgroundWidth = boxRegionWidth - (style.marginLeft + style.marginRight);
                actualBackgroundX = boxRegionCenterX + style.marginLeft - (style.marginLeft + style.marginRight) / 2;
                actualContentWidth = actualBackgroundWidth - (style.paddingLeft + style.paddingRight);
                actualContentWidth = MathUtils.clampInt(actualContentWidth, style.widthMin, style.widthMax); // clamp based on styling
                actualContentX = actualBackgroundX + style.paddingLeft - (style.paddingLeft + style.paddingRight) / 2;
                break;
            case LIQUID: // make it so that the component container conforms to its content
                actualContentWidth = getContentWidth();
                actualContentWidth = MathUtils.clampInt(actualContentWidth, style.widthMin, style.widthMax); // clamp based on styling
                actualBackgroundWidth = actualContentWidth + style.paddingLeft + style.paddingRight; // add padding.
                actualBackgroundX = boxRegionCenterX;
                actualContentX = actualBackgroundX + style.paddingLeft - (style.paddingLeft + style.paddingRight) / 2;
                break;
            case SOLID: // the component size is fixed.
                actualContentWidth = style.width;
                actualContentWidth = MathUtils.clampInt(actualContentWidth, style.widthMin, style.widthMax); // clamp based on styling
                actualBackgroundWidth = actualContentWidth + style.paddingLeft + style.paddingRight; // add padding.
                actualBackgroundX = boxRegionCenterX;
                actualContentX = actualBackgroundX + style.paddingLeft - (style.paddingLeft + style.paddingRight) / 2;
                break;
        }

        switch (style.sizeHeight) {
            case GAS: // make it so that the component fill the space
                actualBackgroundHeight = boxRegionHeight - (style.marginTop + style.marginBottom);
                System.out.println(actualBackgroundHeight);
                actualBackgroundY = boxRegionCenterY + style.marginBottom - (style.marginBottom + style.marginTop) / 2;
                actualContentHeight = actualBackgroundHeight - (style.paddingBottom + style.paddingTop);
                actualContentHeight = MathUtils.clampInt(actualContentHeight, style.heightMin, style.heightMax); // clamp based on styling
                actualContentY = actualBackgroundY + style.paddingBottom - (style.paddingBottom + style.paddingTop) / 2;
                break;
            case LIQUID: // make it so that the component container conforms to its content
                actualContentHeight = getContentHeight();
                actualContentHeight = MathUtils.clampInt(actualContentHeight, style.heightMin, style.heightMax); // clamp based on styling
                actualBackgroundHeight = actualContentHeight + style.paddingBottom + style.paddingTop; // add padding.
                actualBackgroundY = boxRegionCenterY;
                actualContentY = actualBackgroundY + style.paddingBottom - (style.paddingBottom + style.paddingTop) / 2;
                break;
            case SOLID: // the component size is fixed.
                actualContentHeight = style.height;
                actualContentHeight = MathUtils.clampInt(actualContentHeight, style.heightMin, style.heightMax); // clamp based on styling
                actualBackgroundHeight = actualContentHeight + style.paddingBottom + style.paddingTop; // add padding.
                actualBackgroundY = boxRegionCenterY;
                actualContentY = actualBackgroundY + style.paddingBottom - (style.paddingBottom + style.paddingTop) / 2;
                break;
        }


        /* update Region or Regions (included, excluded) based on border radius, padding, clip-paths etc. */ // TODO.

        /* update screen positions */
        if (style.position == Style.Position.ABSOLUTE || container == null) {
            this.screenZIndex = style.zIndex;
            this.screenX = style.x;
            this.screenY = style.y;
            this.screenDeg = style.deg;
            this.screenSclX = style.sclX;
            this.screenSclY = style.sclY;
        }





        /* apply transform */
        region.applyTransform(screenX, screenY, screenDeg, screenSclX, screenSclY);


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

        // TODO: apply content clipping using glScissors.
        // push pixel bounds if overflow != IGNORE
        //render(renderer2D, screenX + innerOffsetX, screenY + innerOffsetY, screenDeg, screenSclX, screenSclY);
        render(renderer2D, actualContentX, actualContentY, screenDeg, screenSclX, screenSclY);
        // pop pixel bounds if overflow != IGNORE

        renderForeground(renderer2D);

        if (UI.debug) region.draw(renderer2D);
    }

    protected void renderBackground(Renderer2D renderer2D) {
        if (style.renderBackground) {
            renderer2D.setColor(style.backgroudColor);
            renderer2D.drawRectangleFilled(actualBackgroundWidth, actualBackgroundHeight,

                    style.borderRadiusTopLeft, style.borderRefinementTopLeft,
                    style.borderRadiusTopRight, style.borderRefinementTopRight,
                    style.borderRadiusBottomRight, style.borderRefinementBottomRight,
                    style.borderRadiusBottomLeft, style.borderRefinementBottomLeft,

                    actualBackgroundX, actualBackgroundY, screenDeg, screenSclX, screenSclY);
        }
    }

    protected void renderForeground(Renderer2D renderer2D) {
        // render scrollbars if needed.

    }


    protected void update(float delta) {}
    protected abstract void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    protected abstract int getContentWidth();
    protected abstract int getContentHeight();
    protected abstract void setDefaultStyle();

    public boolean descendantOf(NodeContainer container) {
        if (container.children.contains(this)) return true;
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
