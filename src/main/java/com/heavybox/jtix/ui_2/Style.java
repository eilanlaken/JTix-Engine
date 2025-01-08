package com.heavybox.jtix.ui_2;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;

public final class Style implements Cloneable {

    public Position position         = Position.RELATIVE;
    public Size     sizeWidth        = Size.LIQUID;
    public Size     sizeHeight       = Size.LIQUID;
    public Overflow overflowVertical = Overflow.IGNORE;
    public Overflow overflowHorizontal = Overflow.IGNORE;
    public Font     font             = null;
    public int      fontSize         = 18;
    public boolean  renderBackground = true;
    public Color    backgroudColor   = Color.valueOf("#007BFF");
    public Color    textColor        = new Color(Color.WHITE);

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

    public int      borderRadiusTopLeft = 0;
    public int      borderRadiusTopRight = 0;
    public int      borderRadiusBottomRight = 0;
    public int      borderRadiusBottomLeft = 0;

    public int      borderRefinementTopLeft = 10;
    public int      borderRefinementTopRight = 10;
    public int      borderRefinementBottomRight = 10;
    public int      borderRefinementBottomLeft = 10;

    /* if width and height are NOT set, the widget will resize to fit its contents. */
    public int width = 100;
    public int height = 100;
    public int widthMin = 0;
    public int widthMax = Integer.MAX_VALUE;
    public int heightMin = 0;
    public int heightMax = Integer.MAX_VALUE;

    // TODO: implement interpolation (linear, cubic, etc.) between styles.

    public void set(final Style style) {
        this.position = style.position;
        this.overflowVertical = style.overflowVertical;
        this.overflowHorizontal = style.overflowHorizontal;
        this.font = style.font;
        this.fontSize = style.fontSize;
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

        this.borderRadiusTopLeft = style.borderRadiusTopLeft;
        this.borderRadiusTopRight = style.borderRadiusTopRight;
        this.borderRadiusBottomRight = style.borderRadiusBottomRight;
        this.borderRadiusBottomLeft = style.borderRadiusBottomLeft;

        this.borderRefinementTopLeft = style.borderRefinementTopLeft;
        this.borderRefinementTopRight = style.borderRefinementTopRight;
        this.borderRefinementBottomRight = style.borderRefinementBottomRight;
        this.borderRefinementBottomLeft = style.borderRefinementBottomLeft;

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
    public enum Position {
        ABSOLUTE,  // positioned x, y, deg, sclX, sclY from the window's center
        RELATIVE,  // positioned x, y, deg, sclX, sclY relative to its parent's center. If the container is null, x, y and deg are calculated relative to the window (effectively ABSOLUTE).
    }

    public enum Size {
        GAS,    // occupies all available space
        LIQUID, // conforms to fit container content
        SOLID,  // explicitly set by width and height
    }

}
