package com.heavybox.jtix.gui;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Texture;

public class Style {

    // style - let's start with styling a round button
    public float      x               = 0;
    public float      y               = 0;
    public float      deg             = 0;
    public float      sclX            = 1;
    public float      sclY            = 1;
    public Position   position        = Position.IMPLICIT;
    public Size       size            = Size.FLEXIBLE;
    public Color      backgroundColor = Color.valueOf("#202123");
    public Texture    backgroundImage = null;
    public Color      textColor       = Color.valueOf("#ECECEC");
    public TextLayout textLayout      = TextLayout.CENTER;
    public Overflow   overflow        = Overflow.TRUNCATE;
    public Font       font            = null;
    public int        fontSize        = 18;
    public float      paddingTop      = 0;
    public float      paddingBottom   = 0;
    public float      paddingLeft     = 0;
    public float      paddingRight    = 0;

    public float borderRadiusTopLeft = 0;
    public float borderRadiusTopRight = 0;
    public float borderRadiusBottomRight = 0;
    public float borderRadiusBottomLeft = 0;

    public int borderRefinementTopLeft = 20;
    public int borderRefinementTopRight = 20;
    public int borderRefinementBottomRight = 20;
    public int borderRefinementBottomLeft = 20;

    public float transitionSeconds = 0;

    public float[] shape = null;

    // width and height - ?
    public float width;
    public float height;

    public enum Overflow {
        DO_NOTHING,
        TRUNCATE,
    }

    public enum TextLayout {
        CENTER,
        LEFT,
        RIGHT,
        NEWSPAPER,
    }

    public enum Position {
        IMPLICIT,  // positioned by its container layout, ignoring x, y, deg, scaleX and scaleY. If the container is null, then behaves like EXPLICIT
        EXPLICIT,  // positioned x, y, deg relative to its container's center
    }

    public enum Size {
        FIXED,
        FLEXIBLE,
    }

}
