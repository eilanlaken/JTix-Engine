package com.heavybox.jtix.application_2;

public interface Scene {

    /* Scene life-cycle: setup() -> start() -> update()...[repeat] -> finish() */
    void setup();
    void start();
    void update();
    void finish();

    @Deprecated void resize(int width, int height); // TODO: there's a better way. Handle using window callbacks and Graphics.

}
