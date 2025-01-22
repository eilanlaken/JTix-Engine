package com.heavybox.jtix.z_deprecated.z_ecs_old_1;

import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Matrix4x4;

public class ComponentTransform extends Matrix4x4 implements Component {

    public ComponentTransform() {
        super();
    }

    public ComponentTransform(final ComponentTransform other) {
        super(other);
    }

    public ComponentTransform(float x, float y, float z, float degX, float degY, float degZ, float sclX, float sclY, float sclZ) {
        setToPositionEulerScaling(x, y, z, degX, degY, degZ, sclX, sclY, sclZ);
    }

    /* intended to be used by the physics 2d system */
    protected Matrix4x4 setToTransform2D(float x, float y, float degZ) {
        float cosTheta = MathUtils.cosDeg(degZ);
        float sinTheta = MathUtils.sinDeg(degZ);
        val[M00] = cosTheta;
        val[M01] = sinTheta;
        val[M03] = x;
        val[M10] = -sinTheta;
        val[M11] = cosTheta;
        val[M13] = y;
        return this;
    }

    @Override
    public final int getBitmask() {
        return Type.TRANSFORM.bitmask;
    }

}
