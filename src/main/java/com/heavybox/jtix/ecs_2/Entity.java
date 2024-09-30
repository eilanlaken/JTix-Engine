package com.heavybox.jtix.ecs_2;

public abstract class Entity {

    protected EntityContainer container;
    protected int handle;
    protected int bitmask;

    protected Entity(float x, float y, float z,
                     float degX, float degY, float degZ,
                     float sclX, float sclY, float sclZ) {

    }

    protected abstract ComponentAudio    createComponentAudio();
    protected abstract ComponentGraphics createComponentGraphics();
    protected abstract ComponentPhysics  createComponentPhysics();
    protected abstract ComponentScripts  createComponentScripts();

}
