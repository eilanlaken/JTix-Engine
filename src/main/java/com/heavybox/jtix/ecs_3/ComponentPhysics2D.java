package com.heavybox.jtix.ecs_3;

public interface ComponentPhysics2D extends ComponentPhysics {

    @Override
    default int getBitmask() {
        return Type.PHYSICS.bitmask;
    }

}
