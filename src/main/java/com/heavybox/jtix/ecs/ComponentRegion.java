package com.heavybox.jtix.ecs;

public interface ComponentRegion extends Component {

    @Override
    default int getBitmask() {
        return Type.AUDIO.bitmask;
    }

}