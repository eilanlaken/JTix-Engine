package com.heavybox.jtix.input_2;

public final class Input {

    public static final Keyboard keyboard = new Keyboard();
    public static final Mouse    mouse    = new Mouse();

    private Input() {}

    public static void update() {
        keyboard.update();
        mouse.update();
    }

}
