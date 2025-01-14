package com.heavybox.jtix.z_old_widgets;

public class Style {

    public Position position      = Position.CONTAINER;
    public Overflow overflow      = Overflow.TRIM;
    public float    marginTop     = 0;
    public float    marginBottom  = 0;
    public float    marginLeft    = 0;
    public float    marginRight   = 0;
    public float    paddingTop    = 0;
    public float    paddingBottom = 0;
    public float    paddingLeft   = 0;
    public float    paddingRight  = 0;

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
