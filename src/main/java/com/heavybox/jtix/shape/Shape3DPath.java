package com.heavybox.jtix.shape;

import com.heavybox.jtix.math.Matrix4x4;

// TODO: redo entire Shape2D
public class Shape3DPath implements Shape3D_old {

    @Override
    public boolean contains(float x, float y, float z) {
        return false;
    }

    @Override
    public float getVolume() {
        return 0;
    }

    @Override
    public float getSurfaceArea() {
        return 0;
    }

    @Override
    public void update(Matrix4x4 m) {

    }
}
