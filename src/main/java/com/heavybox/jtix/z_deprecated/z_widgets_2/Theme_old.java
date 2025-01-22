package com.heavybox.jtix.z_deprecated.z_widgets_2;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;

public final class Theme_old implements Cloneable {

    // TODO: remove from here


    // TODO: make static
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
    // TODO: add background image.


    // TODO: scrollbars
    public ScrollbarRenderer scrollbarRenderer = getDefaultScrollbarRenderer();

    public void set(final Theme_old theme) {
        this.contentOverflowY = theme.contentOverflowY;
        this.contentOverflowX = theme.contentOverflowX;
        this.contentAlignmentX = theme.contentAlignmentX;
        this.contentAlignmentY = theme.contentAlignmentY;
        this.textFont = theme.textFont;
        this.textSize = theme.textSize;
        this.textLineHeight = theme.textLineHeight;
        this.textWrapEnabled = theme.textWrapEnabled;
        this.boxBackgroudColor.set(theme.boxBackgroudColor);
        this.textColor.set(theme.textColor);

        this.boxPaddingTop = theme.boxPaddingTop;
        this.boxPaddingBottom = theme.boxPaddingBottom;
        this.boxPaddingLeft = theme.boxPaddingLeft;
        this.boxPaddingRight = theme.boxPaddingRight;

        this.boxCornerRadiusTopLeft = theme.boxCornerRadiusTopLeft;
        this.boxCornerRadiusTopRight = theme.boxCornerRadiusTopRight;
        this.boxCornerRadiusBottomRight = theme.boxCornerRadiusBottomRight;
        this.boxCornerRadiusBottomLeft = theme.boxCornerRadiusBottomLeft;

        this.boxCornerSegmentsTopLeft = theme.boxCornerSegmentsTopLeft;
        this.boxCornerSegmentsTopRight = theme.boxCornerSegmentsTopRight;
        this.boxCornerSegmentsBottomRight = theme.boxCornerSegmentsBottomRight;
        this.boxCornerSegmentsBottomLeft = theme.boxCornerSegmentsBottomLeft;

        this.boxBorderSize = theme.boxBorderSize;
        this.boxBackgroudColor.set(theme.boxBackgroudColor);
        this.boxBorderColor.set(theme.boxBorderColor);
    }

    public void setPadding(int padding) {
        this.boxPaddingLeft = padding;
        this.boxPaddingTop = padding;
        this.boxPaddingRight = padding;
        this.boxPaddingBottom = padding;
    }

    @Override
    public Theme_old clone() {
        try {
            return (Theme_old) super.clone();
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

    public enum Alignment {
        START,    // Equivalent to TOP or LEFT
        MIDDLE,   // Works for both vertical and horizontal centering
        END       // Equivalent to BOTTOM or RIGHT
    }

    // equivalent to CSS text alignment.
    // TODO: later.
    public enum TextWrap {
        NONE,
        CENTER,
        LEFT,
        RIGHT,
        JUSTIFY,
        // START, // language sensitive ltr or rtl
        // END
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
