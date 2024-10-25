package com.heavybox.jtix.application_2;

public interface Scene {

    /* Scene life-cycle: setup() -> start() -> update()...[repeat] -> finish() */
    void setup();
    void start();
    void update();
    void finish();

    /* GLFW Window callbacks. */
    default void windowResize(int width, int height) {}
    default void windowFocusChanged(boolean focus) {}
    default void windowMinimizedChanged(boolean minimized) {}

}
