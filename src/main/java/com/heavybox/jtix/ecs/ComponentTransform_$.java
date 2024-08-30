package com.heavybox.jtix.ecs;

import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;

public class ComponentTransform_$ extends Component {

    public static final Type TYPE = Type.TRANSFORM;

    /* can be null */
    public ComponentTransform_$ parent;

    /* The local values of the transform */
    public boolean valuesUpdated;
    public float x, y, z;
    public float angleXDeg, angleYDeg, angleZDeg;
    public float scaleX, scaleY, scaleZ;

    public boolean   matrixUpdated;
    public Matrix4x4 matrix;

    // TODO: change to protected
    public ComponentTransform_$(float x, float y, float z, float angleXDeg, float angleYDeg, float angleZDeg, float scaleX, float scaleY, float scaleZ) {
        super(TYPE);
        this.x = x;
        this.y = y;
        this.z = z;
        this.angleXDeg = angleXDeg;
        this.angleYDeg = angleYDeg;
        this.angleZDeg = angleZDeg;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
    }

    protected Matrix4x4 local() {
        if (matrix == null) matrix = new Matrix4x4();

        matrix.setToScaling(scaleX, scaleY, scaleZ);
        matrix.rotateRad(Vector3.X_UNIT, angleXDeg * MathUtils.degreesToRadians);
        matrix.rotateRad(Vector3.Y_UNIT, angleYDeg * MathUtils.degreesToRadians);
        matrix.rotateRad(Vector3.Z_UNIT, angleZDeg * MathUtils.degreesToRadians);
        matrix.translateXYZAxis(x,y,z);

        matrixUpdated = true;
        return matrix;
    }

    public void translate(float dx, float dy, float dz, boolean worldSpace) {

    }

    public void rotateXDeg(float da, boolean worldSpace) {

    }

    public void scale() {

    }



}
