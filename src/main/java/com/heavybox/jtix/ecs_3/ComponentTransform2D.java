package com.heavybox.jtix.ecs_3;

public class ComponentTransform2D implements ComponentTransform {

    public float x     = 0;
    public float y     = 0;
    public float angle = 0;
    public float sclX  = 1;
    public float sclY  = 1;

    public ComponentTransform2D(float x, float y, float angle, float sclX, float sclY) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.sclX = sclX;
        this.sclY = sclY;
    }

    public ComponentTransform2D(float x, float y, float z, float angle) {
        this(x, y, angle, 1, 1);
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
}
