package com.heavybox.jtix.z_ecs_old_2;

public interface ComponentRegion extends Component {

    @Override
    default int getBitmask() {
        return Type.AUDIO.bitmask;
    }

}
