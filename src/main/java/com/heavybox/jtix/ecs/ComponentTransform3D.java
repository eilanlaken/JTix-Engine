package com.heavybox.jtix.ecs;

import com.heavybox.jtix.math.Matrix4x4;

public class ComponentTransform3D extends Matrix4x4 implements ComponentTransform {

    // TODO
    ComponentTransform3D parent;
    ComponentTransform3D world;

    public ComponentTransform3D() {
        super();
    }

    public ComponentTransform3D(final ComponentTransform3D other) {
        super(other);
    }

    public ComponentTransform3D(float x, float y, float z, float degX, float degY, float degZ, float sclX, float sclY, float sclZ) {
        setToPositionEulerScaling(x, y, z, degX, degY, degZ, sclX, sclY, sclZ);
    }

    @Override
    public ComponentTransform3D getWorld() {
        return world == null ? this : world;
    }

}
