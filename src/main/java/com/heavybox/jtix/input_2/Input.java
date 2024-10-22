package com.heavybox.jtix.input_2;

import com.heavybox.jtix.application_2.Application;

public class Input {

    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        Mouse.init();
        Keyboard.init();
        initialized = true;
    }

    public static void update() {
        Mouse.update();
        Keyboard.update();
    }

}
