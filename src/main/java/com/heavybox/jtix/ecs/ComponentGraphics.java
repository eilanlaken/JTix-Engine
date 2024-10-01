package com.heavybox.jtix.ecs;

public interface ComponentGraphics extends Component {

    @Override
    default int getBitmask() {
        return Type.GRAPHICS.bitmask;
    }

}
