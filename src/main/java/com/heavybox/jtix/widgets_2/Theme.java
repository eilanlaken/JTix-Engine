package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;

// represents a global UI theme of an application.
// styles can be inline to override this global theme.
public final class Theme {

    // TODO: make static
    public static Font       textFont                     = null;
    public static Color      textColor                    = Color.WHITE.clone();
    public static int        textSize                     = 18;
    public static float      textLineHeight               = 1.2f;
    public static boolean    textAntialiasing             = true;
    public static boolean    textWrapEnabled              = true;
    public static Overflow   contentOverflowX             = Overflow.IGNORE;
    public static Overflow   contentOverflowY             = Overflow.IGNORE;
    public static Alignment  contentAlignmentX            = Alignment.MIDDLE;
    public static Alignment  contentAlignmentY            = Alignment.MIDDLE;
    public static Color      boxBackgroudColor            = Color.valueOf("#007BFF");
    public static boolean    boxBackgroundEnabled         = true;
    public static int        boxPaddingTop                = 5;
    public static int        boxPaddingBottom             = 5;
    public static int        boxPaddingLeft               = 5;
    public static int        boxPaddingRight              = 5;
    public static int        boxCornerRadiusTopLeft       = 0;
    public static int        boxCornerRadiusTopRight      = 0;
    public static int        boxCornerRadiusBottomRight   = 0;
    public static int        boxCornerRadiusBottomLeft    = 0;
    public static int        boxCornerSegmentsTopLeft     = 10;
    public static int        boxCornerSegmentsTopRight    = 10;
    public static int        boxCornerSegmentsBottomRight = 10;
    public static int        boxCornerSegmentsBottomLeft  = 10;
    public static int        boxBorderSize                = 0;
    public static Color      boxBorderColor               = Color.RED.clone();
    // TODO: add background image.


    // TODO: scrollbars
    public static ScrollbarRenderer scrollbarRenderer = getDefaultScrollbarRenderer();

    @Override
    public Theme clone() {
        try {
            return (Theme) super.clone();
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
