package com.heavybox.jtix.ecs;

public interface ComponentLogics extends Component {

    @Override
    default int getBitmask() {
        return Type.AUDIO.bitmask;
    }

}
