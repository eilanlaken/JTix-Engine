package com.heavybox.jtix.z_old_application;

public final class ApplicationUtils {

    private static boolean initialized = false;
    private static ApplicationWindow window;

    private ApplicationUtils() {}

    public static void init(ApplicationWindow window) {
        if (initialized) return;
        ApplicationUtils.window = window;
        initialized = true;
    }

    public static void windowClose() {
        window.close();
    }

    public static void windowMinimize() {
        window.minimize();
    }

    public static void windowMaximize() {
        window.maximize();
    }

    public static void windowFocus() {
        window.focus();
    }

    public static void windowRestore() {
        window.restore();
    }

    public static void windowFlash() {
        window.flash();
    }

    public static void windowSetIcon(final String path) {
        window.setIcon(path);
    }

    public static void windowSetTitle(final String title) {
        window.setTitle(title);
    }

}
