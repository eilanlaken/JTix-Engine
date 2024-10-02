package com.heavybox.jtix.ecs;

import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Quaternion;
import com.heavybox.jtix.math.Vector3;
import org.jetbrains.annotations.NotNull;

@Deprecated public class ComponentTransform implements Component {

    private static final Vector3    position = new Vector3();
    private static final Quaternion rotation = new Quaternion();
    private static final Vector3    scale    = new Vector3();

    /* can be null */
    public ComponentTransform parent = null;

    /* The local values of the transform */
    public boolean valuesUpdated;
    public float   x, y, z;
    public float   degX, degY, degZ;
    public float   sclX, sclY, sclZ;

    public boolean   matrixUpdated;
    public Matrix4x4 matrix;

    public ComponentTransform() {
        this(0,0,0,0,0,0,1,1,1);
    }

    protected ComponentTransform(float x, float y, float z, float degX, float degY, float degZ, float sclX, float sclY, float sclZ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.degX = degX;
        this.degY = degY;
        this.degZ = degZ;
        this.sclX = sclX;
        this.sclY = sclY;
        this.sclZ = sclZ;
        valuesUpdated = true;
        matrixUpdated = false;
    }

    private void updateValues() {
        matrix.getTranslation(position);
        matrix.getRotation(rotation);
        matrix.getScale(scale);

        this.x = position.x;
        this.y = position.y;
        this.z = position.z;

        this.degX = rotation.getPitchDeg();
        this.degY = rotation.getYawDeg();
        this.degZ = rotation.getRollDeg();

        this.sclX = scale.x;
        this.sclY = scale.y;
        this.sclZ = scale.z;

        valuesUpdated = true;
    }

    // TODO
    private void updateMatrix() {
        if (matrix == null) matrix = new Matrix4x4();

        rotation.setEulerAnglesDeg(degX, degY, degZ);
        matrix.setToTranslationRotationScale(x, y, z, rotation.x, rotation.y, rotation.z, rotation.w, sclX, sclY, sclZ);

//        matrix.idt();
//        matrix.scale(sclX, sclY, sclZ);
//        matrix.rotateRad(Vector3.X_UNIT, degX * MathUtils.degreesToRadians);
//        matrix.rotateRad(Vector3.Y_UNIT, degY * MathUtils.degreesToRadians);
//        matrix.rotateRad(Vector3.Z_UNIT, degZ * MathUtils.degreesToRadians);
//        matrix.translateXYZAxis(x,y,z);
//
//        matrix.idt();
//        matrix.scale(sclX, sclY, sclZ);
//        matrix.rotateSelfAxis(Vector3.X_UNIT, degX);
//        matrix.rotateSelfAxis(Vector3.Y_UNIT, degY);
//        matrix.rotateSelfAxis(Vector3.Z_UNIT, degZ);
//        matrix.translateXYZAxis(x,y,z);

        matrixUpdated = true;
    }

    public void matrix(@NotNull final Matrix4x4 other) {
        matrix.set(other);
        valuesUpdated = false;
        matrixUpdated = true;
    }

    // TODO
    public Matrix4x4 matrix() {
        //if (!matrixUpdated) updateMatrix();
        updateMatrix();
        return matrix;
    }

    @Override
    public final int getBitmask() {
        return Type.TRANSFORM.bitmask;
    }

}
