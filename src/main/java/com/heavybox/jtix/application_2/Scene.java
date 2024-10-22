package com.heavybox.jtix.application_2;

public interface Scene {

    void beforeStart();
    void start();
    void frameUpdate();
    void finish();

    void resize(int width, int height); // TODO: see maybe there's a better way. Handle using window callbacks and graphics.

}
