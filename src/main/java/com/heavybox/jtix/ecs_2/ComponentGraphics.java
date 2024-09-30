package com.heavybox.jtix.ecs_2;

public interface ComponentGraphics extends Component {

    @Override
    default int getBitmask() {
        return Type.GRAPHICS.bitmask;
    }

}
