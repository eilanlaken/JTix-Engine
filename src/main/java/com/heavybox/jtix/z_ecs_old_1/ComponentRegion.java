package com.heavybox.jtix.z_ecs_old_1;

public interface ComponentRegion extends Component {

    @Override
    default int getBitmask() {
        return Type.AUDIO.bitmask;
    }

}
