package com.heavybox.jtix.ecs;

public interface ComponentSignals extends Component {

    @Override
    default int getBitmask() {
        return Type.AUDIO.bitmask;
    }

}
