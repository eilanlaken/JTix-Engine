package com.heavybox.jtix.scene;

import com.heavybox.jtix.application.ApplicationWindow;

public final class SceneUtils {

    private static ApplicationWindow window      = null;
    private static boolean           initialized = false;

    private SceneUtils() {}

    public static void init(final ApplicationWindow window) {
        if (initialized) return;
        SceneUtils.window = window;
        SceneManager.window = window;
        initialized = true;
    }



}
