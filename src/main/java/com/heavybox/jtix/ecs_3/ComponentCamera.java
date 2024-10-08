package com.heavybox.jtix.ecs_3;

public interface ComponentCamera extends Component {

    @Override
    default int getBitmask() {
        return Type.CAMERA.bitmask;
    }

}
