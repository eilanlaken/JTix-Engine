package com.heavybox.jtix.ecs_2;

public interface ComponentSignals extends Component {

    @Override
    default int getBitmask() {
        return Type.AUDIO.bitmask;
    }

}
