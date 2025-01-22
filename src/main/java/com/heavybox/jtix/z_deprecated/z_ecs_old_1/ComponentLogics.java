package com.heavybox.jtix.z_deprecated.z_ecs_old_1;

public interface ComponentLogics extends Component {

    @Override
    default int getBitmask() {
        return Type.AUDIO.bitmask;
    }

}
