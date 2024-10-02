package com.heavybox.jtix.ecs;

import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Quaternion;
import com.heavybox.jtix.math.Vector3;

public class ComponentTransform3 extends Matrix4x4 implements Component {

    public ComponentTransform3() {
        super();
    }

    protected ComponentTransform3(float x, float y, float z, float degX, float degY, float degZ, float sclX, float sclY, float sclZ) {
        Quaternion rotation = new Quaternion().setEulerAnglesDeg(degX, degY, degZ);
        setToTranslationRotationScale(x, y, z, rotation.x, rotation.y, rotation.z, rotation.w, sclX, sclY, sclZ);
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
