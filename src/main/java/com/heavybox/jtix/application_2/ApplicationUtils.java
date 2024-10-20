package com.heavybox.jtix.application_2;

import com.heavybox.jtix.application.ApplicationWindow;

@Deprecated public final class ApplicationUtils {

    private static boolean initialized = false;
    @Deprecated private static ApplicationWindow window;
    private static Application application;

    private ApplicationUtils() {}

    @Deprecated public static void init(ApplicationWindow window) {
        if (initialized) return;
        ApplicationUtils.window = window;
        initialized = true;
    }

    public static void init(final Application application) {
        if (initialized) return;
        ApplicationUtils.application = application;
        initialized = true;
    }

    // TODO
//    public static void windowClose() {
//        window.close();
//    }
//
//    public static void windowMinimize() {
//        window.minimize();
//    }
//
//    public static void windowMaximize() {
//        window.maximize();
//    }
//
//    public static void windowFocus() {
//        window.focus();
//    }
//
//    public static void windowRestore() {
//        window.restore();
//    }
//
//    public static void windowFlash() {
//        window.flash();
//    }
//
//    public static void windowSetIcon(final String path) {
//        window.setIcon(path);
//    }
//
//    public static void windowSetTitle(final String title) {
//        window.setTitle(title);
//    }

}
