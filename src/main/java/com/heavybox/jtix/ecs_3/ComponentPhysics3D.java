package com.heavybox.jtix.ecs_3;

public interface ComponentPhysics3D extends ComponentPhysics {

    @Override
    default int getBitmask() {
        return Type.PHYSICS.bitmask;
    }

}
