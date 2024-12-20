package com.heavybox.jtix.gui;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Texture;

public class Style {

    // style - let's start with styling a round button
    public float x = 0;
    public float y = 0;
    public float deg = 0;
    public float sclX = 1;
    public float sclY = 1;
    public Position position = Position.IMPLICIT;
    public Color backgroundColor = Color.CLEAR;
    public Texture backgroundImage = null;
    public Color textColor = Color.BLACK;
    public TextLayout textLayout = TextLayout.CENTER;
    public Overflow overflow;
    public Font font = null;
    public int fontSize = 18;
    public float padding = 0;
    public float margin = 0;
    public int borderSize = 0;
    public int borderRadius = 0;
    public float transitionSeconds = 0;

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
