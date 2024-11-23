package com.heavybox.jtix.graphics;

import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;

public class Camera {

    /* projection */
    public Mode      mode;
    public Matrix4x4 projection;
    public Matrix4x4 view;
    public Matrix4x4 combined;
    public Matrix4x4 invProjectionView;
    public float     near;
    public float     far;
    public float     fov;
    public float     zoom;
    public float     viewportWidth;
    public float     viewportHeight;

    /* frustum */
    private final Vector3[] frustumCorners;
    private final Vector3[] frustumNormals;
    private final float[]   frustumPlaneDs;

    public Camera(Mode mode, float viewportWidth, float viewportHeight, float zoom, float near, float far, float fov) {
        this.mode = mode;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.zoom = zoom;
        this.near = near;
        this.far = far;
        this.fov = fov;
        this.projection = new Matrix4x4();
        this.view = new Matrix4x4();
        this.combined = new Matrix4x4();
        this.invProjectionView = new Matrix4x4();

        this.frustumCorners = new Vector3[8];
        for (int i = 0; i < 8; i++) {
            this.frustumCorners[i] = new Vector3();
        }
        this.frustumNormals = new Vector3[6];
        for (int i = 0; i < 6; i++) {
            this.frustumNormals[i] = new Vector3();
        }
        this.frustumPlaneDs = new float[6];
    }

    public void update(Vector3 position, Vector3 direction, Vector3 up) {
        switch (mode) {
            case ORTHOGRAPHIC: // TODO: probably better to use projection.setToOrthographicProjection(zoom * viewportWidth, zoom * viewportHeight)
                projection.setToOrthographicProjection(zoom * -viewportWidth / 2.0f, zoom * (viewportWidth / 2.0f), zoom * -(viewportHeight / 2.0f), zoom * viewportHeight / 2.0f, 0, far);
                break;
            case PERSPECTIVE: // TODO: consider zoom here: fov -> fov / zoom?
                this.projection.setToPerspectiveProjection(Math.abs(near), Math.abs(far), fov, viewportWidth / viewportHeight);
                break;
        }

        view.setToLookAt(
                position.x, position.y, position.z,
                position.x + direction.x, position.y + direction.y, position.z + direction.z,
                up.x, up.y, up.z
        ); // set to look at: position, position + direction, up
        combined.set(projection);
        Matrix4x4.mul(combined.val, view.val);
        invProjectionView.set(combined);
        Matrix4x4.inv(invProjectionView.val);

        /* Update frustum corners by taking the canonical cube and un-projecting it. */
        /* The canonical cube is a cube, centered at the origin, with 8 corners: (+-1, +-1, +-1). Also known as OpenGL "clipping volume".*/
        frustumCorners[0].set(-1,-1,-1).prj(invProjectionView);
        frustumCorners[1].set( 1,-1,-1).prj(invProjectionView);
        frustumCorners[2].set( 1, 1,-1).prj(invProjectionView);
        frustumCorners[3].set(-1, 1,-1).prj(invProjectionView);
        frustumCorners[4].set(-1,-1, 1).prj(invProjectionView);
        frustumCorners[5].set( 1,-1, 1).prj(invProjectionView);
        frustumCorners[6].set( 1, 1, 1).prj(invProjectionView);
        frustumCorners[7].set(-1, 1, 1).prj(invProjectionView);

        /* Update the frustum's clipping plane normal and d values. */
        frustumSetClippingPlane(0, frustumCorners[1], frustumCorners[0], frustumCorners[2]); // near
        frustumSetClippingPlane(1, frustumCorners[4], frustumCorners[5], frustumCorners[7]); // far
        frustumSetClippingPlane(2, frustumCorners[0], frustumCorners[4], frustumCorners[3]); // left
        frustumSetClippingPlane(3, frustumCorners[5], frustumCorners[1], frustumCorners[6]); // right
        frustumSetClippingPlane(4, frustumCorners[2], frustumCorners[3], frustumCorners[6]); // top
        frustumSetClippingPlane(5, frustumCorners[4], frustumCorners[0], frustumCorners[1]); // bottom
    }

    private void frustumSetClippingPlane(int i, Vector3 point1, Vector3 point2, Vector3 point3) {
        this.frustumNormals[i].set(point1).sub(point2).crs(point2.x - point3.x, point2.y - point3.y, point2.z - point3.z).nor();
        this.frustumPlaneDs[i] = -1 * Vector3.dot(point1, this.frustumNormals[i]);
    }

    public void unProject(Vector3 screenCoordinates) {
        unProject(0, 0, Graphics.getWindowWidth(), Graphics.getWindowHeight(), screenCoordinates);
    }

    public boolean frustumIntersectsSphere(final Vector3 center, final float r) {
        for (int i = 0; i < 6; i++) {
            float signedDistance = frustumNormals[i].x * center.x + frustumNormals[i].y * center.y + frustumNormals[i].z * center.z + frustumPlaneDs[i];
            float diff = signedDistance + r;
            if (diff < 0) return false;
        }
        return true;
    }

    public void unProject(float viewportX, float viewportY, float viewportWidth, float viewportHeight, Vector3 screenCoordinates) {
        float x = screenCoordinates.x - viewportX, y = Graphics.getWindowHeight() - screenCoordinates.y - viewportY;
        screenCoordinates.x = (2 * x) / viewportWidth - 1;
        screenCoordinates.y = (2 * y) / viewportHeight - 1;
        screenCoordinates.z = 2 * screenCoordinates.z - 1;
        screenCoordinates.prj(invProjectionView);
    }

    public void project(Vector3 worldCoordinates) {
        project(0, 0, Graphics.getWindowWidth(), Graphics.getWindowHeight(), worldCoordinates);
    }

    public void project(float viewportX, float viewportY, float viewportWidth, float viewportHeight, Vector3 worldCoordinates) {
        worldCoordinates.prj(combined);
        worldCoordinates.x = viewportWidth * (worldCoordinates.x + 1) / 2 + viewportX;
        worldCoordinates.y = viewportHeight * (worldCoordinates.y + 1) / 2 + viewportY;
        worldCoordinates.z = (worldCoordinates.z + 1) / 2;
    }

    public float getViewportWidth() {
        return viewportWidth;
    }

    public float getViewportHeight() {
        return viewportHeight;
    }

    public enum Mode {

        ORTHOGRAPHIC,
        PERSPECTIVE,
        ;

    }
}
