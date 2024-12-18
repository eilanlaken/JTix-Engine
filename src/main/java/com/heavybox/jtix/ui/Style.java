package com.heavybox.jtix.ui;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;

public class Style {

    // style - let's start with styling a round button
    public Color colorBackground;
    public Color colorText;
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
        TRUNCATE_WITH_SCROLLBAR
    }

}
