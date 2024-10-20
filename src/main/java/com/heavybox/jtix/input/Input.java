package com.heavybox.jtix.input;

import com.heavybox.jtix.application_2.Application;

public class Input {

    private static Application application = null;
    private static boolean     initialized = false;

    //public static final Mouse mouse = new Mouse();
    //public static final Keyboard keyboard = new Keyboard();

    public static void init(final Application application) {
        if (initialized) return;
        Input.application = application;
        Mouse.init(application);
        Keyboard.init(application);
        initialized = true;
    }

    // TODO
    public void update() {

    }


}
