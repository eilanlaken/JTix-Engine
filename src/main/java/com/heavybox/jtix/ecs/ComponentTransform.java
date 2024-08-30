package com.heavybox.jtix.ecs;

import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Quaternion;
import com.heavybox.jtix.math.Vector3;

public class ComponentTransform extends Component {

    public static final Type TYPE = Type.TRANSFORM;
    public static final Vector3    position = new Vector3();
    public static final Quaternion rotation = new Quaternion();
    public static final Vector3    scale    = new Vector3();

    /* can be null */
    public ComponentTransform parent;

    /* The local values of the transform */
    public boolean valuesUpdated;
    public float x, y, z;
    public float angleXDeg, angleYDeg, angleZDeg;
    public float scaleX, scaleY, scaleZ;

    public boolean   matrixUpdated;
    public Matrix4x4 matrix;

    // TODO: change to protected
    public ComponentTransform() {
        super(TYPE);
        matrix = new Matrix4x4();
        updateValues();
    }

    private void updateValues() {
        matrix.getTranslation(position);
        x = position.x;
        y = position.y;
        z = position.z;

        matrix.getRotation(rotation);
        angleXDeg = rotation.getPitch();
        angleYDeg = rotation.getYaw();
        angleZDeg = rotation.getRoll();

        matrix.getScale(scale);
        scaleX = scale.x;
        scaleY = scale.y;
        scaleZ = scale.z;

        valuesUpdated = true;
    }

    private void updateMatrix() {

        matrixUpdated = true;
    }

    public void translate(float dx, float dy, float dz, boolean worldSpace) {

    }

    public void rotateXDeg(float da, boolean worldSpace) {
        matrix.rotateRad(Vector3.X_UNIT, da * MathUtils.degreesToRadians);
        updateValues();
    }

    public void scale() {

    }



}
