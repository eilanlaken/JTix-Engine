package com.heavybox.jtix.ecs;

import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import org.jetbrains.annotations.NotNull;

public class ComponentTransform extends Component {

    public static final Type TYPE = Type.TRANSFORM;

    /* can be null */
    public ComponentTransform parent = null;

    /* The local values of the transform */
    public boolean valuesUpdated;
    public float   x, y, z;
    public float   angleXDeg, angleYDeg, angleZDeg;
    public float   scaleX, scaleY, scaleZ;

    public boolean   matrixUpdated;
    public Matrix4x4 matrix;

    protected ComponentTransform(@NotNull Type type, float scaleZ, float scaleY, float scaleX, float angleZDeg, float angleYDeg, float angleXDeg, float z, float y, float x) {
        super(type);
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
        super(TYPE);
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

        valuesUpdated = true;
    }

    // TODO: optimize by combining the rotations.
    private void updateMatrix() {
        if (matrix == null) matrix = new Matrix4x4();
        matrix.setToScaling(scaleX, scaleY, scaleZ);
        matrix.rotateRad(Vector3.X_UNIT, angleXDeg * MathUtils.degreesToRadians);
        matrix.rotateRad(Vector3.Y_UNIT, angleYDeg * MathUtils.degreesToRadians);
        matrix.rotateRad(Vector3.Z_UNIT, angleZDeg * MathUtils.degreesToRadians);
        matrix.translateXYZAxis(x, y, z);
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
