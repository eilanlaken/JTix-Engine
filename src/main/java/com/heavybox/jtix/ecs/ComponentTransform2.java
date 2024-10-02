package com.heavybox.jtix.ecs;

import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Quaternion;
import com.heavybox.jtix.math.Vector3;
import org.jetbrains.annotations.NotNull;

@Deprecated public class ComponentTransform2 implements Component {

    public final Matrix4x4 matrix;

    public ComponentTransform2() {
        this(0,0,0,0,0,0,1,1,1);
    }

    protected ComponentTransform2(float x, float y, float z, float degX, float degY, float degZ, float sclX, float sclY, float sclZ) {
        matrix = new Matrix4x4();
        Quaternion rotation = new Quaternion().setEulerAnglesDeg(degX, degY, degZ);
        matrix.setToTranslationRotationScale(x, y, z, rotation.x, rotation.y, rotation.z, rotation.w, sclX, sclY, sclZ);
    }

    public Matrix4x4 set(float x, float y, float z, float degX, float degY, float degZ, float sclX, float sclY, float sclZ) {
        return matrix.setToTranslationEulerRotationDegScale(x, y, z, degX, degY, degZ, sclX, sclY, sclZ);
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
