package com.heavybox.jtix.graphics;

import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;

/**
 * A pyramid with its top sliced off. Used by a camera.
 *      ___
 *    /    \
 *  /_______\
 *  (this, but in 3D)
 *
 */
@Deprecated public class CameraLensFrustum { // TODO: make part of CameraLens (that in turn will be camera).

    private final Vector3[] frustumCorners;
    private final Vector3[] frustumNormals;
    private final float[]   frustumPlaneDs;

    CameraLensFrustum() {
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

    public void update(final Matrix4x4 invPrjView) {
        /* Update frustum corners by taking the canonical cube and un-projecting it. */
        /* The canonical cube is a cube, centered at the origin, with 8 corners: (+-1, +-1, +-1). Also known as OpenGL "clipping volume".*/
        frustumCorners[0].set(-1,-1,-1).prj(invPrjView);
        frustumCorners[1].set( 1,-1,-1).prj(invPrjView);
        frustumCorners[2].set( 1, 1,-1).prj(invPrjView);
        frustumCorners[3].set(-1, 1,-1).prj(invPrjView);
        frustumCorners[4].set(-1,-1, 1).prj(invPrjView);
        frustumCorners[5].set( 1,-1, 1).prj(invPrjView);
        frustumCorners[6].set( 1, 1, 1).prj(invPrjView);
        frustumCorners[7].set(-1, 1, 1).prj(invPrjView);

        /* Update the frustum's clipping plane normal and d values. */
        frustumSetClippingPlane(0, frustumCorners[1], frustumCorners[0], frustumCorners[2]); // near
        frustumSetClippingPlane(1, frustumCorners[4], frustumCorners[5], frustumCorners[7]); // far
        frustumSetClippingPlane(2, frustumCorners[0], frustumCorners[4], frustumCorners[3]); // left
        frustumSetClippingPlane(3, frustumCorners[5], frustumCorners[1], frustumCorners[6]); // right
        frustumSetClippingPlane(4, frustumCorners[2], frustumCorners[3], frustumCorners[6]); // top
        frustumSetClippingPlane(5, frustumCorners[4], frustumCorners[0], frustumCorners[1]); // bottom
    }

    public boolean frustumIntersectsSphere(final Vector3 center, final float r) {
        for (int i = 0; i < 6; i++) {
            float signedDistance = frustumNormals[i].x * center.x + frustumNormals[i].y * center.y + frustumNormals[i].z * center.z + frustumPlaneDs[i];
            float diff = signedDistance + r;
            if (diff < 0) return false;
        }
        return true;
    }

    private void frustumSetClippingPlane(int i, Vector3 point1, Vector3 point2, Vector3 point3) {
        this.frustumNormals[i].set(point1).sub(point2).crs(point2.x - point3.x, point2.y - point3.y, point2.z - point3.z).nor();
        this.frustumPlaneDs[i] = -1 * Vector3.dot(point1, this.frustumNormals[i]);
    }

}