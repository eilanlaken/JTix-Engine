package com.heavybox.jtix.widgets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;

public class Style {

    public Position position       = Position.CONTAINER;
    public Overflow overflow       = Overflow.TRIM;
    public Font     font           = null;
    public int      fontSize       = 18;
    public Color    backgroudColor = Color.valueOf("#007BFF");
    public Color    textColor      = new Color(Color.WHITE);

    public int    marginTop      = 0;
    public int    marginBottom   = 0;
    public int    marginLeft     = 0;
    public int    marginRight    = 0;

    public int    paddingTop     = 5;
    public int    paddingBottom  = 5;
    public int    paddingLeft    = 10;
    public int    paddingRight   = 10;

    public int borderRadiusTopLeft = 0;
    public int borderRadiusTopRight = 0;
    public int borderRadiusBottomRight = 0;
    public int borderRadiusBottomLeft = 0;

    public int borderRefinementTopLeft = 10;
    public int borderRefinementTopRight = 10;
    public int borderRefinementBottomRight = 10;
    public int borderRefinementBottomLeft = 10;

    /* if width and height are NOT set, the widget will resize to fit its contents. */
    public int width = 50;
    public int height = -1;
    public int widthMin = 0;
    public int widthMax = Integer.MAX_VALUE;
    public int heightMin = 0;
    public int heightMax = Integer.MAX_VALUE;
    public Array<Region> regionsIn  = new Array<>(); // clip path, not affecting rendering - only input
    public Array<Region> regionsOut = new Array<>(); // clip path, not affecting rendering - only input

    /* controls how it renders the contents of the widget that overflow the box */
    public enum Overflow {
        IGNORE, // does nothing, renders while ignoring the bounds
        TRIM,   // uses glScissors to clip the content, so only the pixels that land inside the box render. The rest get trimmed.
        //SCROLLBAR,
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
        ABSOLUTE,  // positioned x, y, deg relative to the window's center
        RELATIVE,  // positioned x, y, deg relative to its container's center. If the container is null, x, y and deg are calculated relative to the window (effectively ABSOLUTE).
        CONTAINER, // positioned by its container layout, ignoring x, y, deg, scaleX and scaleY. If the container is null, then behaves like ABSOLUTE
    }

}
