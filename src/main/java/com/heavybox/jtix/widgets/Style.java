package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;

public class Style {

    public Position   position           = Position.CONTAINER;
    public Color      backgroundColor    = Color.valueOf("#202123");
    public Color      textColor          = Color.valueOf("#ECECEC");
    public TextLayout textLayout         = TextLayout.CENTER;
    public Overflow   overflow           = Overflow.TRIM;
    //public Overflow   overflowVertical   = Overflow.TRIM; // TODO: add optional vertical and horizontal.
    //public Overflow   overflowHorizontal = Overflow.TRIM;
    public Font       font               = null;
    public int        fontSize           = 18;

    public float      marginTop       = 0;
    public float      marginBottom    = 0;
    public float      marginLeft      = 0;
    public float      marginRight     = 0;

    public float      paddingTop      = 0;
    public float      paddingBottom   = 0;
    public float      paddingLeft     = 0;
    public float      paddingRight    = 0;

    public float      borderRadiusTopLeft     = 0;
    public float      borderRadiusTopRight    = 0;
    public float      borderRadiusBottomRight = 0;
    public float      borderRadiusBottomLeft  = 0;

    public int        borderRefinementTopLeft     = 20;
    public int        borderRefinementTopRight    = 20;
    public int        borderRefinementBottomRight = 20;
    public int        borderRefinementBottomLeft  = 20;

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
