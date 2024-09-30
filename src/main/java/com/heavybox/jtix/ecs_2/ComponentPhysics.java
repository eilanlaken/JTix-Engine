package com.heavybox.jtix.ecs_2;

public interface ComponentPhysics extends Component {

    @Override
    default int getBitmask() {
        return Type.PHYSICS.bitmask;
    }

}
