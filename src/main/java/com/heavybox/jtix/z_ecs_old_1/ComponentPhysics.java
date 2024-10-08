package com.heavybox.jtix.z_ecs_old_1;

public interface ComponentPhysics extends Component {

    @Override
    default int getBitmask() {
        return Type.PHYSICS.bitmask;
    }

}
