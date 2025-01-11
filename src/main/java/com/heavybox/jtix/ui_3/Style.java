package com.heavybox.jtix.ui_3;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;

public final class Style implements Cloneable {

    public Transform transform         = Transform.RELATIVE;
    public Sizing    sizingWidth       = Sizing.LIQUID;
    public Sizing    sizingHeight      = Sizing.LIQUID;
    public Font      textFont          = null;
    public Color     textColor         = new Color(Color.WHITE);
    public int       textSize          = 18;
    public float     textLineHeight    = 1.2f;
    public boolean   textAntialiasing  = true;
    public boolean   textWrapEnabled   = true;
    public Overflow  contentOverflowX  = Overflow.IGNORE;
    public Overflow  contentOverflowY  = Overflow.IGNORE;
    public Alignment contentAlignmentX = Alignment.MIDDLE;
    public Alignment contentAlignmentY = Alignment.MIDDLE;
    public Color     backgroudColor    = Color.valueOf("#007BFF");
    public boolean   backgroundEnabled = true;

    public int     zIndex   = 0;
    public float   x        = 0;
    public float   y        = 0;
    public float   deg      = 0;
    public float   sclX     = 1;
    public float   sclY     = 1;

    public int      marginTop      = 0;
    public int      marginBottom   = 0;
    public int      marginLeft     = 0;
    public int      marginRight    = 0;

    public int      paddingTop     = 5;
    public int      paddingBottom  = 5;
    public int      paddingLeft    = 5;
    public int      paddingRight   = 5;

    public int cornerRadiusTopLeft = 0;
    public int cornerRadiusTopRight = 0;
    public int cornerRadiusBottomRight = 0;
    public int cornerRadiusBottomLeft = 0;

    public int cornerSegmentsTopLeft = 10;
    public int cornerSegmentsTopRight = 10;
    public int cornerSegmentsBottomRight = 10;
    public int cornerSegmentsBottomLeft = 10;

    // borders
    public int   borderSize  = 0;
    public Color borderColor = new Color(Color.BLACK);

    public int width = 100;
    public int height = 100;
    public int widthMin = 0;
    public int widthMax = Integer.MAX_VALUE;
    public int heightMin = 0;
    public int heightMax = Integer.MAX_VALUE;

    // TODO: implement interpolation (linear, cubic, etc.) between styles.

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
        this.backgroudColor.set(style.backgroudColor);
        this.textColor.set(style.textColor);

        this.x = style.x;
        this.y = style.y;
        this.deg = style.deg;
        this.sclX = style.sclX;
        this.sclY = style.sclY;
        this.zIndex = style.zIndex;

        this.marginTop = style.marginTop;
        this.marginBottom = style.marginBottom;
        this.marginLeft = style.marginLeft;
        this.marginRight = style.marginRight;

        this.paddingTop = style.paddingTop;
        this.paddingBottom = style.paddingBottom;
        this.paddingLeft = style.paddingLeft;
        this.paddingRight = style.paddingRight;

        this.cornerRadiusTopLeft = style.cornerRadiusTopLeft;
        this.cornerRadiusTopRight = style.cornerRadiusTopRight;
        this.cornerRadiusBottomRight = style.cornerRadiusBottomRight;
        this.cornerRadiusBottomLeft = style.cornerRadiusBottomLeft;

        this.cornerSegmentsTopLeft = style.cornerSegmentsTopLeft;
        this.cornerSegmentsTopRight = style.cornerSegmentsTopRight;
        this.cornerSegmentsBottomRight = style.cornerSegmentsBottomRight;
        this.cornerSegmentsBottomLeft = style.cornerSegmentsBottomLeft;

        this.width = style.width;
        this.height = style.height;
        this.widthMin = style.widthMin;
        this.widthMax = style.widthMax;
        this.heightMin = style.heightMin;
        this.heightMax = style.heightMax;

        this.borderSize = style.borderSize;
        this.backgroudColor.set(style.borderColor);
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
        GAS,    // occupies all available space
        LIQUID, // conforms to fit container content
        SOLID,  // explicitly set by width and height
    }

    public enum Alignment {
        START,    // Equivalent to TOP or LEFT
        MIDDLE,   // Works for both vertical and horizontal centering
        END       // Equivalent to BOTTOM or RIGHT
    }

}
