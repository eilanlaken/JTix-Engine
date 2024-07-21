package com.heavybox.jtix.shape;

import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;

@Deprecated
public interface Shape3D_old {

    default boolean contains(final Vector3 point) {
        return contains(point.x, point.y, point.z);
    }
    boolean contains(float x, float y, float z);
    float getVolume();
    float getSurfaceArea();
    void update(Matrix4x4 m);

}
