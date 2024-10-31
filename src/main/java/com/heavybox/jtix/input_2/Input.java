package com.heavybox.jtix.input_2;

public final class Input {

    public static final InputKeyboard keyboard = new InputKeyboard();
    public static final InputMouse    mouse    = new InputMouse();

    private Input() {}

    public static void update() {
        keyboard.update();
        mouse.update();
    }

}
