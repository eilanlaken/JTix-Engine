package com.heavybox.jtix.z_ecs_old_2;

import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Quaternion;
import com.heavybox.jtix.math.Vector3;

public class ComponentCamera implements Component {

    private final Vector3 tmp       = new Vector3();
    public  final Vector3 position  = new Vector3(0,0,0);
    public  final Vector3 direction = new Vector3(0,0,-1);
    public  final Vector3 up        = new Vector3(0,1,0);
    public  final Vector3 left      = new Vector3();

    public  final Camera  lens;

    public ComponentCamera(float viewportWidth, float viewportHeight, float zoom, float near, float far, float fov) {
        this.lens = new Camera(Camera.Mode.ORTHOGRAPHIC, viewportWidth, viewportHeight, zoom, near, far, fov);
        update();
    }

    public ComponentCamera(float viewportWidth, float viewportHeight, float zoom) {
        this(viewportWidth, viewportHeight, zoom, 0.1f, 100, 70);
    }

    public ComponentCamera update() {
        left.set(up).crs(direction);
        lens.update(position, direction, up);
        return this;
    }

    public ComponentCamera update(float viewportWidth, float viewportHeight) {
        lens.viewportWidth  = viewportWidth;
        lens.viewportHeight = viewportHeight;
        return update();
    }

    public void setModeOrthographic() {
        lens.mode = Camera.Mode.ORTHOGRAPHIC;
    }

    public void setModePerspective() {
        lens.mode = Camera.Mode.PERSPECTIVE;
    }

    public void lookAt(float x, float y, float z) {
        tmp.set(x, y, z).sub(position).nor();
        if (tmp.isZero()) return;
        float dot = Vector3.dot(tmp, up);
        if (Math.abs(dot - 1) < 0.000000001f) up.set(direction).scl(-1);
        else if (Math.abs(dot + 1) < 0.000000001f) up.set(direction);
        direction.set(tmp);
        normalizeUp();
        left.set(up).crs(direction);
    }

    public void normalizeUp() {
        tmp.set(direction).crs(up);
        up.set(tmp).crs(direction).nor();
    }

    public void rotate(float angle, float axisX, float axisY, float axisZ) {
        direction.rotate(angle, axisX, axisY, axisZ);
        up.rotate(angle, axisX, axisY, axisZ);
    }

    public void rotate(Vector3 axis, float angle) {
        direction.rotate(axis, angle);
        up.rotate(axis, angle);
    }

    public void rotate(final Matrix4x4 transform) {
        direction.rot(transform);
        up.rot(transform);
    }

    public void rotate(final Quaternion q) {
        q.transform(direction);
        q.transform(up);
    }

    public void rotateAround(Vector3 point, Vector3 axis, float angle) {
        tmp.set(point);
        tmp.sub(position);
        translate(tmp);
        rotate(axis, angle);
        tmp.rotate(axis, angle);
        translate(-tmp.x, -tmp.y, -tmp.z);
    }

    public void transform(final Matrix4x4 transform) {
        position.mul(transform);
        rotate(transform);
    }

    public void translate(float x, float y, float z) {
        position.add(x, y, z);
    }

    public void translate(Vector3 vec) {
        position.add(vec);
    }

    @Override
    public final int getBitmask() {
        return Type.CAMERA.bitmask;
    }

}
