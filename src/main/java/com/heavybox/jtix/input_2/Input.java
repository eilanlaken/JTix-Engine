package com.heavybox.jtix.input_2;

public final class Input {

    private static boolean initialized = false;

    private Input() {}

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
