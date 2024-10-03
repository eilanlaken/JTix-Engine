package com.heavybox.jtix.ecs;

import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Quaternion;

@Deprecated public class ComponentTransform_old_2 implements Component {

    public final Matrix4x4 matrix;

    public ComponentTransform_old_2() {
        this(0,0,0,0,0,0,1,1,1);
    }

    protected ComponentTransform_old_2(float x, float y, float z, float degX, float degY, float degZ, float sclX, float sclY, float sclZ) {
        matrix = new Matrix4x4();
        Quaternion rotation = new Quaternion().setEulerAnglesDeg(degX, degY, degZ);
        matrix.setToPositionRotationScaling(x, y, z, rotation.x, rotation.y, rotation.z, rotation.w, sclX, sclY, sclZ);
    }

    public Matrix4x4 set(float x, float y, float z, float degX, float degY, float degZ, float sclX, float sclY, float sclZ) {
        return null;
    }

    // TODO; implement
    protected Matrix4x4 setTo2DTransform(float x, float y, float deg) {

        return matrix;
    }

    @Override
    public final int getBitmask() {
        return Type.TRANSFORM.bitmask;
    }

}
