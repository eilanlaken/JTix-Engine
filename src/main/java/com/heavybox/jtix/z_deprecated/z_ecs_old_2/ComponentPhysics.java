package com.heavybox.jtix.z_deprecated.z_ecs_old_2;

public interface ComponentPhysics extends Component {

    @Override
    default int getBitmask() {
        return Type.PHYSICS.bitmask;
    }

}
