package com.heavybox.jtix.ecs;

public class ComponentTransform2D implements ComponentTransform {

    // TODO
    private ComponentTransform2D parent;
    private ComponentTransform2D world;

    public float x       = 0;
    public float y       = 0;
    public float degrees = 0;
    public float sclX    = 1;
    public float sclY    = 1;

    public ComponentTransform2D() { }

    public ComponentTransform2D(float x, float y, float degrees, float sclX, float sclY) {
        this.x = x;
        this.y = y;
        this.degrees = degrees;
        this.sclX = sclX;
        this.sclY = sclY;
    }

    public ComponentTransform2D(float x, float y, float degrees) {
        this(x, y, degrees,1,1);
    }

    @Override
    public float getPositionX() {
        return x;
    }

    @Override
    public float getPositionY() {
        return y;
    }

    @Override
    public float getPositionZ() {
        return 0;
    }

    @Override
    public ComponentTransform2D getWorld() {
        return world == null ? this : world;
    }

}
