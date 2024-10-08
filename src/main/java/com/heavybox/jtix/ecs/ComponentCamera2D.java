package com.heavybox.jtix.ecs;

import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;

public class ComponentCamera2D implements ComponentCamera {

    private final Vector3 tmp       = new Vector3();
    public  final Vector3 position  = new Vector3(0,0,0);
    public  final Vector3 direction = new Vector3(0,0,-1);
    public  final Vector3 up        = new Vector3(0,1,0);
    public  final Vector3 left      = new Vector3();

    public  final Camera  lens;

    public ComponentCamera2D(float viewportWidth, float viewportHeight, float zoom, float near, float far, float fov) {
        this.lens = new Camera(Camera.Mode.ORTHOGRAPHIC, viewportWidth, viewportHeight, zoom, near, far, fov);
        update();
    }

    public ComponentCamera2D(float viewportWidth, float viewportHeight, float zoom) {
        this(viewportWidth, viewportHeight, zoom, 0.1f, 100, 70);
    }

    @Deprecated public ComponentCamera2D update() {
        left.set(up).crs(direction);
        lens.update(position, direction, up);
        return this;
    }

    @Deprecated public ComponentCamera2D update(float viewportWidth, float viewportHeight) {
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

    public void normalizeUp() {
        tmp.set(direction).crs(up);
        up.set(tmp).crs(direction).nor();
    }

    @Deprecated public void transform(final Matrix4x4 transform) {
        position.mul(transform);
        direction.rot(transform);
        up.rot(transform);
    }

    protected void applyTransform(float x, float y, float degZ) {
        position.set(x, y, 0);
        direction.set(0, 0, -1);
        up.set(0,1,0);
        Vector2.rotateDeg(up, degZ);
        left.set(up).crs(direction);
        lens.update(position, direction, up);
    }

}
