package com.heavybox.jtix.ecs_3;

public interface ComponentTransform extends Component {

    @Override
    default int getBitmask() {
        return Type.TRANSFORM.bitmask;
    }

}
