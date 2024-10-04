package com.heavybox.jtix.ecs_2;

public interface ComponentRender extends Component {

    @Override
    default int getBitmask() {
        return Type.RENDER.bitmask;
    }

}
