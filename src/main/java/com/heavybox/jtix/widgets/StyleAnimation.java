package com.heavybox.jtix.widgets;

/*
Animations are like components.
You can add a number of animations to a widget,
and they will all affect the style, independently.
You can choose a transition (interpolation) function
from an enum.
Can choose a play mod (ONCE, LOOP, PING_PING, RANDOM, etc.).
 */
public class StyleAnimation {

    public float durationSeconds;
    public boolean reverse;

    public enum PlayMode {
        ONCE,
        LOOP,
        PING_PONG,
        RANDOM
    }

}
