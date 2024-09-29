package com.heavybox.jtix.ecs_2;

import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Quaternion;
import com.heavybox.jtix.math.Vector3;
import org.jetbrains.annotations.NotNull;

public class ComponentTransform extends Component {

    //public  static final Component.Type TYPE = Component.Type.TRANSFORM;
    private static final Vector3 position = new Vector3();
    private static final Quaternion rotation = new Quaternion();
    private static final Vector3    scale    = new Vector3();

    /* can be null */
    public ComponentTransform parent = null;

    /* The local values of the transform */
    public boolean valuesUpdated;
    public float   x, y, z;
    public float   angleXDeg, angleYDeg, angleZDeg;
    public float   scaleX, scaleY, scaleZ;

    public boolean   matrixUpdated;
    public Matrix4x4 matrix;

    protected ComponentTransform(float scaleZ, float scaleY, float scaleX, float angleZDeg, float angleYDeg, float angleXDeg, float z, float y, float x) {
        //super(type);
        this.scaleZ = scaleZ;
        this.scaleY = scaleY;
        this.scaleX = scaleX;
        this.angleZDeg = angleZDeg;
        this.angleYDeg = angleYDeg;
        this.angleXDeg = angleXDeg;
        this.z = z;
        this.y = y;
        this.x = x;
        valuesUpdated = true;
        matrixUpdated = false;
    }

    // TODO: remove
    public ComponentTransform() {
        //super(TYPE);
        this.scaleZ = 1;
        this.scaleY = 1;
        this.scaleX = 1;
        this.angleZDeg = 0;
        this.angleYDeg = 0;
        this.angleXDeg = 0;
        this.z = 0;
        this.y = 0;
        this.x = 0;
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

        this.angleXDeg = rotation.getPitchDeg();
        this.angleYDeg = rotation.getYawDeg();
        this.angleZDeg = rotation.getRollDeg();

        this.scaleX = scale.x;
        this.scaleY = scale.y;
        this.scaleZ = scale.z;

        valuesUpdated = true;
    }

    // TODO: optimize by combining the rotations.
    private void updateMatrix() {
        if (matrix == null) matrix = new Matrix4x4();
        rotation.setEulerAnglesDeg(angleXDeg, angleYDeg, angleZDeg);
        matrix.setToTranslationRotationScale(
                x, y, z,
                rotation.x, rotation.y, rotation.z, rotation.w,
                scaleX, scaleY, scaleZ
        );
        matrixUpdated = true;
    }

    public void matrix(@NotNull final Matrix4x4 other) {
        matrix.set(other);
        valuesUpdated = false;
        matrixUpdated = true;
    }

    public Matrix4x4 matrix() {
        if (!matrixUpdated) updateMatrix();
        return matrix;
    }

}
