package com.heavybox.jtix.ecs;

import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Quaternion;
import com.heavybox.jtix.math.Vector3;
import org.jetbrains.annotations.NotNull;

public class ComponentTransform_2 extends Component {

    public static final Type TYPE = Type.TRANSFORM;

    /* can be null */
    public ComponentTransform_2 parent;

    /* The local values of the transform */
    public float x, y, z;
    public float angleX, angleY, angleZ;
    public float scaleX, scaleY, scaleZ;

    /* The world values of the transform. These are the values that are used by other systems. Calculated every frame - either by parent or by physics 2D / 3D. */
    public float   worldX, worldY, worldZ;
    public float   worldAngleX, worldAngleY, worldAngleZ;
    public float   worldScaleX, worldScaleY, worldScaleZ;

    public boolean   updated;
    public Matrix4x4 local;
    public Matrix4x4 world;

    // TODO: change to protected
    public ComponentTransform_2(float x, float y, float z, float angleX, float angleY, float angleZ, float scaleX, float scaleY, float scaleZ) {
        super(TYPE);
        this.x = x;
        this.y = y;
        this.z = z;
        this.angleX = angleX;
        this.angleY = angleY;
        this.angleZ = angleZ;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;

        this.local = new Matrix4x4(new Vector3(x, y, z), new Quaternion().setEulerAnglesDeg(angleY, angleX, angleZ), new Vector3(scaleX, scaleY, scaleZ));

    }

    protected void update() {
        if (parent != null) {
            parent.update();

        } else {
            this.worldX = x;
            this.worldY = y;
            this.worldZ = z;
            this.worldAngleX = angleX;
            this.worldAngleY = angleY;
            this.worldAngleZ = angleZ;
            this.worldScaleX = scaleX;
            this.worldScaleY = scaleY;
            this.worldScaleZ = scaleZ;
        }
        updated = true;
    }

    protected void setToTransform(@NotNull Matrix4x4 transform) {
        Vector3    position = new Vector3();
        Quaternion rotation = new Quaternion();
        Vector3    scale    = new Vector3();

        transform.getRotation(rotation);
        transform.getScale(scale);
        transform.getTranslation(position);

        if (this.world == null) this.world = new Matrix4x4();
        this.world.set(transform);

        this.worldScaleX = scale.x;
        this.worldScaleY = scale.y;
        this.worldScaleZ = scale.z;

        this.worldAngleX = rotation.getAngleAround(Vector3.X_UNIT);
        this.worldAngleY = rotation.getAngleAround(Vector3.Y_UNIT);
        this.worldAngleZ = rotation.getAngleAround(Vector3.Z_UNIT);

        this.worldX = position.x;
        this.worldY = position.y;
        this.worldZ = position.z;

        this.updated = true;
    }

    public Matrix4x4 world() {
        if (!updated) update();
        if (this.world == null) this.world = new Matrix4x4();
        Quaternion r = new Quaternion().setEulerAnglesDeg(worldAngleY, worldAngleX, worldAngleZ);
        return world.setToTranslationRotationScale(worldX, worldY, worldZ, r.x, r.y, r.z, r.w, worldScaleX, worldScaleY, worldScaleZ);
    }

    public void translate(float x, float y, float z, boolean worldSpace) {
        updated = false;
        if (!worldSpace) {

        } else {
            local.translateXYZAxis(x,y,z);
        }
    }

    public void rotate() {
        updated = false;

    }

    public void scale() {
        updated = false;

    }



}
