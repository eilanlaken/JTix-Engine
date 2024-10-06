package com.heavybox.jtix.ecs_3;

import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Matrix4x4;

public class ComponentTransform2D implements ComponentTransform {

    public float x     = 0;
    public float y     = 0;
    public float z     = 0;
    public float angle = 0;
    public float sclX  = 1;
    public float sclY  = 1;

    public ComponentTransform2D(float x, float y, float z, float angle, float sclX, float sclY) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.angle = angle;
        this.sclX = sclX;
        this.sclY = sclY;
    }

    public ComponentTransform2D(float x, float y, float z, float angle) {
        this(x, y, z, angle, 1, 1);
    }

}
