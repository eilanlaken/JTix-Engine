package com.heavybox.jtix.ecs_3;

public interface ComponentRegion extends Component {

    @Override
    default int getBitmask() {
        return Type.AUDIO.bitmask;
    }

}
