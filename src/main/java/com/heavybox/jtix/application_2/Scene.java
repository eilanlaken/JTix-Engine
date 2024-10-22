package com.heavybox.jtix.application_2;

public interface Scene {

    default void prepare() {}
    void start();
    void frameUpdate();
    void hide();

    void resize(int width, int height); // TODO: see maybe there's a better way.

}
