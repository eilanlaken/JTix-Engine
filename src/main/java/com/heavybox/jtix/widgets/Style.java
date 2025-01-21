package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;

public final class Style implements Cloneable {


    public Transform  transform                    = Transform.RELATIVE;
    public int        transformZIndex              = 0;
    public float      transformX                   = 0;
    public float      transformY                   = 0;
    public float      transformDeg                 = 0;
    public float      transformSclX                = 1;
    public float      transformSclY                = 1;
    public Sizing     sizingWidth                  = Sizing.DYNAMIC;
    public int        sizeWidth                    = 100;
    public int        sizeWidthMin                 = 0;
    public int        sizeWidthMax                 = Integer.MAX_VALUE;
    public Sizing     sizingHeight                 = Sizing.DYNAMIC;
    public int        sizeHeight                   = 100;
    public int        sizeHeightMin                = 0;
    public int        sizeHeightMax                = Integer.MAX_VALUE;
    public Font       textFont                     = null;
    public Color      textColor                    = Color.WHITE.clone();
    public int        textSize                     = 18;
    public float      textLineHeight               = 1.2f;
    public boolean    textAntialiasing             = true;
    public boolean    textWrapEnabled              = true;
    public Overflow   contentOverflowX             = Overflow.IGNORE;
    public Overflow   contentOverflowY             = Overflow.IGNORE;
    public Alignment  contentAlignmentX            = Alignment.MIDDLE;
    public Alignment  contentAlignmentY            = Alignment.MIDDLE;
    public Color      boxBackgroudColor            = Color.valueOf("#007BFF");
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



    // TODO: scrollbars
    public ScrollbarRenderer scrollbarRenderer = getDefaultScrollbarRenderer();

    public void set(final Style style) {
        this.transform = style.transform;
        this.sizingWidth = style.sizingWidth;
        this.sizingHeight = style.sizingHeight;
        this.contentOverflowY = style.contentOverflowY;
        this.contentOverflowX = style.contentOverflowX;
        this.contentAlignmentX = style.contentAlignmentX;
        this.contentAlignmentY = style.contentAlignmentY;
        this.textFont = style.textFont;
        this.textSize = style.textSize;
        this.textLineHeight = style.textLineHeight;
        this.textWrapEnabled = style.textWrapEnabled;
        this.boxBackgroudColor.set(style.boxBackgroudColor);
        this.textColor.set(style.textColor);

        this.transformX = style.transformX;
        this.transformY = style.transformY;
        this.transformDeg = style.transformDeg;
        this.transformSclX = style.transformSclX;
        this.transformSclY = style.transformSclY;
        this.transformZIndex = style.transformZIndex;

        this.boxPaddingTop = style.boxPaddingTop;
        this.boxPaddingBottom = style.boxPaddingBottom;
        this.boxPaddingLeft = style.boxPaddingLeft;
        this.boxPaddingRight = style.boxPaddingRight;

        this.boxCornerRadiusTopLeft = style.boxCornerRadiusTopLeft;
        this.boxCornerRadiusTopRight = style.boxCornerRadiusTopRight;
        this.boxCornerRadiusBottomRight = style.boxCornerRadiusBottomRight;
        this.boxCornerRadiusBottomLeft = style.boxCornerRadiusBottomLeft;

        this.boxCornerSegmentsTopLeft = style.boxCornerSegmentsTopLeft;
        this.boxCornerSegmentsTopRight = style.boxCornerSegmentsTopRight;
        this.boxCornerSegmentsBottomRight = style.boxCornerSegmentsBottomRight;
        this.boxCornerSegmentsBottomLeft = style.boxCornerSegmentsBottomLeft;

        this.boxBorderSize = style.boxBorderSize;
        this.boxBackgroudColor.set(style.boxBackgroudColor);
        this.boxBorderColor.set(style.boxBorderColor);

        this.sizeWidth = style.sizeWidth;
        this.sizeHeight = style.sizeHeight;
        this.sizeWidthMin = style.sizeWidthMin;
        this.sizeWidthMax = style.sizeWidthMax;
        this.sizeHeightMin = style.sizeHeightMin;
        this.sizeHeightMax = style.sizeHeightMax;
    }

    public void setPadding(int padding) {
        this.boxPaddingLeft = padding;
        this.boxPaddingTop = padding;
        this.boxPaddingRight = padding;
        this.boxPaddingBottom = padding;
    }

    @Override
    public Style clone() {
        try {
            return (Style) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /* controls how it renders the contents of the widget that overflow the box */
    public enum Overflow {
        IGNORE, // does nothing, renders while ignoring the bounds
        TRIM,   // uses glScissors to clip the content, so only the pixels that land inside the box render. The rest get trimmed.
        SCROLLBAR,
    }

    /* controls how the final transform of the widget is calculated. */
    public enum Transform {
        ABSOLUTE,  // positioned x, y, deg, sclX, sclY from the container's center (or window, if container is null).
        RELATIVE,  // positioned x, y, deg, sclX, sclY relative to the position calculated by its container. If the container is null, behaves like ABSOLUTE.
    }

    public enum Sizing {
        STATIC,  // explicitly set by width and height
        DYNAMIC, // conforms to fit content
    }

    public enum Alignment {
        START,    // Equivalent to TOP or LEFT
        MIDDLE,   // Works for both vertical and horizontal centering
        END       // Equivalent to BOTTOM or RIGHT
    }

    public static abstract class ScrollbarRenderer {

        protected abstract void render(Node node, float barWidth, float barHeight, float scrollProgressPercentage, float visiblePortionPercentage, float x, float y, float deg, float sclX, float sclY);

    }

    private static ScrollbarRenderer getDefaultScrollbarRenderer() {
        return new ScrollbarRenderer() {
            @Override
            protected void render(Node node, float barWidth, float barHeight, float progress, float viewPortion, float x, float y, float deg, float sclX, float sclY) {

            }
        };
    }

}
