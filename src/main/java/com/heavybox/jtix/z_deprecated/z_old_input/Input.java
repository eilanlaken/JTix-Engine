package com.heavybox.jtix.z_deprecated.z_old_input;

import com.heavybox.jtix.application.Application;

public class Input {

    private static Application application = null;
    private static boolean     initialized = false;

    //public static final Mouse mouse = new Mouse();
    //public static final Keyboard keyboard = new Keyboard();

    public static void init(final Application application) {
        if (initialized) return;
        Input.application = application;
        Mouse.init(application);
        Keyboard.init();
        initialized = true;
    }

    // TODO
    public void update() {

    }


}
