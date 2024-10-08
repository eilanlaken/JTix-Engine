package com.heavybox.jtix.z_ecs_old_1;

public interface ComponentGraphics extends Component {

    @Override
    default int getBitmask() {
        return Type.GRAPHICS.bitmask;
    }

}
