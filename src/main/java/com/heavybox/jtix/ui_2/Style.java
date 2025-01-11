package com.heavybox.jtix.ui_2;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;

public final class Style implements Cloneable {

    public Transform           transform         = Transform.RELATIVE;
    public Sizing              sizeWidth         = Sizing.LIQUID;
    public Sizing              sizeHeight        = Sizing.LIQUID;
    public Font                textFont          = null;
    public Color               textColor         = new Color(Color.WHITE);
    public int                 textSize          = 18;
    public float               textLineHeight    = 1.2f;
    public boolean             textAntialiasing  = true;
    public boolean             textWrapEnabled   = true;
    public Overflow            contentOverflowX  = Overflow.IGNORE;
    public Overflow            contentOverflowY  = Overflow.IGNORE;
    public AlignmentHorizontal contentAlignmentX = AlignmentHorizontal.CENTER;
    public AlignmentVertical   contentAlignmentY = AlignmentVertical.CENTER;
    public Color               backgroudColor    = Color.valueOf("#007BFF");
    public boolean             backgroundEnabled = true;

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
    public int cornerSegmentBottomRight = 10;
    public int cornerSegmentsBottomLeft = 10;

    // borders are out of scope for now.

    /* if width and height are NOT set, the widget will resize to fit its contents. */

    public int width = 100;
    public int height = 100;
    public int widthMin = 0;
    public int widthMax = Integer.MAX_VALUE;
    public int heightMin = 0;
    public int heightMax = Integer.MAX_VALUE;

    // TODO: implement interpolation (linear, cubic, etc.) between styles.

    public void set(final Style style) {
        this.transform = style.transform;
        this.contentOverflowY = style.contentOverflowY;
        this.contentOverflowX = style.contentOverflowX;
        this.textFont = style.textFont;
        this.textSize = style.textSize;
        this.backgroudColor = style.backgroudColor;
        this.textColor = style.textColor;

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
        this.cornerSegmentBottomRight = style.cornerSegmentBottomRight;
        this.cornerSegmentsBottomLeft = style.cornerSegmentsBottomLeft;

        this.width = style.width;
        this.height = style.height;
        this.widthMin = style.widthMin;
        this.widthMax = style.widthMax;
        this.heightMin = style.heightMin;
        this.heightMax = style.heightMax;
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

    /* controls how a text attribute is laid-out inside the widget */
    public enum TextLayout {
        CENTER,
        LEFT,
        RIGHT,
        NEWSPAPER,
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

    public enum AlignmentVertical {
        TOP,
        CENTER,
        BOTTOM,
    }

    public enum AlignmentHorizontal {
        LEFT,
        CENTER,
        RIGHT,
    }

}
