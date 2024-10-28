package com.heavybox.jtix.application_3;

import com.heavybox.jtix.collections.Array;

public interface Scene {

    /* Scene life-cycle: setup() -> start() -> update()...[repeat] -> finish() */
    void setup();
    void start();
    void update();
    void finish();

    /* GLFW Window callbacks. */
    default void windowResized(int width, int height) {}
    default void windowFocused(boolean focus) {}
    default void windowMinimized(boolean minimized) {}
    default void windowMaximized(boolean maximized) {}
    default void windowFilesDraggedAndDropped(Array<String> filePaths) {}

}
