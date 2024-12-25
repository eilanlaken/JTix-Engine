package com.heavybox.jtix.z_old_ui;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Texture;

public class Style {

    // style - let's start with styling a round button
    public Color backgroundColor;
    public Texture backgroundImage;
    public Color textColor;
    public Overflow overflow;
    public Font font;
    public int fontSize = 18;
    public float padding;
    public float margin;
    public int border;
    public int borderRadius;
    public float transitionSeconds;

    public enum Overflow {
        DO_NOTHING,
        RESIZE_TO_FIT,
        TRUNCATE,
    }

}
